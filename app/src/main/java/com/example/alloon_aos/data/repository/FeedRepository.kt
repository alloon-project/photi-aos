package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.FeedChallengeData
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.remote.FeedApiService
import okhttp3.MultipartBody
import retrofit2.Response

class FeedRepository(private val apiService: FeedApiService) {

    // 챌린지 피드 조회
    suspend fun getChallengeFeeds(
        challengeId: Int,
        page: Int = 0,
        size: Int = 10,
        sort: String = "LATEST"
    ): Response<ApiResponse<ChallengeFeedsData>> {
        return apiService.get_challengeFeeds(challengeId, page, size, sort)
    }

    // 챌린지 조회
    suspend fun getChallenge(challengeId: Int): Response<ApiResponse<FeedChallengeData>> {
        return apiService.get_challenge(challengeId)
    }

    // 챌린지 정보 조회
    suspend fun getChallengeInfo(challengeId: Int): Response<ApiResponse<ChallengeInfoData>> {
        return apiService.get_challengeInfo(challengeId)
    }

    // 챌린지 개별 피드 조회
    suspend fun getChallengeFeedDetail(
        challengeId: Int,
        feedId: Int
    ): Response<ApiResponse<FeedDetailData>> {
        return apiService.get_challengeFeedDetail(challengeId, feedId)
    }

    // 챌린지 멤버 조회
    suspend fun getChallengeMembers(challengeId: Int): Response<ApiResponse<List<ChallengeMember>>> {
        return apiService.get_challengeMembers(challengeId)
    }

    // 피드 댓글 리스트 조회
    suspend fun getFeedComments(
        feedId: Int,
        page: Int = 0,
        size: Int = 10
    ): Response<ApiResponse<FeedCommentsData>> {
        return apiService.get_feedComments(feedId, page, size)
    }

    // 챌린지 개인 목표 업데이트
    suspend fun updateGoal(
        challengeId: Int,
        goal: Map<String, String>
    ): ApiResponse<SuccessMessageReponse> {
        return apiService.updateGoal(challengeId, goal)
    }

    // 챌린지 인증 등록
    suspend fun postChallengeFeed(
        challengeId: Int,
        image: MultipartBody.Part,
        description: String
    ): ApiResponse<SuccessMessageReponse> {
        return apiService.postChallengeFeed(challengeId, image, description)
    }

    // 댓글 등록
    suspend fun postComment(
        challengeId: Int,
        feedId: Int,
        comment: Map<String, String>
    ): ApiResponse<SuccessMessageReponse> {
        return apiService.postComment(challengeId, feedId, comment)
    }
}
