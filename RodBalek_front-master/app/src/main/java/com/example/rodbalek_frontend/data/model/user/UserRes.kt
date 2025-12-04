package com.example.rodbalek_frontend.data.model.user

data class UserRes(
    val username: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: Int?,
    val role: String,
    val created_at: String,
    val imageUrl: String?
)
