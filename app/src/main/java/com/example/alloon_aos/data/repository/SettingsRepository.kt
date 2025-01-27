package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.request.InquiryRequest
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.AuthResponse
import com.example.alloon_aos.data.model.response.InquiryResponse
import com.example.alloon_aos.data.model.response.ProfileImageData
import com.example.alloon_aos.data.model.response.UserProfile
import com.example.alloon_aos.data.remote.ApiService
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsRepository(private val apiService: ApiService) {
    suspend fun getUsers(): Response<ApiResponse<UserProfile>> {
        return apiService.get_users()
    }

    suspend fun postImage(imageFile: MultipartBody.Part): Response<ApiResponse<ProfileImageData>> {
        return apiService.post_image(imageFile)
    }

    suspend fun postInquiries(inquiryRequest: InquiryRequest): Response<ApiResponse<InquiryResponse>> {
        return apiService.post_inquiries(inquiryRequest)
    }

    fun deleteUser(pwd: Map<String, String>, callback: MainRepositoryCallback<AuthResponse>) {
        apiService.patch_deleteUser(pwd).enqueue(object : Callback<AuthResponse> {
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