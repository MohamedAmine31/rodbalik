package com.example.rodbalek_frontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import com.example.rodbalek_frontend.data.repository.AccueilRepository

class AccueilViewModel(private val repository: AccueilRepository) : ViewModel() {

    private val _signalements = MutableLiveData<List<Signalement>>()
    val signalements: LiveData<List<Signalement>> = _signalements

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadSignalements()
    }

    fun loadSignalements() {
        _isLoading.value = true
        repository.getSignalements { result ->
            _isLoading.value = false
            result.onSuccess { list ->
                _signalements.value = list
            }.onFailure { t ->
                _error.value = t.message
            }
        }
    }
}

class AccueilViewModelFactory(private val repository: AccueilRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccueilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccueilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
