package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.EmailCode
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.alloon_aos.data.model.AuthDTO
import com.example.alloon_aos.data.model.UserData
import com.example.alloon_aos.data.model.NewPwd
import retrofit2.http.Headers
import retrofit2.http.PATCH

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/v1/contacts")
    fun post_sendEmailCode(
        @Body params:Map<String, String>)
    : Call<AuthDTO>

    @PATCH("/api/v1/contacts/verify")
    fun patch_verifyEmailCode(
        @Body params:EmailCode)
            : Call<AuthDTO>

    @GET("/api/v1/users/username")
    fun get_verifyId(
        @Query("username") name : String)
    : Call<AuthDTO>

    @POST("/api/v1/users/register")
    fun post_signUp(
        @Body parmas:UserData
    ): Call<AuthDTO>

    @POST("/api/v1/users/find-username")
    fun post_findId(
        @Body parmas:Map<String,String>
    ): Call<AuthDTO>

    @POST("/api/v1/users/find-password")
    fun post_findPwd(
        @Body parmas:UserData
    ): Call<AuthDTO>

    @POST("/api/v1/users/login")
    fun post_login(
        @Body parmas:UserData
    ): Call<AuthDTO>

    @PATCH("/api/v1/users/password")
    fun patch_modifyPwd(
        @Body parmas:NewPwd
    ): Call<AuthDTO>
}