package com.example.alloon_aos.data.repository

import android.util.Log
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
        Log.d("test","interceptor~")

        val request = chain.request()
        val url = request.url.toString()

        val newRequestBuilder = request.newBuilder()

        if (url.contains("/password")) {
            val token: String = runBlocking {
                tokenManager.getAccessToken()
            } ?: return errorResponse(chain.request())

            newRequestBuilder.header("Authorization", "Bearer $token")
        }

        val newRequest = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }

    private fun errorResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2)
        .code(401)
        .message("")
        .body(ResponseBody.create(null, ""))
        .build()
}