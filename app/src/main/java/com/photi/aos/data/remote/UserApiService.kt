package com.photi.aos.data.remote

import com.photi.aos.data.model.response.ApiResponse
import com.photi.aos.data.model.response.ChallengeRecordData
import com.photi.aos.data.model.response.EndedChallengeData
import com.photi.aos.data.model.response.FeedByDate
import com.photi.aos.data.model.response.FeedDate
import com.photi.aos.data.model.response.FeedHistoryData
import com.photi.aos.data.model.response.MyChallengeCount
import com.photi.aos.data.model.response.MyChallenges
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UserApiService {
    @Headers("Content-Type: application/json")

    @GET("/api/v2/users/challenges")
    suspend fun get_my_challenges(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<MyChallenges>>

    @GET("/api/v2/users/feed-dates")
    suspend fun get_feed_date(): Response<ApiResponse<FeedDate>>

    @GET("/api/v2/users/feeds-by-date")
    suspend fun get_feeds_by_date(
        @Query("date") date: String, //2024-10-23
    ): Response<ApiResponse<List<FeedByDate>>>

    @GET("/api/v2/users/feed-history")
    suspend fun get_feed_history(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<FeedHistoryData>>

    @GET("/api/v2/users/ended-challenges")
    suspend fun get_ended_challenges(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<ApiResponse<EndedChallengeData>>

    @GET("/api/v2/users/challenge-count")
    suspend fun get_challenges_count(): Response<ApiResponse<MyChallengeCount>>

    @GET("/api/v2/users/challenge-history")
    suspend fun get_challenge_history(): Response<ApiResponse<ChallengeRecordData>>

}