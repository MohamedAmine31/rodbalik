package com.example.rodbalek_frontend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rodbalek_frontend.data.model.login.LoginRequest
import com.example.rodbalek_frontend.data.model.login.TokenResponse
import com.example.rodbalek_frontend.data.model.register.RegisterRequest
import com.example.rodbalek_frontend.data.model.register.UsernameSuggestionResponse
import com.example.rodbalek_frontend.data.repository.AuthRepository

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<TokenResponse>>()
    val loginResult: LiveData<Result<TokenResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Void?>>()
    val registerResult: LiveData<Result<Void?>> = _registerResult

    private val _usernameCheckResult = MutableLiveData<Result<UsernameSuggestionResponse>>()
    val usernameCheckResult: LiveData<Result<UsernameSuggestionResponse>> = _usernameCheckResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(request: LoginRequest) {
        _isLoading.value = true
        repository.login(request) {
            _isLoading.value = false
            _loginResult.value = it
        }
    }

    fun register(request: RegisterRequest) {
        _isLoading.value = true
        repository.register(request) {
            _isLoading.value = false
            _registerResult.value = it
        }
    }

    fun checkUsername(username: String) {
        repository.checkUsername(username) {
            _usernameCheckResult.value = it
        }
    }
}