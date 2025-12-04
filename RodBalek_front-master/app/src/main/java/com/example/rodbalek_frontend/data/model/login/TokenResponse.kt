package com.example.rodbalek_frontend.data.model.login

data class TokenResponse(
    val access: String,
    val refresh: String,
    val user_id : Int
)