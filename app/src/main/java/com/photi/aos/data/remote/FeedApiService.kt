package com.photi.aos.data.remote

import com.photi.aos.data.model.request.ChallengeFeedImageRequest
import com.photi.aos.data.model.request.ReportRequest
import com.photi.aos.data.model.response.ApiResponse
import com.photi.aos.data.model.response.ChallengeFeedsData
import com.photi.aos.data.model.response.ChallengeInfoData
import com.photi.aos.data.model.response.ChallengeMember
import com.photi.aos.data.model.response.CommentRequest
import com.photi.aos.data.model.response.CommentResponse
import com.photi.aos.data.model.response.FeedChallengeData
import com.photi.aos.data.model.response.FeedCommentsData
import com.photi.aos.data.model.response.FeedDetailData
import com.photi.aos.data.model.response.SuccessMessageReponse
import com.photi.aos.data.model.response.UserVerificationStatus
import com.photi.aos.data.model.response.VerifiedFeedExistence
import com.photi.aos.data.model.response.VerifiedMemberCount
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("/api/v2/challenges/{challengeId}")
    suspend fun get_challenge( //챌린지 개별 조회
        @Path("challengeId") challengeId: Int
    ): Response<ApiResponse<FeedChallengeData>>

    @GET("/api/v2/feeds/{challengeId}/v2")
    suspend fun get_challengeFeeds(
        @Path("challengeId") challengeId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "LATEST"
    ): Response<ApiResponse<ChallengeFeedsData>>

    //피드 챌린지 소개
    @GET("/api/v2/challenges/{challengeId}/intro")
    suspend fun get_challengeInfo( //챌린지 소개 조회
        @Path("challengeId") challengeId: Int
    ): Response<ApiResponse<ChallengeInfoData>>

    //피드 개별 조회
    @GET("/api/v2/feeds/{challengeId}/{feedId}")
    suspend fun get_challengeFeedDetail(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int
    ): Response<ApiResponse<FeedDetailData>>

    @GET("/api/v2/challenge-members/{challengeId}")
    suspend fun get_challengeMembers(
        @Path("challengeId") challengeId: Int
    ): Response<ApiResponse<List<ChallengeMember>>>


    //댓글리스트 조회
    @GET("/api/v2/feed-comments/{feedId}")
    suspend fun get_feedComments(
        @Path("feedId") feedId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<FeedCommentsData>>

    // 챌린지 개인 목표 업데이트
    @PATCH("/api/v2/challenge-members/{challengeId}/goal")
    suspend fun updateGoal(
        @Path("challengeId") challengeId: Int, // 챌린지 ID
        @Body goal: Map<String, String>
    ): ApiResponse<SuccessMessageReponse>

    //피드 인증
    @POST("/api/v2/feeds/{challengeId}")
    suspend fun postChallengeFeed(
        @Path("challengeId") challengeId: Int, // 챌린지 ID
        @Body request: ChallengeFeedImageRequest // 이미지 url
    ): Response<ApiResponse<SuccessMessageReponse>>

    //댓글 등록
    @POST("/api/v2/feed-comments/{challengeId}/{feedId}")
    suspend fun postComment(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
        @Body comment: CommentRequest
    ): Response<ApiResponse<CommentResponse>>

    //댓글 삭제
    @DELETE("/api/v2/feed-comments/{challengeId}/{feedId}/{commentId}")
    suspend fun deleteComment(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
        @Path("commentId") commentId: Int,

    ): Response<ApiResponse<SuccessMessageReponse>>


    //신고 등록
    @POST("/api/v2/reports/{targetId}")
    suspend fun post_reports(
        @Path("targetId") targetId: Int,
        @Body params: ReportRequest
    ): Response<ApiResponse<SuccessMessageReponse>>

    // 피드 좋아요 추가
    @POST("/api/v2/feed-likes/{challengeId}/{feedId}")
    suspend fun postFeedLike(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>>

    // 피드 좋아요 취소
    @DELETE("/api/v2/feed-likes/{challengeId}/{feedId}")
    suspend fun deleteFeedLike(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>>

    //피드 당일 인증 파티원 수 조회
    @GET("/api/v2/feeds/{challengeId}/member-count")
    suspend fun get_verifiedMemberCount(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<VerifiedMemberCount>>

    //피드 오늘 인증 여부
    @GET("/api/v2/users/{challengeId}/prove")
    suspend fun get_is_user_verified_today(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<UserVerificationStatus>>

    //챌린지 인증 피드 존재 여부
    @GET("/api/v2/challenges/{challengeId}/feed")
    suspend fun get_is_verified_feed_exist(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<VerifiedFeedExistence>>

    //피드 삭제
    @DELETE("/api/v2/feeds/{challengeId}/{feedId}")
    suspend fun delete_feed(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>>

}