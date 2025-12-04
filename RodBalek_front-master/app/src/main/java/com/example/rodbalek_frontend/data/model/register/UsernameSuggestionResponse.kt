package com.example.rodbalek_frontend.data.model.register

data class UsernameSuggestionResponse(
    val success: Boolean,
    val username: String,
    val available: Boolean,
    val suggestions: List<String>,
    val message: String
)