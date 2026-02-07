package com.example.shoppingg.data

import android.content.Context
import android.content.SharedPreferences
import com.example.shoppingg.ui.models.CartItem
import com.example.shoppingg.ui.orders.Order
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_FULLNAME = "full_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        private const val KEY_PHONE = "user_phone"
        private const val KEY_ADDRESS = "user_address"
        private const val KEY_CART = "cart_items"
        private const val KEY_ORDERS = "orders"

        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    fun saveOrders(orders: List<Order>) {
        val json = Gson().toJson(orders)
        prefs.edit().putString(KEY_ORDERS, json).apply()
    }
    fun getOrders(): MutableList<Order> {
        val json = prefs.getString(KEY_ORDERS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Order>>() {}.type
        return Gson().fromJson(json, type)
    }
    fun clearOrders() {
        prefs.edit().remove(KEY_ORDERS).apply()
    }

    fun saveUser(email: String, password: String, fullName: String) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_FULLNAME, fullName)
            .apply()
    }

    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getUserPassword(): String? = prefs.getString(KEY_PASSWORD, null)
    fun getFullName(): String? = prefs.getString(KEY_FULLNAME, "")

    fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean =
        prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun saveUserPhone(phone: String) {
        prefs.edit().putString(KEY_PHONE, phone).apply()
    }

    fun getUserPhone(): String? =
        prefs.getString(KEY_PHONE, null)

    fun saveUserAddress(address: String) {
        prefs.edit().putString(KEY_ADDRESS, address).apply()
    }

    fun getUserAddress(): String? =
        prefs.getString(KEY_ADDRESS, null)

    fun clearPhoneAddress() {
        prefs.edit()
            .remove(KEY_PHONE)
            .remove(KEY_ADDRESS)
            .apply()
    }

    fun saveCart(cartItems: List<CartItem>) {
        val json = Gson().toJson(cartItems)
        prefs.edit().putString(KEY_CART, json).apply()
    }

    fun getCart(): MutableList<CartItem> {
        val json = prefs.getString(KEY_CART, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<CartItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearCart() {
        prefs.edit().remove(KEY_CART).apply()
    }
}
