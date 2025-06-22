package com.photi.aos.data.remote

import com.photi.aos.MyApplication
import com.photi.aos.data.storage.TokenAuthenticator
import com.photi.aos.data.storage.TokenInterceptor
import com.photi.aos.data.storage.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private const val BASE_URL = "http://54.180.105.206:8080" //클라우드 서버

    private val tokenManager: TokenManager by lazy {
        TokenManager(MyApplication.mySharedPreferences)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(TokenInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val challengeService: ChallengeService by lazy {
        retrofit.create(ChallengeService::class.java)
    }

    val feedService: FeedApiService by lazy {
        retrofit.create(FeedApiService::class.java)
    }

    val userService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}