package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.response.*
import com.example.alloon_aos.data.remote.UserApiService
import okhttp3.MultipartBody
import retrofit2.Response

class UserRepository(private val apiService: UserApiService) {

    suspend fun getUsers(): Response<ApiResponse<UserProfile>> {
        return apiService.get_users()
    }

    suspend fun getMyChallenges(page: Int, size: Int): Response<ApiResponse<MyChallenges>> {
        return apiService.get_my_challenges(page, size)
    }

    suspend fun getFeeds(): Response<ApiResponse<FeedDate>> {
        return apiService.get_feeds()
    }

    suspend fun getFeedsByDate(date: String): ApiResponse<List<FeedByDate>> {
        return apiService.get_feeds_by_date(date)
    }

    suspend fun getFeedHistory(page: Int, size: Int): Response<ApiResponse<FeedHistoryData>> {
        return apiService.get_feed_history(page, size)
    }

    suspend fun getEndedChallenges(page: Int, size: Int): Response<ApiResponse<EndedChallengeData>> {
        return apiService.get_ended_challenges(page, size)
    }

    suspend fun getChallenges(): Response<ApiResponse<MyChallengeCount>> {
        return apiService.get_challenges()
    }

    suspend fun getChallengeHistory(): Response<ApiResponse<ChallengeRecordData>> {
        return apiService.get_challenge_history()
    }

    suspend fun postImage(imageFile: MultipartBody.Part): Response<ApiResponse<ProfileImageData>> {
        return apiService.post_image(imageFile)
    }
}
