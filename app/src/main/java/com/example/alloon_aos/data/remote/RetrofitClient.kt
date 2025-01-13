package com.example.alloon_aos.data.remote

import com.example.alloon_aos.MyApplication
import com.example.alloon_aos.data.storage.TokenAuthenticator
import com.example.alloon_aos.data.storage.TokenInterceptor
import com.example.alloon_aos.data.storage.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    //private const val BASE_
    // URL = "http://10.0.2.2:8080" //에뮬레이
    private const val BASE_URL = "http://172.30.1.76:8080" //스라
    //private const val BASE_URL = "http://172.30.1.44:8080"
    //private const val BASE_URL = "http://172.16.100.34:8080" //신촌 커피빈
    //private const val BASE_URL = "http://192.168.219.101:8080" //별 핫스팟


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