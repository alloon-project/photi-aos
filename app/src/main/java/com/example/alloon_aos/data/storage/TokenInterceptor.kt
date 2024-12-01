package com.example.alloon_aos.data.storage

import android.util.Log
import com.example.alloon_aos.data.model.ApiConfig
import com.example.alloon_aos.data.storage.TokenManager
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
        Log.d("token","interceptor")

        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        val newRequestBuilder = request.newBuilder()

        if (shouldAddToken(url, method)) {
            val token: String = runBlocking {
                tokenManager.getAccessToken()
            } ?: return errorResponse(chain.request())

            newRequestBuilder.header("Authorization", "Bearer $token")
        }

        val newRequest = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }

    private fun shouldAddToken(url: String, method: String): Boolean {
        // URL이 ApiConfig.tokenRequiredApis에 설정된 URL과 일치하고, 메서드가 해당 값과 일치하는지 확인
        return ApiConfig.tokenRequiredApis[url] == method
    }

    private fun errorResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2)
        .code(401)
        .message("")
        .body(ResponseBody.create(null, ""))
        .build()
}