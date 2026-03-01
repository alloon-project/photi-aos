package com.photi.aos.data.remote

import com.photi.aos.data.model.request.AppVersionRequest
import com.photi.aos.data.model.request.Email
import com.photi.aos.data.model.request.EmailCode
import com.photi.aos.data.model.request.InquiryRequest
import com.photi.aos.data.model.request.NewPwd
import com.photi.aos.data.model.request.UserData
import com.photi.aos.data.model.response.ApiResponse
import com.photi.aos.data.model.response.AppVersionResponse
import com.photi.aos.data.model.response.AuthResponse
import com.photi.aos.data.model.response.DeletedDateResponse
import com.photi.aos.data.model.response.InquiryResponse
import com.photi.aos.data.model.response.LoginOAuthResponse
import com.photi.aos.data.model.response.ProfileImageData
import com.photi.aos.data.model.response.TokenResponse
import com.photi.aos.data.model.response.UpdateOAuthUserNameRequest
import com.photi.aos.data.model.response.UpdateOAuthUserNameResponse
import com.photi.aos.data.model.response.UpdateUserProfileImageRequest
import com.photi.aos.data.model.response.UpdateUserProfileImageResponse
import com.photi.aos.data.model.response.UserImagePresignedUrlRequest
import com.photi.aos.data.model.response.UserImagePresignedUrlResponse
import com.photi.aos.data.model.response.UserProfile
import com.photi.aos.data.model.response.WithdrawOAuthResponse
import okhttp3.MultipartBody
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
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("/api/v2/auth/code")
    fun post_sendEmailCode(
        @Body params: Map<String, String>
    )
    : Call<AuthResponse>

    @PATCH("/api/v2/auth")
    fun patch_deleteUser(
        @Body params: Map<String, String>
    ): Call<AuthResponse>

    @PATCH("/api/v2/auth/code")
    fun patch_verifyEmailCode(
        @Body params: EmailCode
    )
    : Call<AuthResponse>

    @GET("/api/v2/auth/validate/name")
    fun get_verifyId(
        @Query("username") name: String
    )
    : Call<AuthResponse>

    @POST("/api/v2/auth")
    fun post_signUp(
        @Body parmas: UserData
    ): Call<AuthResponse>

    @POST("/api/v2/auth/username")
    fun post_findId(
        @Body parmas: Map<String, String>
    ): Call<AuthResponse>

    @POST("/api/v2/auth/password")
    fun post_findPwd(
        @Body parmas: UserData
    ): Call<AuthResponse>

    @POST("/api/v2/auth/deleted-date")
    suspend fun post_deletedDate(
        @Body params: Email
    ): Response<ApiResponse<DeletedDateResponse>>

    @POST("/api/v2/auth/login")
    fun post_login(
        @Body parmas: UserData
    ): Call<AuthResponse>

    @PATCH("/api/v2/auth/password")
    fun patch_modifyPwd(
        @Body parmas: NewPwd
    ): Call<AuthResponse>

    @POST("/api/v2/auth/token")
    fun post_token(
        @Header("Refresh-Token") token: String
    ): Call<TokenResponse>

    @GET("/api/v2/users")
    suspend fun get_users(): Response<ApiResponse<UserProfile>>

    @POST("/api/v2/inquiries")
    suspend fun post_inquiries(
        @Body params: InquiryRequest
    ): Response<ApiResponse<InquiryResponse>>

    @POST("/api/v2/app-version")
    suspend fun post_appVersion(
        @Body params: AppVersionRequest
    ): Response<ApiResponse<AppVersionResponse>>

    @POST("/api/v2/users/image/pre-signed-url")
    suspend fun post_user_image_presigne_url(
        @Body request: UserImagePresignedUrlRequest
    ) : Response<ApiResponse<UserImagePresignedUrlResponse>>

    @PATCH("/api/v2/users/image")
    suspend fun patch_user_profile_image_url(
        @Body request : UpdateUserProfileImageRequest
    ) : Response<ApiResponse<UpdateUserProfileImageResponse>>

    @GET("/api/v2/oauth/{provider}/login")
    suspend fun get_oauth_login(
        @Path("provider") provider: String,
        @Query("id_token") idToken: String,
    ) : Response<ApiResponse<LoginOAuthResponse>>


    @PATCH("/api/v2/oauth/{provider}/withdraw")
    suspend fun patch_oauth_withdraw(
        @Path("provider") provider: String,
        @Query("access_token") accessToken: String,
    ) : Response<ApiResponse<WithdrawOAuthResponse>>


    @POST("/api/v2/oauth/username")
    suspend fun post_oauth_username(
        @Body request : UpdateOAuthUserNameRequest
    ) : Response<ApiResponse<UpdateOAuthUserNameResponse>>
}

