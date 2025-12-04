package com.example.rodbalek_frontend.data.model.register

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val last_name: String,
    val first_name: String,
    val phone: String,
    val role: String = "CITOYEN"
)
