package com.example.doan.ui.models

import java.io.Serializable

data class Product(
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val image: Int
) : Serializable
