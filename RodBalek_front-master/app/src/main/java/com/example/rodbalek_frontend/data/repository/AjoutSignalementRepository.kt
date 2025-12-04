package com.example.rodbalek_frontend.data.repository

import com.example.rodbalek_frontend.api.ApiService
import com.example.rodbalek_frontend.data.model.Categorie.ApiResponseCategories
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AjoutSignalementRepository(private val apiService: ApiService) {

    fun getCategories(callback: (Result<List<String>>) -> Unit) {
        apiService.getCategories().enqueue(object : Callback<ApiResponseCategories> {
            override fun onResponse(
                call: Call<ApiResponseCategories>,
                response: Response<ApiResponseCategories>
            ) {
                if (response.isSuccessful) {
                    val categories = response.body()?.results?.map { it.name } ?: emptyList()
                    callback(Result.success(categories))
                } else {
                    callback(Result.failure(Exception("Erreur chargement catégories")))
                }
            }

            override fun onFailure(call: Call<ApiResponseCategories>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun envoyerSignalement(
        description: RequestBody,
        categorie: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        photo: MultipartBody.Part,
        callback: (Result<Void?>) -> Unit
    ) {
        apiService.envoyerSignalementMultipart(description, categorie, latitude, longitude, photo)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback(Result.success(response.body()))
                    } else {
                        val errorBody = response.errorBody()?.string()
                        callback(Result.failure(Exception(errorBody)))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }
}
