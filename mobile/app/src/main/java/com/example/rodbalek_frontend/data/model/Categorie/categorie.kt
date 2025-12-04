package com.example.rodbalek_frontend.data.model.Categorie


data class categorie(
    val name: String,
    val description: String?,
    val icon: String?,
    val color: String?,
    val is_active: Boolean
)

data class ApiResponseCategories(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<categorie>
)

data class ApiResponse(
    val count: Int,
    val results: List<categorie>
)
data class Category(
    val id: Int,
    val name: String
)