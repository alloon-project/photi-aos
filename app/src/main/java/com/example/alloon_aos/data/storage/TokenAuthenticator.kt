package com.example.alloon_aos.data.storage

import android.util.Log
import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.data.model.request.RefreshTokenRequest
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.remote.RetrofitClient.apiService
import com.example.alloon_aos.data.storage.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor( // 401 에러(토큰 관련 에러) 응답 시 호출 됨
    private val tokenManager: TokenManager
) : Authenticator {
    private var retryCount = 0
    private val maxRetries = 3
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator","Authenticating ...")
        if (retryCount >= maxRetries) {
            Log.e("TokenAuthenticator", "Max retry limit reached. Stopping token refresh.")
            return null // 더 이상 인증 시도 안 함
        }

        retryCount++

        Log.d("TokenAuthenticator", "Authenticating ... Attempt: $retryCount")

        val url = response.request.url.toString()

        if (url.contains("login")) {
            // 401에러인데 토큰과 관련이 없고 토큰이 없는 상태 : 하나씩 하면서 추가할 것
            return null
        }

        val refreshToken = runBlocking {
            tokenManager.getRefreshToken()
        }
        if (refreshToken == null) {
            Log.e("TokenAuthenticator", "Refresh token is null, logging out...")
            doTokenExired()
            return null
        }


        val newAccessToken = runBlocking {
            try {
                Log.d("TokenAuthenticator", "Making API request to refresh token...")
                val tokenResponse = apiService.post_token(refreshToken).execute()

                Log.d("TokenAuthenticator", "API request executed. Response received. ($tokenResponse)")

                if (tokenResponse.isSuccessful) {
                    Log.d("TokenAuthenticator", "Response is successful. Extracting new access token...")

                    val accessToken = tokenResponse.headers()["authorization"]?.replace("Bearer ", "")
                    accessToken
                } else {
                    Log.e("TokenAuthenticator", "Failed to refresh token. Response code: ${tokenResponse.code()}")

                    if (tokenResponse.code() == 401 || tokenResponse.code() == 403) {
                        Log.e("TokenAuthenticator", "Received 401/403, calling doTokenExpired()...")
                        doTokenExired()
                    }

                    Log.d("TokenAuthenticator", "response Code: ${tokenResponse.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "Exception occurred while refreshing token", e)
                e.printStackTrace()
                null
            }
        }


//        if (newAccessToken == null) {
//            doTokenExired()
//            return null
//        } else {
//            tokenManager.saveAccessToken(newAccessToken)
//            return response.request.newBuilder().apply {
//                removeHeader("Authorization")
//                addHeader("Authorization", "Bearer $newAccessToken")
//            }.build()
//        }

        return if (newAccessToken != null) {
            Log.d("TokenAuthenticator", "New access token received: $newAccessToken")

            // 새 토큰 저장
            tokenManager.saveAccessToken(newAccessToken)

            // 기존 요청을 새로운 토큰으로 재시도
            response.request.newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            Log.e("TokenAuthenticator", "New access token is null, logging out...")
            doTokenExired()
            null
        }
    }

    private fun doTokenExired() {
        Log.w("TokenAuthenticator", "Token expired. Logging out user.")
        tokenManager.deleteAllToken()
        MyApplication.instance.tokenExpired()
    }
}