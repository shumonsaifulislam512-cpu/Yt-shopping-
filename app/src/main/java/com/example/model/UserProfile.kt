package com.example.model

data class UserTasteProfile(
    val userName: String = "Alex",
    val preferredCategories: Set<String> = setOf("Audio & Tech", "Wearables"),
    val preferredVibes: Set<String> = setOf("Minimalist", "Tech & Futurism", "Nordic Cozy"),
    val budgetTier: BudgetTier = BudgetTier.BALANCED,
    val viewedProductIds: List<String> = emptyList(),
    val isPersonalizationActive: Boolean = true
)

enum class BudgetTier(val label: String, val rangeHint: String) {
    VALUE("Value Friendly", "$10 - $70"),
    BALANCED("Balanced Quality", "$50 - $200"),
    PREMIUM("Luxury & Pro", "$150 - $600+")
}
