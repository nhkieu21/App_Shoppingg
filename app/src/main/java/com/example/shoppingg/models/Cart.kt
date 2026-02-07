package com.example.shoppingg.models

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

data class AddToCartRequest(
    val productId: Int,
    val quantity: Int
)