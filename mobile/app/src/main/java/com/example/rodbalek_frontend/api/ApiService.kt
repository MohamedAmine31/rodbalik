package com.example.rodbalek_frontend.api


import com.example.rodbalek_frontend.data.model.Categorie.ApiResponseCategories
import com.example.rodbalek_frontend.data.model.Signalement.Signalement
import com.example.rodbalek_frontend.data.model.Signalement.SignalementRequest
import com.example.rodbalek_frontend.data.model.login.ChangePasswordRequest
import com.example.rodbalek_frontend.data.model.login.ChangePasswordResponse
import com.example.rodbalek_frontend.data.model.login.ForgetPasswordRequest
import com.example.rodbalek_frontend.data.model.login.ForgetPasswordResponse
import com.example.rodbalek_frontend.data.model.login.LoginRequest
import com.example.rodbalek_frontend.data.model.login.TokenResponse
import com.example.rodbalek_frontend.data.model.login.VerifyCodeRequest
import com.example.rodbalek_frontend.data.model.login.VerifyCodeResponse
import com.example.rodbalek_frontend.data.model.register.RegisterRequest
import com.example.rodbalek_frontend.data.model.register.UsernameSuggestionRequest
import com.example.rodbalek_frontend.data.model.register.UsernameSuggestionResponse
import com.example.rodbalek_frontend.data.model.user.UserRes
import com.example.rodbalek_frontend.data.model.user.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/auth/register/")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("api/users/suggest-username/")
    fun suggestUsername(@Body request: UsernameSuggestionRequest): Call<UsernameSuggestionResponse>

    @POST("api/token/")
    fun login(@Body request: LoginRequest): Call<TokenResponse>

    @POST("api/token/refresh/")
    fun refreshToken(@Body request: Map<String, String>): Call<TokenResponse>

    @GET("api/users/me/")
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserResponse>

    @POST("api/user/user_email/")
    fun find_email(@Body email: String): Call<Void>

    @POST("api/email/send-code/")
    fun forgetPassword(@Body request: ForgetPasswordRequest): Call<ForgetPasswordResponse>

    @POST("api/email/verify-code/")
    fun resetPassword(@Body request: VerifyCodeRequest): Call<VerifyCodeResponse>

    @POST("api/email/change-password/")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

    @POST("api/signalement/soumettre/")
    fun envoyerSignalement(
        @Body request: SignalementRequest
    ): Call<Void>

    @Multipart
    @POST("api/signalement/soumettre/")
    fun envoyerSignalementMultipart(
        @Part("Description") description: RequestBody,
        @Part("Categorie") categorie: RequestBody,
        @Part("Latitude") latitude: RequestBody,
        @Part("Longitude") longitude: RequestBody,
        @Part media: MultipartBody.Part?
    ): Call<Void>

    @GET("api/categories/")
    fun getCategories(

    ): Call<ApiResponseCategories>
    @GET("api/signalements/me/")
    fun getSignalements(): Call<List<Signalement>>

    @GET("api/users/me/")
    fun getUserProfile(): Call<UserRes>


}
