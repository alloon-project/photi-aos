package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeRecordData
import com.example.alloon_aos.data.model.response.EndedChallengeData
import com.example.alloon_aos.data.model.response.FeedByDate
import com.example.alloon_aos.data.model.response.FeedDate
import com.example.alloon_aos.data.model.response.FeedHistoryData
import com.example.alloon_aos.data.model.response.MyChallengeCount
import com.example.alloon_aos.data.model.response.MyChallenges
import com.example.alloon_aos.data.model.response.ProfileImageData
import com.example.alloon_aos.data.remote.UserApiService
import okhttp3.MultipartBody
import retrofit2.Response

class UserRepository(private val apiService: UserApiService) {

    //사용자 참옂 중인 챌린지 조회
    suspend fun getMyChallenges(page: Int, size: Int): Response<ApiResponse<MyChallenges>> {
        return apiService.get_my_challenges(page, size)
    }

    suspend fun getFeeds(): Response<ApiResponse<FeedDate>> {
        return apiService.get_feeds()
    }

    suspend fun getFeedsByDate(date: String): Response<ApiResponse<List<FeedByDate>>> {
        return apiService.get_feeds_by_date(date)
    }

    //인증 횟수 모아보기 리스트
    suspend fun getFeedHistory(page: Int, size: Int): Response<ApiResponse<FeedHistoryData>> {
        return apiService.get_feed_history(page, size)
    }

    //종료된 챌린지 모아보기 리스트
    suspend fun getEndedChallenges(
        page: Int,
        size: Int
    ): Response<ApiResponse<EndedChallengeData>> {
        return apiService.get_ended_challenges(page, size)
    }

    //참여중인 챌린지 갯수(어디서 사용?)
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
