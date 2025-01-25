package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeRecordData
import com.example.alloon_aos.data.model.response.EndedChallengeData
import com.example.alloon_aos.data.model.response.FeedByDate
import com.example.alloon_aos.data.model.response.FeedDate
import com.example.alloon_aos.data.model.response.FeedHistoryData
import com.example.alloon_aos.data.model.response.MyChallengeCount
import com.example.alloon_aos.data.model.response.MyChallenges
import com.example.alloon_aos.data.model.response.ProfileImageData
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApiService {
    @Headers("Content-Type: application/json")

    @GET("/api/my-challenges")
    suspend fun get_my_challenges(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<MyChallenges>>

    @GET("/api/users/feeds")
    suspend fun get_feed_date(): Response<ApiResponse<FeedDate>>

    @GET("/api/users/feeds-by-date")
    suspend fun get_feeds_by_date(
        @Query("date") date: String, //2024-10-23
    ): Response<ApiResponse<List<FeedByDate>>>

    @GET("/api/users/feed-history")
    suspend fun get_feed_history(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<FeedHistoryData>>

    @GET("/api/users/ended-challenges")
    suspend fun get_ended_challenges(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<EndedChallengeData>>

    @GET("/api/users/challenges")
    suspend fun get_challenges(): Response<ApiResponse<MyChallengeCount>>

    @GET("/api/users/challenge-history")
    suspend fun get_challenge_history(): Response<ApiResponse<ChallengeRecordData>>

    @POST("/api/users/image")
    suspend fun post_image(
        @Part imageFile: MultipartBody.Part
    ): Response<ApiResponse<ProfileImageData>>

}