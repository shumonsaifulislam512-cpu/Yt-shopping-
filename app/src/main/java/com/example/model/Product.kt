package com.example.model

data class Product(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Double,
    val reviewCount: Int,
    val tags: List<String>,
    val description: String,
    val highlights: List<String>,
    val specs: Map<String, String>,
    val imageUrl: String,
    val colors: List<String> = listOf("Midnight Black", "Platinum Silver", "Warm Amber"),
    val inStock: Boolean = true,
    val badge: String? = null,
    val styleVibe: String = "Minimalist"
) {
    val discountPercent: Int?
        get() = if (originalPrice != null && originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else null
}

data class PersonalizedMatch(
    val product: Product,
    val matchScore: Int, // 0 - 100
    val matchReason: String
)
