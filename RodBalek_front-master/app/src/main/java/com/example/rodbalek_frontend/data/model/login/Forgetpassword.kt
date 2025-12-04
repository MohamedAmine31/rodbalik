package com.example.rodbalek_frontend.data.model.login

data class ForgetPasswordRequest(
    val email: String
)

data class ForgetPasswordResponse(
    val message: String
)

data class VerifyCodeResponse(
    val message: String
)

data class ChangePasswordRequest(
    val email: String,
    val new_password: String,
    val confirm_password: String
)

data class ChangePasswordResponse(
    val message: String
)
data class VerifyCodeRequest(
    val email: String,
    val code: String
)