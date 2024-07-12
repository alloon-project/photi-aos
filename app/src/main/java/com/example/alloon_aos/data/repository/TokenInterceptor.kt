package com.example.alloon_aos.data.repository

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject

class TokenInterceptor @Inject constructor( // request 헤더에 jwt 토큰 추가 해주는 역할
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String = runBlocking {
            tokenManager.getAccessToken()
        } ?: return errorResponse(chain.request())

        val request = chain.request().newBuilder().header("Authorization", "Bearer $token").build()

        val response = chain.proceed(request)
        if (response.code == 200) { //reponse code가 200인지 확인
            val newAccessToken: String = response.header("Authorization", null) ?: return response //response 헤더에 키 값이 존재하는지 확인

            val existedAccessToken = tokenManager.getAccessToken()
            if (existedAccessToken != newAccessToken) { //token manager에 있는 token과 비교
                tokenManager.saveAccessToken(newAccessToken) //다르면 저장
            }
        }

        return chain.proceed(request)
    }

    private fun errorResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2)
        .code(401)
        .message("")
        .body(ResponseBody.create(null, ""))
        .build()
}