package com.photi.aos.data.storage

import android.util.Log
import com.photi.aos.data.model.ApiConfig
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.net.URL
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("TokenInterceptor", "Intercepting request...")

        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        Log.d("TokenInterceptor", "Request URL: $url, Method: $method")

        val newRequestBuilder = request.newBuilder()

        if (ApiConfig.isTokenRequired(getPathFromUrl(url), method)) {
            val token: String? = runBlocking {
                tokenManager.getAccessToken()
            }

            if (token.isNullOrEmpty()) {
                Log.e("TokenInterceptor", "AccessToken is null or empty!")
                return errorResponse(request)
            }

            Log.d("TokenInterceptor", "Adding Authorization Header: Bearer $token")
            newRequestBuilder.header("Authorization", "Bearer $token")
        } else {
            Log.d("TokenInterceptor", "No Authorization header added for URL: $url")
        }

        val newRequest = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }

    private fun shouldAddToken(url: String, method: String): Boolean {
        val match = ApiConfig.tokenRequiredApis.keys.any { apiUrl ->
            getPathFromUrl(url) == apiUrl && ApiConfig.tokenRequiredApis[apiUrl] == method
        }
        Log.d("TokenInterceptor", "Should Add Token: $match for URL: $url")
        return match
    }

    private fun getPathFromUrl(url: String): String {
        val parsedUrl = URL(url)  // URL 객체로 변환
        return parsedUrl.path  // 경로만 추출
    }


    private fun errorResponse(request: Request): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_2)
        .code(401)
        .message("Unauthorized: AccessToken is missing or invalid")
        .body(ResponseBody.create(null, ""))
        .build()
}
