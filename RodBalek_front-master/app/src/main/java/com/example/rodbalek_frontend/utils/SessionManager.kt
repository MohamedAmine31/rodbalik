package com.example.rodbalek_frontend.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "RodBalekSession"
    private const val USER_TOKEN = "user_token"
    private const val REFRESH_TOKEN = "refresh_token"
    private const val USER_ID = "user_id"

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuth(context: Context, token: String, refreshToken: String, userId: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(USER_TOKEN, token)
        editor.putString(REFRESH_TOKEN, refreshToken)
        editor.putInt(USER_ID, userId)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        return getSharedPreferences(context).getString(USER_TOKEN, null)
    }

    fun getRefreshToken(context: Context): String? {
        return getSharedPreferences(context).getString(REFRESH_TOKEN, null)
    }

    fun getUserId(context: Context): Int {
        return getSharedPreferences(context).getInt(USER_ID, -1)
    }

    fun logout(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(USER_TOKEN)
        editor.remove(REFRESH_TOKEN)
        editor.remove(USER_ID)
        editor.apply()
    }
}