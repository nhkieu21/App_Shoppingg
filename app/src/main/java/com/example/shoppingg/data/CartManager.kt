package com.example.shoppingg.data

import com.example.shoppingg.network.RetrofitClient
import com.example.shoppingg.ui.models.CartItem
import com.example.shoppingg.ui.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CartManager {
    private val _cartItems = mutableListOf<CartItem>()

    val cartItems: List<CartItem>
        get() = _cartItems
    fun setCart(items: List<CartItem>) {
        _cartItems.clear()
        _cartItems.addAll(items)
    }
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

//    suspend fun syncCartWithServer(token: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = RetrofitClient.apiService.getCart("Bearer $token")
//                if (response.isSuccessful && response.body() != null) {
//                    setCart(response.body()!!)
//                    true
//                } else {
//                    false
//                }
//            } catch (e: Exception) {
//                false
//            }
//        }
//    }
}