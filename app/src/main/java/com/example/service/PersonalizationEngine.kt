package com.example.service

import com.example.model.BudgetTier
import com.example.model.PersonalizedMatch
import com.example.model.Product
import com.example.model.UserTasteProfile

object PersonalizationEngine {

    fun evaluateProduct(
        product: Product,
        profile: UserTasteProfile
    ): PersonalizedMatch {
        var score = 55

        // Category affinity
        val categoryMatch = profile.preferredCategories.contains(product.category)
        if (categoryMatch) score += 20

        // Style vibe affinity
        val vibeMatch = profile.preferredVibes.any { vibe ->
            product.tags.any { it.equals(vibe, ignoreCase = true) } ||
            product.styleVibe.equals(vibe, ignoreCase = true)
        }
        if (vibeMatch) score += 15

        // Budget tier alignment
        val budgetMatch = when (profile.budgetTier) {
            BudgetTier.VALUE -> product.price <= 75.0
            BudgetTier.BALANCED -> product.price in 40.0..220.0
            BudgetTier.PREMIUM -> product.price >= 100.0
        }
        if (budgetMatch) score += 10

        // Rating boost
        if (product.rating >= 4.8) score += 5

        // Recently viewed boost (if user checked related items)
        if (profile.viewedProductIds.contains(product.id)) {
            score -= 5 // slight decrease so we don't only show what they already opened
        }

        val clampedScore = score.coerceIn(68, 99)

        // Generate tailored explanation text
        val reasons = mutableListOf<String>()
        if (vibeMatch) {
            reasons.add("Fits your ${product.styleVibe} aesthetic")
        }
        if (categoryMatch) {
            reasons.add("Top pick in ${product.category}")
        }
        if (budgetMatch) {
            reasons.add("Matches your ${profile.budgetTier.label.lowercase()} range")
        }
        if (product.rating >= 4.8) {
            reasons.add("Highly rated (${product.rating}★)")
        }

        val reasonText = if (reasons.isNotEmpty()) {
            reasons.take(2).joinToString(" & ")
        } else {
            "Curated for you based on current shopping trends"
        }

        return PersonalizedMatch(
            product = product,
            matchScore = clampedScore,
            matchReason = "$clampedScore% Match • $reasonText"
        )
    }

    fun getCuratedPicks(
        products: List<Product>,
        profile: UserTasteProfile,
        limit: Int = 5
    ): List<PersonalizedMatch> {
        return products.map { evaluateProduct(it, profile) }
            .sortedByDescending { it.matchScore }
            .take(limit)
    }

    fun getBecauseYouViewed(
        products: List<Product>,
        viewedProductId: String?,
        profile: UserTasteProfile,
        limit: Int = 4
    ): Pair<Product?, List<PersonalizedMatch>> {
        val viewedProduct = products.find { it.id == viewedProductId } ?: products.firstOrNull()
        if (viewedProduct == null) return Pair(null, emptyList())

        val related = products
            .filter { it.id != viewedProduct.id }
            .map { evaluateProduct(it, profile) }
            .sortedByDescending { match ->
                var boost = match.matchScore
                if (match.product.category == viewedProduct.category) boost += 15
                if (match.product.styleVibe == viewedProduct.styleVibe) boost += 10
                boost
            }
            .take(limit)

        return Pair(viewedProduct, related)
    }

    fun getSmartBundle(
        primaryProduct: Product,
        allProducts: List<Product>
    ): Product? {
        // Find a complementary product in tech/wearables/home
        return allProducts.firstOrNull { it.id != primaryProduct.id && it.price < primaryProduct.price }
    }
}
