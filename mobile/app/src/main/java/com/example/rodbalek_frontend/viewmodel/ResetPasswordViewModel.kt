package com.example.rodbalek_frontend.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.data.model.login.VerifyCodeRequest
import com.example.rodbalek_frontend.data.model.login.VerifyCodeResponse
import com.example.rodbalek_frontend.data.repository.ForgetPasswordRepository

class ResetPasswordViewModel(private val repository: ForgetPasswordRepository) : ViewModel() {

    private val _verifyCodeResult = MutableLiveData<Result<VerifyCodeResponse>>()
    val verifyCodeResult: LiveData<Result<VerifyCodeResponse>> = _verifyCodeResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _isTimerExpired = MutableLiveData<Boolean>()
    val isTimerExpired: LiveData<Boolean> = _isTimerExpired

    private var countDownTimer: CountDownTimer? = null

    init {
        startTimer()
    }

    fun verifyCode(email: String, code: String) {
        _isLoading.value = true
        val request = VerifyCodeRequest(email, code)
        repository.verifyCode(request) { result ->
            _isLoading.value = false
            _verifyCodeResult.value = result
        }
    }

    private fun startTimer() {
        _isTimerExpired.value = false
        countDownTimer = object : CountDownTimer(10 * 60 * 1000, 1000) { // 10 minutes
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                _timerText.value = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _timerText.value = "⏰ Code expiré"
                _isTimerExpired.value = true
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}

class ResetPasswordViewModelFactory(private val repository: ForgetPasswordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResetPasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
