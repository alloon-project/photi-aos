package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApiService {
    @Headers("Content-Type: application/json")
    @GET("/api/challenges/{challengeId}/feeds")
    suspend fun get_challengeFeeds(
        @Path("challengeId") challengeId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "LATEST"
    ): Response<ApiResponse<ChallengeFeedsData>>

    @GET("/api/challenges/{challengeId}/info")
    suspend fun get_challengeInfo(
        @Path("challengeId") challengeId: Long
    ):  Response<ApiResponse<ChallengeInfoData>>

    @GET("/api/challenges/{challengeId}/feeds/{feedId}")
    suspend fun get_challengeFeedDetail(
        @Path("challengeId") challengeId: Long,
        @Path("feedId") feedId: Long
    ): Response<ApiResponse<FeedDetailData>>

    @GET("/api/challenges/{challengeId}/challenge-members")
    suspend fun get_challengeMembers(
        @Path("challengeId") challengeId: Long
    ): Response<ApiResponse<List<ChallengeMember>>>


    @GET("/api/challenges/feeds/{feedId}/comments")
    suspend fun get_feedComments(
        @Path("feedId") feedId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<FeedCommentsData>>
}