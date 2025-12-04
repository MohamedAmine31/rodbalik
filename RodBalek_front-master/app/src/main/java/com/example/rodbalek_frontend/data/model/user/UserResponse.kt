package com.example.rodbalek_frontend.data.model.user

data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val nom: String,
    val prenom: String,
    val phone: String
)
