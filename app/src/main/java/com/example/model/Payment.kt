package com.example.model

enum class PaymentType(val displayName: String, val subtitle: String) {
    GOOGLE_PAY("Google Pay", "Fast, 1-tap encrypted checkout"),
    CREDIT_DEBIT_CARD("Credit / Debit Card", "Visa, Mastercard, Amex supported"),
    UPI("Instant UPI", "Pay via GPay, PhonePe, or VPA"),
    PAY_LATER("Lumina Split", "Pay in 4 interest-free installments")
}

data class CardData(
    val number: String = "",
    val holderName: String = "",
    val expiry: String = "",
    val cvv: String = "",
    val saveCard: Boolean = true
) {
    val cardBrand: String
        get() = when {
            number.startsWith("4") -> "Visa"
            number.startsWith("51") || number.startsWith("52") || number.startsWith("53") ||
            number.startsWith("54") || number.startsWith("55") -> "Mastercard"
            number.startsWith("34") || number.startsWith("37") -> "Amex"
            else -> "Card"
        }

    val maskedNumber: String
        get() = if (number.length >= 4) {
            "•••• •••• •••• " + number.takeLast(4)
        } else "•••• •••• •••• ••••"
}

enum class DeliverySpeed(
    val title: String,
    val eta: String,
    val cost: Double
) {
    STANDARD("Standard Delivery", "3-5 business days", 0.0),
    EXPRESS("Priority Express", "1-2 business days", 9.99),
    VIP_SAME_DAY("VIP Same-Day", "Today by 8:00 PM", 19.99)
}

data class ShippingAddress(
    val label: String = "Home",
    val recipientName: String = "Alex Rivera",
    val street: String = "742 Evergreen Terrace, Apt 4B",
    val city: String = "San Francisco",
    val state: String = "CA",
    val zipCode: String = "94107",
    val phone: String = "+1 (415) 555-0198"
) {
    val formatted: String
        get() = "$street, $city, $state $zipCode"
}

sealed class PaymentProcessingState {
    object Idle : PaymentProcessingState()
    data class Processing(val step: String, val progress: Float) : PaymentProcessingState()
    data class Success(val orderId: String, val txnRef: String) : PaymentProcessingState()
    data class Failed(val reason: String) : PaymentProcessingState()
}
