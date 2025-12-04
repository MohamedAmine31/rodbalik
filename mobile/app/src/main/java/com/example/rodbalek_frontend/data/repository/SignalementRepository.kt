package com.example.rodbalek_frontend.data.repository

import com.example.rodbalek_frontend.api.ApiService
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignalementRepository(private val apiService: ApiService) {
    fun getSignalements(onResult: (Result<List<Signalement>>) -> Unit) {
        apiService.getSignalements().enqueue(object : Callback<List<Signalement>> {
            override fun onResponse(call: Call<List<Signalement>>, response: Response<List<Signalement>>) {
                if (response.isSuccessful) {
                    onResult(Result.success(response.body() ?: emptyList()))
                } else {
                    onResult(Result.failure(Exception(response.errorBody()?.string() ?: "Erreur inconnue")))
                }
            }
            override fun onFailure(call: Call<List<Signalement>>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
