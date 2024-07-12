package com.example.alloon_aos.data.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token",null)
    }

    fun saveAccessToken(token: String){
        sharedPreferences.setString("access_token", ACCESS_TOKEN_KEY.toString())
    }

    fun deleteAccessToken(){
        sharedPreferences.remove("access_token")
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token",null)
    }

    fun saveRefreshToken(token: String){
        sharedPreferences.setString("refresh_token", REFRESH_TOKEN_KEY.toString())
    }

    fun deleteRefreshToken(){
        sharedPreferences.remove("refresh_token")
    }
}