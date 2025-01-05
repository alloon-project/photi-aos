package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApiService {
    @Headers("Content-Type: application/json")
    @GET("/api/challenges/{challengeId}/feeds")

    //피드 조회
    suspend fun get_challengeFeeds(
        @Path("challengeId") challengeId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "LATEST"
    ): Response<ApiResponse<ChallengeFeedsData>>

    //피드 챌린지 소개
    @GET("/api/challenges/{challengeId}/info")
    suspend fun get_challengeInfo(
        @Path("challengeId") challengeId: Long
    ): Response<ApiResponse<ChallengeInfoData>>

    //피드 개별 조회
    @GET("/api/challenges/{challengeId}/feeds/{feedId}")
    suspend fun get_challengeFeedDetail(
        @Path("challengeId") challengeId: Long,
        @Path("feedId") feedId: Long
    ): Response<ApiResponse<FeedDetailData>>

    @GET("/api/challenges/{challengeId}/challenge-members")
    suspend fun get_challengeMembers(
        @Path("challengeId") challengeId: Long
    ): Response<ApiResponse<List<ChallengeMember>>>


    //댓글리스트 조회
    @GET("/api/challenges/feeds/{feedId}/comments")
    suspend fun get_feedComments(
        @Path("feedId") feedId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<FeedCommentsData>>

    // 챌린지 개인 목표 업데이트
    @PATCH("/api/challenges/{challengeId}/challenge-members/goal")
    suspend fun updateGoal(
        @Path("challengeId") challengeId: Long, // 챌린지 ID
        @Body goal: Map<String, String>
    ): ApiResponse<SuccessMessageReponse>

    //챌린지 인증
    @Multipart
    @POST("/api/challenges/{challengeId}/feeds")
    suspend fun postChallengeFeed(
        @Path("challengeId") challengeId: Long, // 챌린지 ID
        @Part image: MultipartBody.Part, // 이미지 파일
        @Part("description") description: String // 피드 설명
    ): ApiResponse<SuccessMessageReponse>

    //댓글 등록
    @POST("/api/challenges/{challengeId}/feeds/{feedId}/comments")
    suspend fun postComment(
        @Path("challengeId") challengeId: Long, // 챌린지 ID
        @Path("feedId") feedId: Long, // 피드 ID
        @Body comment: Map<String, String> // 댓글 등록 요청 데이터
    ): ApiResponse<SuccessMessageReponse>
}