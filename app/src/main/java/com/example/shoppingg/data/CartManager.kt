package com.example.shoppingg.data

import com.example.shoppingg.ui.models.CartItem
import com.example.shoppingg.ui.models.Product

object CartManager {
    private val _cartItems = mutableListOf<CartItem>()

    val cartItems: List<CartItem>
        get() = _cartItems

    fun addItem(product: Product, quantity: Int = 1) {
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

    fun updateQuantity(product: Product, quantity:Int){
        val item = _cartItems.find { it.product == product }
        if (item != null) {
            item.quantity = quantity
            if (quantity <= 0) _cartItems.remove(item)
        }
    }
}