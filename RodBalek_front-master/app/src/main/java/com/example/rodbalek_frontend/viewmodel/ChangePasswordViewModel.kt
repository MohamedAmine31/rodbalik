package com.example.rodbalek_frontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.data.model.login.ChangePasswordRequest
import com.example.rodbalek_frontend.data.model.login.ChangePasswordResponse
import com.example.rodbalek_frontend.data.repository.ForgetPasswordRepository

class ChangePasswordViewModel(private val repository: ForgetPasswordRepository) : ViewModel() {

    private val _changePasswordResult = MutableLiveData<Result<ChangePasswordResponse>>()
    val changePasswordResult: LiveData<Result<ChangePasswordResponse>> = _changePasswordResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun changePassword(request: ChangePasswordRequest) {
        _isLoading.value = true
        repository.changePassword(request) { result ->
            _isLoading.value = false
            _changePasswordResult.value = result
        }
    }
}

class ChangePasswordViewModelFactory(private val repository: ForgetPasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
