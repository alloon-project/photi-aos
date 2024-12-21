package com.example.alloon_aos.data.storage

import android.util.Log
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {
    companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    fun hasNoTokens(): Boolean {
        return getAccessToken() == null && getRefreshToken() == null
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY,null)
    }

    fun saveAccessToken(token: String){
        sharedPreferences.setString(ACCESS_TOKEN_KEY, token)
        Log.d("test","get access : "+getAccessToken())
    }

    fun deleteAccessToken(){
        sharedPreferences.remove(ACCESS_TOKEN_KEY)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY,null)
    }

    fun saveRefreshToken(token: String){
        sharedPreferences.setString(REFRESH_TOKEN_KEY, token)
        Log.d("test","get refresh : "+getRefreshToken())
    }

    fun deleteRefreshToken(){
        sharedPreferences.remove(REFRESH_TOKEN_KEY)
    }

    fun deleteAllToken() {
        deleteAccessToken()
        deleteRefreshToken()
    }
}