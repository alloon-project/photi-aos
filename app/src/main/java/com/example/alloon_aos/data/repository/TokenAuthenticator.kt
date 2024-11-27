package com.example.alloon_aos.data.repository

import android.util.Log
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.data.model.request.RefreshTokenRequest
import com.example.alloon_aos.data.remote.RetrofitClient.apiService
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

        val accessToken = runBlocking {
            tokenManager.getAccessToken()
        }
        if (accessToken != null) {
            return null
        }

        val refreshToken = runBlocking {
            tokenManager.getRefreshToken()
        }
        if (refreshToken == null) {
            MyApplication.instance.tokenExpired()
            return null
        }

        val newAccessToken = runBlocking {
            try {
                val response = apiService.post_token(RefreshTokenRequest(refreshToken)).execute()
                if (response.isSuccessful) {
                    response.body()?.accessToken
                } else {
                    if (response.code() == 401) {
                        MyApplication.instance.tokenExpired()
                    }
                    null
                }
            } catch (e: Exception) {
                MyApplication.instance.tokenExpired()
                null
            }
        }

        if (newAccessToken == null)
            return null
        else {
            return response.request.newBuilder().apply {
                removeHeader("Authorization")
                addHeader("Authorization", "Bearer $newAccessToken")
            }.build()
        }
    }
}