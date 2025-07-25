package com.photi.aos.data.repository

import com.photi.aos.data.model.response.ApiResponse
import com.photi.aos.data.model.response.ChallengeRecordData
import com.photi.aos.data.model.response.EndedChallengeData
import com.photi.aos.data.model.response.FeedByDate
import com.photi.aos.data.model.response.FeedDate
import com.photi.aos.data.model.response.FeedHistoryData
import com.photi.aos.data.model.response.MyChallengeCount
import com.photi.aos.data.model.response.MyChallenges
import com.photi.aos.data.remote.UserApiService
import retrofit2.Response

class UserRepository(private val apiService: UserApiService) {

    //사용자 참여 중인 챌린지 조회
    suspend fun getMyChallenges(page: Int, size: Int): Response<ApiResponse<MyChallenges>> {
        return apiService.get_my_challenges(page, size)
    }

    suspend fun getFeeds(): Response<ApiResponse<FeedDate>> {
        return apiService.get_feed_date()
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

    suspend fun getChallengesCount(): Response<ApiResponse<MyChallengeCount>> {
        return apiService.get_challenges_count()
    }

    suspend fun getChallengeHistory(): Response<ApiResponse<ChallengeRecordData>> {
        return apiService.get_challenge_history()
    }

}
