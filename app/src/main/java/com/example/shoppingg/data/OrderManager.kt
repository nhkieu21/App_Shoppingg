package com.example.shoppingg.data

import android.content.Context
import com.example.shoppingg.network.RetrofitClient
import com.example.shoppingg.ui.orders.Order
import com.example.shoppingg.ui.checkout.tempOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object OrderManager {

    private val orderList = mutableListOf<Order>()
    private var tempOrder: tempOrder? = null
    fun init(context: Context) {
        val session = SessionManager(context)
        orderList.clear()
        orderList.addAll(session.getOrders())
    }
    fun setTempOrder(temp: tempOrder) {
        tempOrder = temp
    }
    fun getTempOrder(): tempOrder? = tempOrder
    fun clearTempOrder() { tempOrder = null }

    fun addOrder(context: Context, order: Order) {
        orderList.add(order)
        SessionManager(context).saveOrders(orderList)
    }

    fun getOrders(): List<Order> {
        return orderList.toList()
    }

    fun clear() {
        orderList.clear()
    }

    suspend fun syncCreateOrder(token: String, order: Order): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.createOrder("Bearer $token", order)
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }
}
