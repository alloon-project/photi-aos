package com.example.alloon_aos.data.remote

import com.example.alloon_aos.data.model.request.EmailCode
import com.example.alloon_aos.data.model.request.InquiryRequest
import com.example.alloon_aos.data.model.request.NewPwd
import com.example.alloon_aos.data.model.request.RefreshTokenRequest
import com.example.alloon_aos.data.model.request.UserData
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.AuthResponse
import com.example.alloon_aos.data.model.response.InquiryResponse
import com.example.alloon_aos.data.model.response.ProfileImageData
import com.example.alloon_aos.data.model.response.TokenResponse
import com.example.alloon_aos.data.model.response.UserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/contacts")
    fun post_sendEmailCode(
        @Body params: Map<String, String>
    )
            : Call<AuthResponse>

    @PATCH("/api/users")
    fun patch_deleteUser(
        @Body params: Map<String, String>
    ): Call<AuthResponse>

    @PATCH("/api/contacts/verify")
    fun patch_verifyEmailCode(
        @Body params: EmailCode
    )
            : Call<AuthResponse>

    @GET("/api/users/username")
    fun get_verifyId(
        @Query("username") name: String
    )
            : Call<AuthResponse>

    @POST("/api/users/register")
    fun post_signUp(
        @Body parmas: UserData
    ): Call<AuthResponse>

    @POST("/api/users/find-username")
    fun post_findId(
        @Body parmas: Map<String, String>
    ): Call<AuthResponse>

    @POST("/api/users/find-password")
    fun post_findPwd(
        @Body parmas: UserData
    ): Call<AuthResponse>

    @POST("/api/users/login")
    fun post_login(
        @Body parmas: UserData
    ): Call<AuthResponse>

    @PATCH("/api/users/password")
    fun patch_modifyPwd(
        @Body parmas: NewPwd
    ): Call<AuthResponse>

    @POST("/api/users/token")
    fun post_token(
        @Header("Refresh-Token") token: String
    ): Call<TokenResponse>

    @GET("/api/users")
    suspend fun get_users(): Response<ApiResponse<UserProfile>>

    @POST("/api/inquiries")
    suspend fun post_inquiries(
        @Body params: InquiryRequest
    ): Response<ApiResponse<InquiryResponse>>

    @Multipart
    @POST("/api/users/image")
    suspend fun post_image(
        @Part imageFile: MultipartBody.Part
    ): Response<ApiResponse<ProfileImageData>>

}

