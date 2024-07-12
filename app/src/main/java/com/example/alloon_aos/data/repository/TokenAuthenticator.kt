package com.example.alloon_aos.data.repository

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
        val refreshToken = runBlocking {
            tokenManager.getRefreshToken()
        }

//        if (refreshToken == null || refreshToken == "LOGIN") {
//            response.close()
//            return null
//        }

        if (refreshToken != null) {
            val accessToken = runBlocking {
                tokenManager.getAccessToken()
            }
            response.request.newBuilder().apply {
                removeHeader("Authorization")
                addHeader("Authorization", "Bearer $accessToken")
            }.build()
        } else {
            //refresh token도 만료되어 로그인화면으로 이동
            return null
        }
        return newRequestWithToken(refreshToken, response.request)
    }

    private fun newRequestWithToken(token: String, request: Request): Request =
        request.newBuilder()
            .header("Authorization", token)
            .build()
}