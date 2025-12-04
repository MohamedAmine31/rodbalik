package com.example.rodbalek_frontend.data.repository

import com.example.rodbalek_frontend.api.ApiService
import com.example.rodbalek_frontend.data.model.login.ChangePasswordRequest
import com.example.rodbalek_frontend.data.model.login.ChangePasswordResponse
import com.example.rodbalek_frontend.data.model.login.ForgetPasswordRequest
import com.example.rodbalek_frontend.data.model.login.ForgetPasswordResponse
import com.example.rodbalek_frontend.data.model.login.VerifyCodeRequest
import com.example.rodbalek_frontend.data.model.login.VerifyCodeResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordRepository(private val apiService: ApiService) {

    fun forgetPassword(request: ForgetPasswordRequest, callback: (Result<ForgetPasswordResponse>) -> Unit) {
        apiService.forgetPassword(request).enqueue(object : Callback<ForgetPasswordResponse> {
            override fun onResponse(
                call: Call<ForgetPasswordResponse>,
                response: Response<ForgetPasswordResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    val errorBody = response.errorBody()?.string()
                    callback(Result.failure(Exception(errorBody)))
                }
            }

            override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun verifyCode(request: VerifyCodeRequest, callback: (Result<VerifyCodeResponse>) -> Unit) {
        apiService.resetPassword(request).enqueue(object : Callback<VerifyCodeResponse> {
            override fun onResponse(
                call: Call<VerifyCodeResponse>,
                response: Response<VerifyCodeResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    val errorBody = response.errorBody()?.string()
                    callback(Result.failure(Exception(errorBody)))
                }
            }

            override fun onFailure(call: Call<VerifyCodeResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun changePassword(request: ChangePasswordRequest, callback: (Result<ChangePasswordResponse>) -> Unit) {
        apiService.changePassword(request).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(
                call: Call<ChangePasswordResponse>,
                response: Response<ChangePasswordResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(Result.success(response.body()!!))
                } else {
                    val errorBody = response.errorBody()?.string()
                    callback(Result.failure(Exception(errorBody)))
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
