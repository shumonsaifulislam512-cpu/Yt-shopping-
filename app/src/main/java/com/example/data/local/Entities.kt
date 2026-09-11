package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val selectedColor: String
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val deliveryCost: Double,
    val total: Double,
    val paymentType: String,
    val status: String,
    val shippingJson: String,
    val itemsJson: String,
    val txnRef: String
)

@Entity(tableName = "user_preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)
