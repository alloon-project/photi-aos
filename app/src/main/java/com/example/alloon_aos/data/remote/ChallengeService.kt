package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.request.Goal
import com.example.alloon_aos.data.model.response.ChallengeResponse
import com.example.alloon_aos.data.model.response.CodeResponse
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.model.response.ExamImgResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ChallengeService {
    //DELETE
    @DELETE("/api/challenges/{challengeId}")
    fun delete_exitChallenge( //챌린지 탈퇴
        @Path("challengeId") challengeId: Int
    ): Call<MessageResponse>

    //GET
    @GET("/api/challenges")
    fun get_challengeLatest( ////////////최신순-모든 챌린지 조회
    )

    @GET("/api/challenges/{challengeId}")
    fun get_challengeInfo( //챌린지 개별 조회
        @Path("challengeId") challengeId: Int
    ): Call<ChallengeResponse>

    @GET("/api/challenges/{challengeId}/invitation-code")
    fun get_challengeCode( //챌린지 초대코드 조회
        @Path("challengeId") challengeId: Int
    ): Call<CodeResponse>

    @GET("/api/challenges/popular")
    fun get_challengePopular( ///////////추천순-인기있는 챌린지 조회
    )

    @GET("/api/challenges/example-images")
    fun get_exampleImg( //예시 이미지 리스트 조회
    ): Call<ExamImgResponse>

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
}