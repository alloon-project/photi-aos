package com.photi.aos.data.storage

import android.util.Log
import com.photi.aos.data.enum.OAuthProvider
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {
    companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val OAUTH_PROVIDER = "oauthProvider"
        private const val OAUTH_SUB = "oauthSub"
    }

    fun hasNoTokens(): Boolean {
        return getAccessToken() == null && getRefreshToken() == null
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY,null)
    }

    fun saveAccessToken(token: String){
        sharedPreferences.setString(ACCESS_TOKEN_KEY, token)
    }

    fun deleteAccessToken(){
        sharedPreferences.remove(ACCESS_TOKEN_KEY)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY,null)
    }

    fun saveRefreshToken(token: String){
        sharedPreferences.setString(REFRESH_TOKEN_KEY, token)
    }

    fun deleteRefreshToken(){
        sharedPreferences.remove(REFRESH_TOKEN_KEY)
    }

    fun deleteAllToken() {
        deleteAccessToken()
        deleteRefreshToken()
        clearOAuthTokenSet()
    }


    // oauth
    data class OAuthTokenSet(
        val provider: OAuthProvider,
        val sub: String,
    )

    fun saveOAuthTokenSet(provider: OAuthProvider, sub: String) {
        sharedPreferences.setString(OAUTH_PROVIDER, provider.name)
        sharedPreferences.setString(OAUTH_SUB, sub)
    }

    fun getOAuthTokenSet(): OAuthTokenSet? {
        val providerRaw = sharedPreferences.getString(OAUTH_PROVIDER, null) ?: return null
        val sub = sharedPreferences.getString(OAUTH_SUB, null) ?: return null

        val provider = runCatching { OAuthProvider.valueOf(providerRaw) }.getOrNull() ?: return null
        if (sub.isBlank()) return null

        return OAuthTokenSet(provider, sub)
    }

    fun clearOAuthTokenSet() {
        sharedPreferences.remove(OAUTH_PROVIDER)
        sharedPreferences.remove(OAUTH_SUB)
    }
}