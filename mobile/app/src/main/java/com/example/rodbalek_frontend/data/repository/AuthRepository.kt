package com.example.rodbalek_frontend.data.repository

import com.example.rodbalek_frontend.api.ApiService
import com.example.rodbalek_frontend.data.model.login.TokenResponse
import com.example.rodbalek_frontend.data.model.login.LoginRequest
import com.example.rodbalek_frontend.data.model.register.RegisterRequest
import com.example.rodbalek_frontend.data.model.register.UsernameSuggestionRequest
import com.example.rodbalek_frontend.data.model.register.UsernameSuggestionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class  AuthRepository(private val apiService: ApiService) {

    fun login(request: LoginRequest, onResult: (Result<TokenResponse>) -> Unit) {
        apiService.login(request).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(Result.success(response.body()!!))
                } else {
                    onResult(Result.failure(Exception(response.errorBody()?.string() ?: "Erreur inconnue")))
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    fun register(request: RegisterRequest, onResult: (Result<Void?>) -> Unit) {
        apiService.registerUser(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onResult(Result.success(null))
                } else {
                    onResult(Result.failure(Exception(response.errorBody()?.string() ?: "Erreur inconnue")))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    fun checkUsername(username: String, onResult: (Result<UsernameSuggestionResponse>) -> Unit) {
        val request = UsernameSuggestionRequest(username)
        apiService.suggestUsername(request).enqueue(object : Callback<UsernameSuggestionResponse> {
            override fun onResponse(
                call: Call<UsernameSuggestionResponse>,
                response: Response<UsernameSuggestionResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(Result.success(response.body()!!))
                } else {
                    onResult(Result.failure(Exception(response.errorBody()?.string() ?: "Erreur lors de la vérification")))
                }
            }

            override fun onFailure(call: Call<UsernameSuggestionResponse>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }
}
