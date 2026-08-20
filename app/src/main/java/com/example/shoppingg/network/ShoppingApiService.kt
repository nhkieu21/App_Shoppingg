package com.example.shoppingg.network

import com.example.shoppingg.models.*
import com.example.shoppingg.ui.orders.Order
import retrofit2.Response
import retrofit2.http.*

interface ShoppingApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") productId: Int): Response<Product>

    @GET("api/products/search")
    suspend fun searchProducts(@Query("query") query: String): Response<List<Product>>

    @GET("api/cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<List<CartItem>>

    @POST("api/cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<Unit>

    @POST("api/orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body order: Order
    ): Response<Unit>

    @GET("api/orders")
    suspend fun getUserOrders(@Header("Authorization") token: String): Response<List<Order>>
}