package com.example.rodbalek_frontend.ui.auth

import android.content.Context
import com.example.rodbalek_frontend.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = SessionManager.getToken(context)
        val requestBuilder = chain.request().newBuilder()

        val request = chain.request()
        val url = request.url.toString()
        if (url.contains("/api/users/suggest-username/")) {
            return chain.proceed(request)
        }
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
