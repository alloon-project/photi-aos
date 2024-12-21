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
    @DELETE("/api/challenges/{challengeId}")
    fun delete_exitChallenge(
        @Path("challengeId") challengeId: Int
    ): Call<MessageResponse>

    @GET("/api/challenges/example-images")
    fun get_exampleImg(
    ): Call<ExamImgResponse>

    @GET("/api/challenges/{challengeId}")
    fun get_challengeInfo(
        @Path("challengeId") challengeId: Int
    ): Call<ChallengeResponse>

    @GET("/api/challenges/{challengeId}/invitation-code")
    fun get_challengeCode(
        @Path("challengeId") challengeId: Int
    ): Call<CodeResponse>

    @Multipart
    @PATCH("/api/challenges/{challengeId}")
    fun patch_modifyChallenge(
        @Path("challengeId") challengeId: Int,
        @Part("request") request: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Call<MessageResponse>

    @PATCH("/api/challenges/{challengeId}/challenge-members/goal")
    fun patch_setGoal(
        @Path("challengeId") challengeId: Int,
        @Body params: Goal
    ): Call<MessageResponse>

    @Multipart
    @POST("/api/challenges")
    fun post_createChallenge(
        @Part("request") request: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Call<ChallengeResponse>
}