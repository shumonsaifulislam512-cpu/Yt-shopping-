package com.example.model

data class CartItem(
    val product: Product,
    val quantity: Int,
    val selectedColor: String
) {
    val totalPrice: Double
        get() = product.price * quantity
}

enum class OrderStatus(val label: String) {
    CONFIRMED("Order Confirmed"),
    PREPARING("Preparing Package"),
    SHIPPED("Shipped & In Transit"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered")
}

data class TrackingStep(
    val title: String,
    val description: String,
    val time: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class OrderItem(
    val productId: String,
    val title: String,
    val price: Double,
    val quantity: Int,
    val color: String,
    val imageUrl: String
)

data class Order(
    val id: String = "LUM-00000",
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val deliveryCost: Double = 0.0,
    val total: Double = 0.0,
    val paymentType: PaymentType = PaymentType.GOOGLE_PAY,
    val status: OrderStatus = OrderStatus.CONFIRMED,
    val shippingAddress: ShippingAddress = ShippingAddress(),
    val transactionRef: String = "TXN-DEFAULT",
    val trackingSteps: List<TrackingStep> = emptyList()
)
