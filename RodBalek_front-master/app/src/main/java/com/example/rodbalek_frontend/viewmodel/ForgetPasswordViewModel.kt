package com.example.rodbalek_frontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.data.model.login.ForgetPasswordRequest
import com.example.rodbalek_frontend.data.model.login.ForgetPasswordResponse
import com.example.rodbalek_frontend.data.repository.ForgetPasswordRepository

class ForgetPasswordViewModel(private val repository: ForgetPasswordRepository) : ViewModel() {

    private val _forgetPasswordResult = MutableLiveData<Result<ForgetPasswordResponse>>()
    val forgetPasswordResult: LiveData<Result<ForgetPasswordResponse>> = _forgetPasswordResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun forgetPassword(email: String) {
        _isLoading.value = true
        val request = ForgetPasswordRequest(email)
        repository.forgetPassword(request) { result ->
            _isLoading.value = false
            _forgetPasswordResult.value = result
        }
    }
}

class ForgetPasswordViewModelFactory(private val repository: ForgetPasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgetPasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
