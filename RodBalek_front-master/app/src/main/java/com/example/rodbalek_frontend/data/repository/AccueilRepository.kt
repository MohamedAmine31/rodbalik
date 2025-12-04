package com.example.rodbalek_frontend.data.repository

import com.example.rodbalek_frontend.api.ApiService
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccueilRepository(private val apiService: ApiService) {

    fun getSignalements(callback: (Result<List<Signalement>>) -> Unit) {
        apiService.getSignalements().enqueue(object : Callback<List<Signalement>> {
            override fun onResponse(
                call: Call<List<Signalement>>,
                response: Response<List<Signalement>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Exception("Erreur lors de la récupération des signalements")))
                }
            }

            override fun onFailure(call: Call<List<Signalement>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
