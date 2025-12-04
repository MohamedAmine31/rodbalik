package com.example.rodbalek_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.api.RetrofitClient
import com.example.rodbalek_frontend.data.repository.SignalementRepository

class SignalemntViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignalemntViewModel::class.java)) {
            val apiService = RetrofitClient.getInstance(context.applicationContext)
            val repository = SignalementRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return SignalemntViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}