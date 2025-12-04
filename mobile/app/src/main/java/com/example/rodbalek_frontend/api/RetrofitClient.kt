package com.example.rodbalek_frontend.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"
    private var retrofit: Retrofit? = null
    private var retrofitPublic: Retrofit? = null

    fun getInstance(context: Context): ApiService {
        if (retrofit == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }


        return retrofit!!.create(ApiService::class.java)
    }
    fun getPublicInstance(): ApiService {
        if (retrofitPublic == null) {
            retrofitPublic = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitPublic!!.create(ApiService::class.java)
    }

}
