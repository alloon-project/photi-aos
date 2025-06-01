package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.request.Goal
import com.example.alloon_aos.data.model.response.ChallengeListResponse
import com.example.alloon_aos.data.model.response.ChallengeResponse
import com.example.alloon_aos.data.model.response.ChipListResponse
import com.example.alloon_aos.data.model.response.CodeResponse
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.model.response.ExamImgResponse
import com.example.alloon_aos.data.model.response.MatchResponse
import com.example.alloon_aos.data.model.response.PagingListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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

interface ChallengeService {
    @Headers("Content-Type: application/json")

    //DELETE
    @DELETE("/api/challenges/{challengeId}")
    fun delete_exitChallenge( //챌린지 탈퇴
        @Path("challengeId") challengeId: Int
    ): Call<MessageResponse>

    //GET
    @GET("/api/challenges")
    suspend fun get_challengeLatest( //최신순-모든 챌린지 조회
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingListResponse>

    @GET("/api/challenges/{challengeId}/invitation-code")
    fun get_challengeCode( //챌린지 초대코드 조회
        @Path("challengeId") challengeId: Int
    ): Call<CodeResponse>

    @GET("/api/challenges/{challengeId}/invitation-code-match")
    fun get_challengeCodeMatch( //챌린지 초대코드 일치여부 조회
        @Path("challengeId") challengeId: Int,
        @Query("invitationCode") invitationCode: String
    ): Call<MatchResponse>

    @GET("/api/challenges/{challengeId}")
    fun get_challenge( //챌린지 소개 조회
        @Path("challengeId") challengeId: Int
    ): Call<ChallengeResponse>

    @GET("/api/challenges/search/name")
    suspend fun get_searchName( //챌린지 이름 검색
        @Query("challengeName") challengeName: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingListResponse>

    @GET("/api/challenges/search/hashtag")
    suspend fun get_searchHashtag( //챌린지 해시태그 검색
        @Query("hashtag") hashtag: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingListResponse>

    @GET("/api/challenges/popular")
    fun get_challengePopular( //추천순-인기있는 챌린지 조회
    ): Call<ChallengeListResponse>

    @GET("/api/challenges/hashtags")
    fun get_hashtagList( //해시태그 리스트 조회
    ): Call<ChipListResponse>

    @GET("/api/challenges/example-images")
    fun get_exampleImg( //예시 이미지 리스트 조회
    ): Call<ExamImgResponse>

    @GET("/api/challenges/by-hashtags")
    suspend fun get_challengeHashtag( //해시태그 모아보기 조회
        @Query("hashtag") hashtag: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PagingListResponse>

    //PATCH
    @Multipart
    @PATCH("/api/challenges/{challengeId}")
    fun patch_modifyChallenge( //챌린지 수정
        @Path("challengeId") challengeId: Int,
        @Part("request") request: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Call<MessageResponse>

    @PATCH("/api/challenges/{challengeId}/challenge-members/goal")
    fun patch_setGoal( //개인 목표 작성
        @Path("challengeId") challengeId: Int,
        @Body params: Goal
    ): Call<MessageResponse>

    //POST
    @Multipart
    @POST("/api/challenges")
    fun post_createChallenge( //챌린지 생성
        @Part("request") request: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Call<ChallengeResponse>

    @POST("/api/challenges/{challengeId}/join")
    fun post_joinChallenge( //챌린지 참여하기
        @Path("challengeId") challengeId: Int,
        @Body params: Goal
    ): Call<MessageResponse>
}