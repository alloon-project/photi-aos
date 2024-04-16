package com.example.alloon_aos.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.alloon_aos.data.model.EmailCodeDTO
import com.example.alloon_aos.data.model.IdCodeDTO
import retrofit2.http.Headers

interface ApiService {
    @Headers("Content-Type: application/json")

    @POST("/api/v1/contacts")
    fun sendEmailCode(@Body params:Map<String, String>) : Call<EmailCodeDTO>

    @GET("/api/v1/users/username")
    fun verifyId(
        @Query("username") name : String)
    : Call<IdCodeDTO>

}