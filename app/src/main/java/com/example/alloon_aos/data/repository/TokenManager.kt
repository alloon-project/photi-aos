package com.example.alloon_aos.data.repository

import android.util.Log
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
        sharedPreferences.setString("access_token", token)
        Log.d("test","get access : "+getAccessToken())
    }

    fun deleteAccessToken(){
        sharedPreferences.remove("access_token")
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token",null)
    }

    fun saveRefreshToken(token: String){
        sharedPreferences.setString("refresh_token", token)
        Log.d("test","get refresh : "+getRefreshToken())
    }

    fun deleteRefreshToken(){
        sharedPreferences.remove("refresh_token")
    }
}