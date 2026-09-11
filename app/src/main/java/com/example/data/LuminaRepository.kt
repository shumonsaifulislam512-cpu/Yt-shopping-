package com.example.data

import com.example.data.local.CartEntity
import com.example.data.local.LuminaDatabase
import com.example.data.local.OrderEntity
import com.example.data.local.PreferenceEntity
import com.example.data.local.WishlistEntity
import com.example.model.BudgetTier
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.PaymentType
import com.example.model.ShippingAddress
import com.example.model.TrackingStep
import com.example.model.UserTasteProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LuminaRepository(private val database: LuminaDatabase) {

    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val orderDao = database.orderDao()
    private val preferenceDao = database.preferenceDao()

    val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems().map { entities ->
        entities.mapNotNull { entity ->
            val product = SampleProducts.getById(entity.productId)
            if (product != null) {
                CartItem(
                    product = product,
                    quantity = entity.quantity,
                    selectedColor = entity.selectedColor
                )
            } else null
        }
    }

    val wishlistProductIds: Flow<Set<String>> = wishlistDao.getAllWishlistItems().map { entities ->
        entities.map { it.productId }.toSet()
    }

    val orders: Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { entity ->
            deserializeOrder(entity)
        }
    }

    val userTasteProfile: Flow<UserTasteProfile> = preferenceDao.getAllPreferences().map { prefs ->
        val map = prefs.associate { it.key to it.value }
        val name = map["user_name"] ?: "Alex"
        val categories = map["pref_categories"]?.split(",")?.filter { it.isNotBlank() }?.toSet()
            ?: setOf("Audio & Tech", "Wearables")
        val vibes = map["pref_vibes"]?.split(",")?.filter { it.isNotBlank() }?.toSet()
            ?: setOf("Minimalist", "Tech & Futurism", "Nordic Cozy")
        val budgetTier = try {
            BudgetTier.valueOf(map["budget_tier"] ?: BudgetTier.BALANCED.name)
        } catch (_: Exception) {
            BudgetTier.BALANCED
        }
        val viewedIds = map["viewed_ids"]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

        UserTasteProfile(
            userName = name,
            preferredCategories = categories,
            preferredVibes = vibes,
            budgetTier = budgetTier,
            viewedProductIds = viewedIds
        )
    }

    suspend fun addToCart(productId: String, color: String, quantity: Int = 1) = withContext(Dispatchers.IO) {
        cartDao.insertOrUpdate(CartEntity(productId, quantity, color))
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteItem(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: String) = withContext(Dispatchers.IO) {
        cartDao.deleteItem(productId)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    suspend fun toggleWishlist(productId: String, isCurrentlyWishlisted: Boolean) = withContext(Dispatchers.IO) {
        if (isCurrentlyWishlisted) {
            wishlistDao.removeFromWishlist(productId)
        } else {
            wishlistDao.addToWishlist(WishlistEntity(productId))
        }
    }

    suspend fun recordProductView(productId: String, currentProfile: UserTasteProfile) = withContext(Dispatchers.IO) {
        val updatedList = (listOf(productId) + currentProfile.viewedProductIds.filter { it != productId }).take(8)
        preferenceDao.setPreference(PreferenceEntity("viewed_ids", updatedList.joinToString(",")))
    }

    suspend fun saveTasteProfile(profile: UserTasteProfile) = withContext(Dispatchers.IO) {
        preferenceDao.setPreference(PreferenceEntity("user_name", profile.userName))
        preferenceDao.setPreference(PreferenceEntity("pref_categories", profile.preferredCategories.joinToString(",")))
        preferenceDao.setPreference(PreferenceEntity("pref_vibes", profile.preferredVibes.joinToString(",")))
        preferenceDao.setPreference(PreferenceEntity("budget_tier", profile.budgetTier.name))
    }

    suspend fun saveOrder(order: Order) = withContext(Dispatchers.IO) {
        val itemsJsonArray = JSONArray()
        order.items.forEach { item ->
            val obj = JSONObject()
            obj.put("productId", item.productId)
            obj.put("title", item.title)
            obj.put("price", item.price)
            obj.put("quantity", item.quantity)
            obj.put("color", item.color)
            obj.put("imageUrl", item.imageUrl)
            itemsJsonArray.put(obj)
        }

        val shipObj = JSONObject()
        shipObj.put("recipientName", order.shippingAddress.recipientName)
        shipObj.put("street", order.shippingAddress.street)
        shipObj.put("city", order.shippingAddress.city)
        shipObj.put("state", order.shippingAddress.state)
        shipObj.put("zipCode", order.shippingAddress.zipCode)
        shipObj.put("phone", order.shippingAddress.phone)

        val entity = OrderEntity(
            id = order.id,
            timestamp = order.timestamp,
            subtotal = order.subtotal,
            discount = order.discount,
            tax = order.tax,
            deliveryCost = order.deliveryCost,
            total = order.total,
            paymentType = order.paymentType.name,
            status = order.status.name,
            shippingJson = shipObj.toString(),
            itemsJson = itemsJsonArray.toString(),
            txnRef = order.transactionRef
        )
        orderDao.insertOrder(entity)
    }

    private fun deserializeOrder(entity: OrderEntity): Order {
        val items = mutableListOf<OrderItem>()
        try {
            val arr = JSONArray(entity.itemsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                items.add(
                    OrderItem(
                        productId = obj.optString("productId", ""),
                        title = obj.optString("title", "Item"),
                        price = obj.optDouble("price", 0.0),
                        quantity = obj.optInt("quantity", 1),
                        color = obj.optString("color", ""),
                        imageUrl = obj.optString("imageUrl", "")
                    )
                )
            }
        } catch (_: Exception) {}

        var address = ShippingAddress()
        try {
            val obj = JSONObject(entity.shippingJson)
            address = ShippingAddress(
                recipientName = obj.optString("recipientName", "Alex"),
                street = obj.optString("street", ""),
                city = obj.optString("city", ""),
                state = obj.optString("state", ""),
                zipCode = obj.optString("zipCode", ""),
                phone = obj.optString("phone", "")
            )
        } catch (_: Exception) {}

        val paymentType = try {
            PaymentType.valueOf(entity.paymentType)
        } catch (_: Exception) {
            PaymentType.GOOGLE_PAY
        }

        val status = try {
            OrderStatus.valueOf(entity.status)
        } catch (_: Exception) {
            OrderStatus.CONFIRMED
        }

        val steps = listOf(
            TrackingStep("Order Placed & Verified", "Instant authorization successful", "Today", true, false),
            TrackingStep("Packing at Fulfillment Center", "Items inspected and boxed in eco packaging", "Within 6 hrs", true, true),
            TrackingStep("Handed to Express Courier", "Carrier scan & tracking assigned", "Tomorrow", false, false),
            TrackingStep("Out for Delivery", "Driver assigned for final drop-off", "Estimated in 2 days", false, false)
        )

        return Order(
            id = entity.id,
            timestamp = entity.timestamp,
            items = items,
            subtotal = entity.subtotal,
            discount = entity.discount,
            tax = entity.tax,
            deliveryCost = entity.deliveryCost,
            total = entity.total,
            paymentType = paymentType,
            status = status,
            shippingAddress = address,
            transactionRef = entity.txnRef,
            trackingSteps = steps
        )
    }
}
