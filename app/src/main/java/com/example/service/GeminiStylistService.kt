package com.example.service

import com.example.BuildConfig
import com.example.data.SampleProducts
import com.example.model.Product
import com.example.model.UserTasteProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiStylistService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askStylist(
        userQuery: String,
        profile: UserTasteProfile,
        currentProduct: Product? = null
    ): StylistResponse = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        // Prepare catalog context
        val catalogContext = SampleProducts.allProducts.joinToString("\n") { p ->
            "- ID: ${p.id}, Title: ${p.title}, Price: $${p.price}, Category: ${p.category}, Vibe: ${p.styleVibe}, Tags: ${p.tags.joinToString(", ")}, Highlights: ${p.highlights.take(2).joinToString("; ")}"
        }

        val systemPrompt = """
            You are Lumina's premier AI Personal Shopper & Stylist.
            User Profile:
            - Name: ${profile.userName}
            - Preferred Categories: ${profile.preferredCategories.joinToString(", ")}
            - Style Vibe: ${profile.preferredVibes.joinToString(", ")}
            - Budget Preference: ${profile.budgetTier.label} (${profile.budgetTier.rangeHint})
            ${if (currentProduct != null) "Currently viewing product: ${currentProduct.title} ($${currentProduct.price}, ${currentProduct.category})" else ""}

            Available store catalog:
            $catalogContext

            Instructions:
            Provide a warm, stylish, personalized recommendation advice (under 90 words).
            Explicitly mention 1 to 3 specific product IDs from the catalog that best match their taste or inquiry.
            Format your final response strictly as JSON:
            {
              "message": "Your conversational advice here...",
              "recommendedProductIds": ["id1", "id2"]
            }
        """.trimIndent()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                
                val reqBodyJson = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val turn = JSONObject().apply {
                            val parts = JSONArray().apply {
                                put(JSONObject().put("text", "$systemPrompt\n\nUser Question: $userQuery"))
                            }
                            put("parts", parts)
                        }
                        put(turn)
                    }
                    put("contents", contents)
                    val genConfig = JSONObject().apply {
                        put("temperature", 0.7)
                        put("responseMimeType", "application/json")
                    }
                    put("generationConfig", genConfig)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(reqBodyJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string().orEmpty()
                    val respObj = JSONObject(bodyString)
                    val candidates = respObj.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")

                    if (!text.isNullOrBlank()) {
                        val parsed = parseJsonResponse(text)
                        if (parsed != null) return@withContext parsed
                    }
                }
            } catch (_: Exception) {
                // Fall back gracefully to local expert advisor
            }
        }

        // Local Smart Personalization Fallback
        return@withContext generateLocalStylistResponse(userQuery, profile, currentProduct)
    }

    private fun parseJsonResponse(raw: String): StylistResponse? {
        return try {
            val clean = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = JSONObject(clean)
            val msg = obj.optString("message", "")
            val arr = obj.optJSONArray("recommendedProductIds")
            val ids = mutableListOf<String>()
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    ids.add(arr.getString(i))
                }
            }
            if (msg.isNotBlank()) StylistResponse(msg, ids) else null
        } catch (_: Exception) {
            null
        }
    }

    private fun generateLocalStylistResponse(
        query: String,
        profile: UserTasteProfile,
        currentProduct: Product?
    ): StylistResponse {
        val q = query.lowercase()
        return when {
            q.contains("desk") || q.contains("work") || q.contains("office") -> {
                StylistResponse(
                    message = "For an elevated ${profile.preferredVibes.firstOrNull() ?: "Minimalist"} workspace, the NeoGlow Smart Lamp balances natural circadian lighting while wireless charging your phone. Pair it with the Aura Pro Headphones for focused audio immersion.",
                    recommendedProductIds = listOf("lumina_smart_desk_lamp", "lumina_anc_pro")
                )
            }
            q.contains("travel") || q.contains("commute") || q.contains("bag") -> {
                StylistResponse(
                    message = "For seamless travel, the AeroFlex Modular Pack keeps your devices weather-shielded, and the AuraSnap 10,000mAh Magnetic Power Core ensures you never run low on battery during transit.",
                    recommendedProductIds = listOf("aeroflex_urban_pack", "lumina_magsafe_powerbank")
                )
            }
            q.contains("gift") || q.contains("coffee") || q.contains("morning") -> {
                StylistResponse(
                    message = "A favorite recommendation for refined living: the Nordic Artisan Ceramic Pour-Over brings Danish minimalism to morning coffee, perfectly matching a cozy aesthetic.",
                    recommendedProductIds = listOf("barista_pour_over_set")
                )
            }
            currentProduct != null -> {
                val complementary = SampleProducts.allProducts.filter { it.id != currentProduct.id }
                    .sortedByDescending { it.rating }
                    .take(2)
                StylistResponse(
                    message = "Since you're exploring the ${currentProduct.title}, here are two complementary picks that match your ${profile.budgetTier.label.lowercase()} budget and aesthetic.",
                    recommendedProductIds = complementary.map { it.id }
                )
            }
            else -> {
                val topCurated = PersonalizationEngine.getCuratedPicks(SampleProducts.allProducts, profile, 2)
                StylistResponse(
                    message = "Based on your ${profile.preferredVibes.joinToString(" & ")} preference, I've curated these high-affinity picks with top user satisfaction ratings.",
                    recommendedProductIds = topCurated.map { it.product.id }
                )
            }
        }
    }
}

data class StylistResponse(
    val message: String,
    val recommendedProductIds: List<String>
)
