package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LuminaRepository
import com.example.data.SampleProducts
import com.example.data.local.LuminaDatabase
import com.example.model.BudgetTier
import com.example.model.CardData
import com.example.model.CartItem
import com.example.model.DeliverySpeed
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.PaymentProcessingState
import com.example.model.PaymentType
import com.example.model.PersonalizedMatch
import com.example.model.Product
import com.example.model.ShippingAddress
import com.example.model.TrackingStep
import com.example.model.UserTasteProfile
import com.example.service.GeminiStylistService
import com.example.service.PersonalizationEngine
import com.example.service.StylistResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class LuminaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LuminaRepository(LuminaDatabase.getInstance(application))

    val allProducts = SampleProducts.allProducts

    val userProfile: StateFlow<UserTasteProfile> = repository.userTasteProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserTasteProfile()
        )

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wishlistIds: StateFlow<Set<String>> = repository.wishlistProductIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val orders: StateFlow<List<Order>> = repository.orders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI state
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Checkout & Payment State
    private val _deliverySpeed = MutableStateFlow(DeliverySpeed.STANDARD)
    val deliverySpeed: StateFlow<DeliverySpeed> = _deliverySpeed.asStateFlow()

    private val _paymentType = MutableStateFlow(PaymentType.GOOGLE_PAY)
    val paymentType: StateFlow<PaymentType> = _paymentType.asStateFlow()

    private val _cardData = MutableStateFlow(
        CardData(
            number = "4532718992014242",
            holderName = "Alex Rivera",
            expiry = "08/28",
            cvv = "842"
        )
    )
    val cardData: StateFlow<CardData> = _cardData.asStateFlow()

    private val _upiId = MutableStateFlow("alex.rivera@okaxis")
    val upiId: StateFlow<String> = _upiId.asStateFlow()

    private val _promoCode = MutableStateFlow<String?>(null)
    val promoCode: StateFlow<String?> = _promoCode.asStateFlow()

    private val _promoDiscountRate = MutableStateFlow(0.0)
    val promoDiscountRate: StateFlow<Double> = _promoDiscountRate.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentProcessingState>(PaymentProcessingState.Idle)
    val paymentState: StateFlow<PaymentProcessingState> = _paymentState.asStateFlow()

    private val _confirmedOrder = MutableStateFlow<Order?>(null)
    val confirmedOrder: StateFlow<Order?> = _confirmedOrder.asStateFlow()

    // AI Stylist State
    private val _aiStylistResponse = MutableStateFlow<StylistResponse?>(null)
    val aiStylistResponse: StateFlow<StylistResponse?> = _aiStylistResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Dynamic Personalized Carousels
    val curatedRecommendations: StateFlow<List<PersonalizedMatch>> = userProfile.combine(_searchQuery) { profile, _ ->
        PersonalizationEngine.getCuratedPicks(allProducts, profile, limit = 5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val becauseYouExplored: StateFlow<Pair<Product?, List<PersonalizedMatch>>> = userProfile.combine(_selectedProduct) { profile, selProduct ->
        val lastViewedId = selProduct?.id ?: profile.viewedProductIds.firstOrNull() ?: allProducts.first().id
        PersonalizationEngine.getBecauseYouViewed(allProducts, lastViewedId, profile, limit = 4)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(null, emptyList()))

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
        if (product != null) {
            viewModelScope.launch {
                repository.recordProductView(product.id, userProfile.value)
            }
        }
    }

    fun addToCart(product: Product, color: String? = null, qty: Int = 1) {
        viewModelScope.launch {
            val selectedColor = color ?: product.colors.firstOrNull() ?: "Default"
            val existing = cartItems.value.find { it.product.id == product.id }
            val newQty = (existing?.quantity ?: 0) + qty
            repository.addToCart(product.id, selectedColor, newQty)
        }
    }

    fun updateCartQty(productId: String, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, qty)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isWishlisted = wishlistIds.value.contains(product.id)
            repository.toggleWishlist(product.id, isWishlisted)
        }
    }

    fun applyPromoCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        return when (trimmed) {
            "LUMINA20" -> {
                _promoCode.value = "LUMINA20"
                _promoDiscountRate.value = 0.20
                true
            }
            "FREESHIP" -> {
                _promoCode.value = "FREESHIP"
                _deliverySpeed.value = DeliverySpeed.STANDARD
                _promoDiscountRate.value = 0.10
                true
            }
            "VIP15" -> {
                _promoCode.value = "VIP15"
                _promoDiscountRate.value = 0.15
                true
            }
            else -> false
        }
    }

    fun removePromoCode() {
        _promoCode.value = null
        _promoDiscountRate.value = 0.0
    }

    fun setDeliverySpeed(speed: DeliverySpeed) {
        _deliverySpeed.value = speed
    }

    fun setPaymentType(type: PaymentType) {
        _paymentType.value = type
    }

    fun updateCardData(newCard: CardData) {
        _cardData.value = newCard
    }

    fun updateUpiId(id: String) {
        _upiId.value = id
    }

    fun updateTasteProfile(
        name: String,
        categories: Set<String>,
        vibes: Set<String>,
        budget: BudgetTier
    ) {
        viewModelScope.launch {
            val newProfile = userProfile.value.copy(
                userName = name,
                preferredCategories = categories,
                preferredVibes = vibes,
                budgetTier = budget
            )
            repository.saveTasteProfile(newProfile)
        }
    }

    fun askAiStylist(userQuery: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val response = GeminiStylistService.askStylist(
                userQuery = userQuery,
                profile = userProfile.value,
                currentProduct = _selectedProduct.value
            )
            _aiStylistResponse.value = response
            _isAiLoading.value = false
        }
    }

    fun executeSeamlessCheckout(onSuccess: (Order) -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val subtotal = items.sumOf { it.totalPrice }
            val discount = subtotal * _promoDiscountRate.value
            val tax = (subtotal - discount) * 0.0825
            val deliveryCost = _deliverySpeed.value.cost
            val total = subtotal - discount + tax + deliveryCost

            // Step 1: Encrypting credentials
            _paymentState.value = PaymentProcessingState.Processing(
                step = "🔒 Encrypting payment tokens (256-bit AES)...",
                progress = 0.25f
            )
            delay(500)

            // Step 2: Gateway authorization
            val gatewayName = when (_paymentType.value) {
                PaymentType.GOOGLE_PAY -> "Google Pay Instant Gateway"
                PaymentType.CREDIT_DEBIT_CARD -> "${_cardData.value.cardBrand} Secure Network"
                PaymentType.UPI -> "NPCI Instant UPI Switch"
                PaymentType.PAY_LATER -> "Lumina Split 4-Pay Engine"
            }
            _paymentState.value = PaymentProcessingState.Processing(
                step = "⚡ Authorizing with $gatewayName...",
                progress = 0.65f
            )
            delay(600)

            // Step 3: Bank approval & fraud validation
            _paymentState.value = PaymentProcessingState.Processing(
                step = "✨ Finalizing authorization & verified receipt...",
                progress = 0.90f
            )
            delay(400)

            val orderId = "LUM-" + (10000..99999).random()
            val txnRef = "TXN-" + UUID.randomUUID().toString().take(10).uppercase()

            val orderItems = items.map {
                OrderItem(
                    productId = it.product.id,
                    title = it.product.title,
                    price = it.product.price,
                    quantity = it.quantity,
                    color = it.selectedColor,
                    imageUrl = it.product.imageUrl
                )
            }

            val tracking = listOf(
                TrackingStep("Payment Authorized & Verified", "Transaction $txnRef approved", "Just now", true, false),
                TrackingStep("Order Received at Fulfillment Center", "Items inspected and boxed in eco packaging", "Next 2 hours", true, true),
                TrackingStep("Courier Handover", "Dispatched via Express Courier", "Tomorrow", false, false),
                TrackingStep("Delivered to Doorstep", "Signed delivery at ${ShippingAddress().formatted}", _deliverySpeed.value.eta, false, false)
            )

            val order = Order(
                id = orderId,
                timestamp = System.currentTimeMillis(),
                items = orderItems,
                subtotal = subtotal,
                discount = discount,
                tax = tax,
                deliveryCost = deliveryCost,
                total = total,
                paymentType = _paymentType.value,
                status = OrderStatus.CONFIRMED,
                shippingAddress = ShippingAddress(),
                transactionRef = txnRef,
                trackingSteps = tracking
            )

            // Persist order in Room and clear cart
            repository.saveOrder(order)
            repository.clearCart()

            _confirmedOrder.value = order
            _paymentState.value = PaymentProcessingState.Success(orderId, txnRef)

            delay(300)
            onSuccess(order)
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentProcessingState.Idle
    }
}
