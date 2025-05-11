package com.example.alloon_aos.data.repository

import com.example.alloon_aos.data.model.request.ReportRequest
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.FeedChallengeData
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.model.response.UserVerificationStatus
import com.example.alloon_aos.data.model.response.VerifiedFeedExistence
import com.example.alloon_aos.data.model.response.VerifiedMemberCount
import com.example.alloon_aos.data.remote.FeedApiService
import okhttp3.MultipartBody
import retrofit2.Response

class FeedRepository(private val apiService: FeedApiService) {

    // 챌린지 피드 조회
    suspend fun getChallengeFeeds(
        challengeId: Int,
        page: Int,
        size: Int,
        sort: String,
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
        image: MultipartBody.Part
    ): Response<ApiResponse<SuccessMessageReponse>> {
        return apiService.postChallengeFeed(challengeId, image)
    }

    // 댓글 등록
    suspend fun postComment(
        challengeId: Int,
        feedId: Int,
        comment: Map<String, String>
    ): ApiResponse<SuccessMessageReponse> {
        return apiService.postComment(challengeId, feedId, comment)
    }

    // 신고 등록
    suspend fun postReport(
        targetId: Int,
        reportRequest: ReportRequest
    ): Response<ApiResponse<SuccessMessageReponse>> {
        return apiService.post_reports(targetId, reportRequest)
    }

    suspend fun postFeedLike(
        challengeId: Int,
        feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>> {
        return apiService.postFeedLike(challengeId, feedId)
    }

    suspend fun deleteFeedLike(
        challengeId: Int,
        feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>> {
        return apiService.deleteFeedLike(challengeId, feedId)
    }

    suspend fun getVerifiedMemberCount(
        challengeId: Int,
    ): Response<ApiResponse<VerifiedMemberCount>> {
        return apiService.get_verifiedMemberCount(challengeId)
    }

    suspend fun getIsUserVerifiedToday(
        challengeId: Int,
    ): Response<ApiResponse<UserVerificationStatus>> {
        return apiService.get_is_user_verified_today(challengeId)
    }

    suspend fun getIsVerifiedFeedExist(
        challengeId: Int,
    ): Response<ApiResponse<VerifiedFeedExistence>> {
        return apiService.get_is_verified_feed_exist(challengeId)
    }


    suspend fun deleteFeed(
        challengeId: Int,
        feedId: Int,
    ): Response<ApiResponse<SuccessMessageReponse>> {
        return apiService.delete_feed(challengeId,feedId)
    }
}
