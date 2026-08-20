package com.example.shoppingg.models

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val categoryId: Int
)