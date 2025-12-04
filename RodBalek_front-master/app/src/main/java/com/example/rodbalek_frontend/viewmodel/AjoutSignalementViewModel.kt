package com.example.rodbalek_frontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.data.repository.AjoutSignalementRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AjoutSignalementViewModel(private val repository: AjoutSignalementRepository) : ViewModel() {

    //hadhi illi igerriha veiw model
    private val _categories = MutableLiveData<List<String>>()
    //hadhi illi ya9raha fragment
    val categories: LiveData<List<String>> = _categories

    private val _sendResult = MutableLiveData<Result<Void?>>()
    val sendResult: LiveData<Result<Void?>> = _sendResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadCategories()
    }

    fun loadCategories() {
        repository.getCategories { result ->
            result.onSuccess { list ->
                _categories.value = list
            }.onFailure { t ->
                _error.value = t.message
            }
        }
    }

    fun envoyerSignalement(
        description: RequestBody,
        categorie: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        photo: MultipartBody.Part
    ) {
        _isLoading.value = true
        repository.envoyerSignalement(description, categorie, latitude, longitude, photo) { result ->
            _isLoading.value = false
            _sendResult.value = result
        }
    }
}

class AjoutSignalementViewModelFactory(private val repository: AjoutSignalementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AjoutSignalementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AjoutSignalementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}