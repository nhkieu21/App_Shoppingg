package com.example.shoppingg.data

import com.example.shoppingg.ui.orders.Order

object OrderManager {

    private val orderList = mutableListOf<Order>()

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
