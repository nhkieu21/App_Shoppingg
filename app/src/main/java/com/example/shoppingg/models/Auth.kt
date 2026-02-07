package com.example.shoppingg.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val message: String,
    val user: UserInfo?
)

data class UserInfo(
    val id: Int,
    val name: String,
    val email: String
)