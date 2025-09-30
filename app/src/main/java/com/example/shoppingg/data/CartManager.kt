package com.example.shoppingg.data

import android.os.Looper
import com.example.shoppingg.ui.models.CartItem
import com.example.shoppingg.ui.models.Product

object CartManager {
    // danh sách nội bộ (private)
    private val _cartItems = mutableListOf<CartItem>()

    // cho phép đọc từ ngoài (read-only)
    val cartItems: List<CartItem>
        get() = _cartItems

    fun addItem(product: Product, quantity: Int = 1) {
        // nếu sản phẩm đã có thì tăng số lượng
        val existing = _cartItems.find { it.product.name == product.name }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            _cartItems.add(CartItem(product, quantity))
        }
    }

    fun clear() {
        _cartItems.clear()
    }

    fun getItemCount(): Int = _cartItems.sumOf { it.quantity }

    fun getTotalPrice(): Int =
        _cartItems.sumOf { it.product.price * it.quantity }

    fun removeItem(product: Product) {
        val iterator = _cartItems.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.product == product) {
                iterator.remove()
                break
            }
        }
    }
}