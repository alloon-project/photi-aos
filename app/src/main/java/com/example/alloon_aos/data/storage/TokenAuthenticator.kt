package com.example.alloon_aos.data.storage

import android.util.Log
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.data.model.request.RefreshTokenRequest
import com.example.alloon_aos.data.remote.RetrofitClient.apiService
import com.example.alloon_aos.data.storage.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor( // 401 에러(토큰 관련 에러) 응답 시 호출 됨
    private val tokenManager: TokenManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("token","authenticator")

        val url = response.request.url.toString()

        if (url.contains("login")) {
            // 401에러인데 토큰과 관련이 없고 토큰이 없는 상태 : 하나씩 하면서 추가할 것
            return null
        }

        val refreshToken = runBlocking {
            tokenManager.getRefreshToken()
        }
        if (refreshToken == null) {
            doTokenExired()
            return null
        }

        val newAccessToken = runBlocking {
            try {
                val response = apiService.post_token(RefreshTokenRequest(refreshToken)).execute()
                if (response.isSuccessful) {
                    response.body()?.accessToken
                } else {
                    if (response.code() == 401) {
                        doTokenExired()
                    }
                    Log.d("token","code: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                doTokenExired()
                null
            }
        }

        if (newAccessToken == null) {
            doTokenExired()
            return null
        } else {
            tokenManager.saveAccessToken(newAccessToken)
            return response.request.newBuilder().apply {
                removeHeader("Authorization")
                addHeader("Authorization", "Bearer $newAccessToken")
            }.build()
        }
    }

    private fun doTokenExired() {
        tokenManager.deleteAllToken()
        MyApplication.instance.tokenExpired()
    }
}