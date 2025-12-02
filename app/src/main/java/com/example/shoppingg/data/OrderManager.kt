package com.example.shoppingg.data

import com.example.shoppingg.ui.orders.Order
import com.example.shoppingg.ui.checkout.tempOrder

object OrderManager {

    private val orderList = mutableListOf<Order>()
    private var tempOrder: tempOrder? = null

    fun setTempOrder(temp: tempOrder) {
        tempOrder = temp
    }
    fun getTempOrder(): tempOrder? = tempOrder
    fun clearTempOrder() { tempOrder = null }

    fun addOrder(order: Order) {
        orderList.add(order)
    }

    fun getOrders(): List<Order> {
        return orderList.toList()
    }

    fun clear() {
        orderList.clear()
    }
}
