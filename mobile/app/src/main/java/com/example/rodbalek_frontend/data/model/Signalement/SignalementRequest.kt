package com.example.rodbalek_frontend.data.model.Signalement

import com.example.rodbalek_frontend.data.model.Categorie.Category
import com.example.rodbalek_frontend.data.model.user.Citoyen
import com.google.gson.annotations.SerializedName

data class SignalementRequest(
    @SerializedName("Description") val Description: String,
    @SerializedName("Catégorie") val Categorie: String,
    @SerializedName("Latitude") val Latitude: Double,
    @SerializedName("Longitude") val Longitude: Double,
    @SerializedName("Photo") val Photo: String
)

data class Signalement(
    val id: Int,
    val description: String,
    val date: String,
    val statut: String,
    val photo: String?,
    val latitude: Double,
    val longitude: Double,
    val category: Category?,
    val citoyen: Citoyen?
)

data class SignalementResponse(
    val count: Int,
    val results: List<Signalement>
)
