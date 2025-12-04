package com.example.rodbalek_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import com.example.rodbalek_frontend.data.repository.SignalementRepository
import kotlinx.coroutines.launch

class SignalemntViewModel(private val repository: SignalementRepository) : ViewModel() {

    private val _signalements = MutableLiveData<List<Signalement>>()
    val signalements: LiveData<List<Signalement>> = _signalements

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSignalements() {
        _isLoading.value = true
        repository.getSignalements {
            _isLoading.value = false
            it.onSuccess { signalements ->
                _signalements.value = signalements
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message
            }
        }
    }
}