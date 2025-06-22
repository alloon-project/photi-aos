package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.request.ReportRequest
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.CommentRequest
import com.example.alloon_aos.data.model.response.CommentResponse
import com.example.alloon_aos.data.model.response.FeedChallengeData
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.model.response.UserVerificationStatus
import com.example.alloon_aos.data.model.response.VerifiedFeedExistence
import com.example.alloon_aos.data.model.response.VerifiedMemberCount
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

    @GET("/api/challenges/{challengeId}")
    suspend fun get_challenge( //챌린지 소개 조회
        @Path("challengeId") challengeId: Int
    ): Response<ApiResponse<FeedChallengeData>>

    @GET("/api/challenges/{challengeId}/feeds/v2")
    suspend fun get_challengeFeeds(
        @Path("challengeId") challengeId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "LATEST"
    ): Response<ApiResponse<ChallengeFeedsData>>

    @GET("/api/challenges/{challengeId}/feed-members")
    suspend fun get_challengeFeedCount(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<ChallengeFeedsData>> //TODO response type 수정

    //피드 챌린지 소개
    @GET("/api/challenges/{challengeId}/info")
    suspend fun get_challengeInfo(
        @Path("challengeId") challengeId: Int
    ): Response<ApiResponse<ChallengeInfoData>>

    //피드 개별 조회
    @GET("/api/challenges/{challengeId}/feeds/{feedId}")
    suspend fun get_challengeFeedDetail(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int
    ): Response<ApiResponse<FeedDetailData>>

    @GET("/api/challenges/{challengeId}/challenge-members")
    suspend fun get_challengeMembers(
        @Path("challengeId") challengeId: Int
    ): Response<ApiResponse<List<ChallengeMember>>>


    //댓글리스트 조회
    @GET("/api/challenges/feeds/{feedId}/comments")
    suspend fun get_feedComments(
        @Path("feedId") feedId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<FeedCommentsData>>

    // 챌린지 개인 목표 업데이트
    @PATCH("/api/challenges/{challengeId}/challenge-members/goal")
    suspend fun updateGoal(
        @Path("challengeId") challengeId: Int, // 챌린지 ID
        @Body goal: Map<String, String>
    ): ApiResponse<SuccessMessageReponse>

    //챌린지 인증
    @Multipart
    @POST("/api/challenges/{challengeId}/feeds")
    suspend fun postChallengeFeed(
        @Path("challengeId") challengeId: Int, // 챌린지 ID
        @Part image: MultipartBody.Part // 이미지 파일
    ): Response<ApiResponse<SuccessMessageReponse>>

    //댓글 등록
    @POST("/api/challenges/{challengeId}/feeds/{feedId}/comments")
    suspend fun postComment(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
        @Body comment: CommentRequest
    ): Response<ApiResponse<CommentResponse>>

    //댓글 등록
    @DELETE("/api/challenges/{challengeId}/feeds/{feedId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
        @Path("commentId") commentId: Int,

    ): Response<ApiResponse<SuccessMessageReponse>>


    //신고 등록
    @POST("/api/reports/{targetId}")
    suspend fun post_reports(
        @Path("targetId") targetId: Int,
        @Body params: ReportRequest
    ): Response<ApiResponse<SuccessMessageReponse>>

    // 피드 좋아요 추가
    @POST("/api/challenges/{challengeId}/feeds/{feedId}/like")
    suspend fun postFeedLike(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>>

    // 피드 좋아요 취소
    @DELETE("/api/challenges/{challengeId}/feeds/{feedId}/like")
    suspend fun deleteFeedLike(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>>

    //피드 당일 인증 파티원 수 조회
    @GET("/api/challenges/{challengeId}/feed-members")
    suspend fun get_verifiedMemberCount(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<VerifiedMemberCount>>

    //피드 오늘 인증 여부
    @GET("/api/users/challenges/{challengeId}/prove")
    suspend fun get_is_user_verified_today(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<UserVerificationStatus>>

    //챌린지 인증 피드 존재 여부
    @GET("/api/challenges/{challengeId}/feed-existence")
    suspend fun get_is_verified_feed_exist(
        @Path("challengeId") challengeId: Int,
    ): Response<ApiResponse<VerifiedFeedExistence>>

    //피드 삭제
    @DELETE("/api/challenges/{challengeId}/feeds/{feedId}")
    suspend fun delete_feed(
        @Path("challengeId") challengeId: Int,
        @Path("feedId") feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>>

}