package com.example.shoppingg.ui.models

import java.io.Serializable
import java.text.NumberFormat
import java.util.Locale

data class Product(
    val name: String,
    val category: String,
    val price: Int,
    val description: String,
    val image: String
) : Serializable {
    val priceFormatted: String
        get() {
            val vn = Locale("vi", "VN")
            val formatter = NumberFormat.getInstance(vn)
            return formatter.format(price) + "₫"
        }
}