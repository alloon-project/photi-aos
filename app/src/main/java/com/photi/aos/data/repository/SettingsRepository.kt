package com.photi.aos.data.repository

import com.photi.aos.data.model.request.InquiryRequest
import com.photi.aos.data.model.response.ApiResponse
import com.photi.aos.data.model.response.AuthResponse
import com.photi.aos.data.model.response.InquiryResponse
import com.photi.aos.data.model.response.ProfileImageData
import com.photi.aos.data.model.response.UpdateUserProfileImageRequest
import com.photi.aos.data.model.response.UpdateUserProfileImageResponse
import com.photi.aos.data.model.response.UserImagePresignedUrlRequest
import com.photi.aos.data.model.response.UserImagePresignedUrlResponse
import com.photi.aos.data.model.response.UserProfile
import com.photi.aos.data.model.response.WithdrawOAuthResponse
import com.photi.aos.data.model.response.WithdrawOAuthUserRequest
import com.photi.aos.data.remote.AuthService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class SettingsRepository(private val authService: AuthService) {
    suspend fun getUsers(): Response<ApiResponse<UserProfile>> {
        return authService.get_users()
    }

    suspend fun postUserImagePresignedUrl(imageName : String): Response<ApiResponse<UserImagePresignedUrlResponse>> {

         return  authService.post_user_image_presigne_url(
            UserImagePresignedUrlRequest(imageName)
        )


    }

    suspend fun patchUserProfileImage(presignedUrl : String) : Response<ApiResponse<UpdateUserProfileImageResponse>>{
        return authService.patch_user_profile_image_url(
            UpdateUserProfileImageRequest(
                presignedUrl
            )
        )
    }



    suspend fun postInquiries(inquiryRequest: InquiryRequest): Response<ApiResponse<InquiryResponse>> {
        return authService.post_inquiries(inquiryRequest)
    }

    suspend fun withdrawOauth(provider: String, sub: String): Response<ApiResponse<WithdrawOAuthResponse>> {
        return authService.patch_oauth_withdraw(WithdrawOAuthUserRequest(provider = provider, sub = sub))
    }

    fun deleteUser(pwd: Map<String, String>, callback: MainRepositoryCallback<AuthResponse>) {
        authService.patch_deleteUser(pwd).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body()!!)
                } else {
                    val error = response.errorBody()?.string()!!
                    callback.onFailure(Throwable(error))
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }
}