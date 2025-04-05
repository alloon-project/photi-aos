package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.request.ReportRequest
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.CodeResponse
import com.example.alloon_aos.data.model.response.Comment
import com.example.alloon_aos.data.model.response.EndedChallengeContent
import com.example.alloon_aos.data.model.response.Feed
import com.example.alloon_aos.data.model.response.FeedChallengeData
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.FeedUiItem
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.model.response.VerifiedMemberCount
import com.example.alloon_aos.data.paging.EndedChallengePagingSource
import com.example.alloon_aos.data.paging.feed.ChallengeFeedsPagingSource
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler
import com.example.alloon_aos.data.repository.FeedRepository
import com.example.alloon_aos.data.repository.handleApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Comments(
    val id: String,
    val text: String
)

data class PartyItem(
    val id: String,
    val time: String,
    var text: String = "",
    var isMe: Boolean = false
)

class FeedViewModel : ViewModel() {
    companion object {
        private const val TAG = "FEED"
    }

    private val challengeService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(challengeService)

    val apiResponse = MutableLiveData<ActionApiResponse>()
    val codeResponse = MutableLiveData<ActionApiResponse>()

    fun resetResponse() {
        apiResponse.value = ActionApiResponse()
        codeResponse.value = ActionApiResponse()
    }

    var challengeId = -1
    var invitecode = ""

    var targetId = -1
    var category = ""
    var reason = ""
    var content = ""



    fun deleteChallenge() {
        repository.deleteChallenge(challengeId, object : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "deleteChallenge: $mes $result")
            }
            override fun onFailure(error: Throwable) {
                apiResponse.value = ActionApiResponse(ErrorHandler.handle(error))
            }
        })
    }

    fun getInviteCode() {
        repository.getChallengeCode(challengeId, object : ChallengeRepositoryCallback<CodeResponse> {
            override fun onSuccess(data: CodeResponse) {
                val result = data.code
                val mes = data.message
                invitecode = data.data.invitationCode
                codeResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getInviteCode: $mes $result $invitecode")
            }
            override fun onFailure(error: Throwable) {
                codeResponse.value = ActionApiResponse(ErrorHandler.handle(error))
            }
        })
    }

    /////////////////////////////////////////////////////////


    var id = "myId" //api에서 받아올 나의 아이디
    val isMissionClear = MutableLiveData(false) //api에서 받아올 오늘 미션관련 플래그

    //피드뷰
    val comments = arrayListOf<Comments>(
        Comments("aaa", "이 책 좋네요"),
        Comments("abc", "멋져요"),
        Comments("baa", "와우"),
        Comments("Seul", "우왕굳 ㅋㅋ"),
        Comments("HB", "짱~!"),
        Comments("aaa", "엄청긴댓글입니다아홉열열하나다여"),
        Comments("aaa", "이 책 좋네요"),
        Comments("abc", "멋져요"),
        Comments("aaa", "엄청긴댓글입니다아홉열열하나다여"),
        Comments("긴아이디입니다아아앙아앙", "오늘은 정말 기분 좋은 날이야."),
        Comments("긴아이디입니다아아앙아앙", "행복한 하루가 되길 진심으로 바랍니다.행복한 하루가 되길 진심으로 바랍니다.행복한 하루가 되길 진심으로 바랍니다.행복한 하루가 되길 진심으로 바랍니다.")
    )


    //파티원
    val paryItem = arrayListOf<PartyItem>(
        PartyItem("photi_1", "1", "열심히 운동해서 바디프로필 찍기!!!", true),
        PartyItem("photi_2", "10", "일찍 자고 일찍 일어나기"),
        PartyItem("photi_3", "11223"),
        PartyItem("photi_4", "2", "내용")
    )

    fun updateGoal(newGoal: String) {
        _challengeMembers.value = _challengeMembers.value?.map {
            if (it.isCreator) it.copy(goal = newGoal) else it
        }
    }
    private val feedService = RetrofitClient.feedService
    private val feedRepository = FeedRepository(feedService)

    private val _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code

    private val _challengeFeeds = MutableStateFlow<PagingData<FeedUiItem>>(PagingData.empty())
    val challengeFeeds: StateFlow<PagingData<FeedUiItem>> = _challengeFeeds

    private val _challenge = MutableLiveData<FeedChallengeData?>()
    val challenge: LiveData<FeedChallengeData?> get() = _challenge

    private val _challengeInfo = MutableLiveData<ChallengeInfoData?>()
    val challengeInfo: LiveData<ChallengeInfoData?> get() = _challengeInfo

    private val _challengeFeedDetail = MutableLiveData<FeedDetailData?>()
    val challengeFeedDetail: LiveData<FeedDetailData?> get() = _challengeFeedDetail

    private val _challengeMembers = MutableLiveData<List<ChallengeMember>?>()
    val challengeMembers: LiveData<List<ChallengeMember>?> get() = _challengeMembers

    private val _feedComments = MutableLiveData<List<Comment>?>()
    val feedComments: LiveData<List<Comment>?> get() = _feedComments

    private val _feedVerifiedUserCount = MutableLiveData<Int?>()
    val feedVerifiedUserCount: LiveData<Int?> get() = _feedVerifiedUserCount


    private val _isUserVerifiedToday= MutableLiveData<Boolean?>()
    val isUserVerifiedToday: LiveData<Boolean?> get() = _isUserVerifiedToday


    private val _updateGoalResponse = MutableLiveData<SuccessMessageReponse?>()
    val updateGoalResponse: LiveData<SuccessMessageReponse?> get() = _updateGoalResponse

    private val _postChallengeFeedResponse = MutableLiveData<SuccessMessageReponse?>()
    val postChallengeFeedResponse: LiveData<SuccessMessageReponse?> get() = _postChallengeFeedResponse

    private val _postCommentResponse = MutableLiveData<SuccessMessageReponse?>()
    val postCommentResponse: LiveData<SuccessMessageReponse?> get() = _postCommentResponse

    fun fetchChallenge() {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getChallenge(challengeId) },
                onSuccess = { data ->
                    _challenge.postValue(data)
                },
                onFailure = { errorCode ->
                    _challenge.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun fetchChallengeInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getChallengeInfo(challengeId) },
                onSuccess = { data ->
                    _challengeInfo.postValue(data)
                },
                onFailure = { errorCode ->
                    _challengeInfo.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun clearEndedChallengeData() {
        _challengeFeeds.value = PagingData.empty()
    }

    fun fetchChallengeFeeds( sort: String = "LATEST") {
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20, enablePlaceholders = false)
            ) {
                ChallengeFeedsPagingSource(feedRepository, challengeId, sort)
            }.flow
                // 1) 먼저 Feed -> FeedUiItem.Content 변환
                .map { pagingData ->
                    pagingData.map { feed ->
                        FeedUiItem.Content(feed)
                    }
                }
                // 2) insertSeparators로 날짜가 달라지는 지점에 FeedUiItem.Header 삽입
                .map { pagingData ->
                    pagingData.insertSeparators { before: FeedUiItem.Content?, after: FeedUiItem.Content? ->
                        if (after == null) return@insertSeparators null
                        // 첫 아이템 앞에 헤더를 붙이고 싶다면 before == null 시점에서 넣을 수도 있음
                        if (before == null) {
                            // 예: 리스트의 첫 아이템 앞에 무조건 헤더를 달고 싶다면
                            return@insertSeparators FeedUiItem.Header(
                                formatHeader(after.feed.createdDateTime)
                            )
                        }

                        // 날짜 비교 (연월일만 비교하는 간단 예시)
                        val beforeDate = before.feed.createdDateTime.substring(0, 10) // "2025-03-29"
                        val afterDate = after.feed.createdDateTime.substring(0, 10)

                        if (beforeDate != afterDate) {
                            // 날짜가 달라졌다면 새 헤더 삽입
                            FeedUiItem.Header(formatHeader(after.feed.createdDateTime))
                        } else {
                            null
                        }
                    }
                }
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _challengeFeeds.value = pagingData
                }
        }
    }

    private fun formatHeader(dateString: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))
        val inputDate = Instant.from(formatter.parse(dateString))

        val now = Instant.now()
        val duration = Duration.between(inputDate, now)


        return when {
            duration.toDays() <= 1 -> "오늘"
            else -> "${duration.toDays()}일전"
        }
    }


    fun fetchChallengeFeedDetail(feedId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getChallengeFeedDetail(challengeId, feedId) },
                onSuccess = { data ->
                    _challengeFeedDetail.postValue(data)
                },
                onFailure = { errorCode ->
                    _challengeFeedDetail.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun fetchChallengeMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getChallengeMembers(challengeId) },
                onSuccess = { data ->
                    _challengeMembers.postValue(data)
                },
                onFailure = { errorCode ->
                    _challengeMembers.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun fetchFeedComments(feedId: Int, page: Int = 0, size: Int = 10) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getFeedComments(feedId, page, size) },
                onSuccess = { data ->
                    if(data != null)
                     _feedComments.postValue(data.content)
                },
                onFailure = { errorCode ->
                    _feedComments.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun sendReport(id: Int) {
        viewModelScope.launch {
            handleApiCall(
                call = { feedRepository.postReport(id, ReportRequest(category, reason, content)) },
                onSuccess = { data ->
                    //_code.value = "201 CREATED"
                },
                onFailure = { errorCode ->
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun addFeedLike(feedId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            handleApiCall(
                call = { feedRepository.postFeedLike(challengeId, feedId) },
                onSuccess = { data ->
                    _code.value = "201 CREATED"
                    onComplete()
                },
                onFailure = { errorCode ->
                    _code.postValue(errorCode)
                    onComplete()
                }
            )
        }
    }

    fun removeFeedLike(feedId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            handleApiCall(
                call = { feedRepository.deleteFeedLike(challengeId, feedId) },
                onSuccess = { data ->
                    _code.value = "201 CREATED"
                    onComplete()
                },
                onFailure = { errorCode ->
                    _code.postValue(errorCode)
                    onComplete()
                }
            )
        }
    }

    fun fetchVerifiedMemberCount() {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getVerifiedMemberCount(challengeId) },
                onSuccess = { data ->
                    if(data != null)
                        _feedVerifiedUserCount.postValue(data.feedMemberCnt)
                },
                onFailure = { errorCode ->
                    _feedVerifiedUserCount.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun fetchIsUserVerifiedToday() {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getIsUserVerifiedToday(challengeId) },
                onSuccess = { data ->
                    if(data != null)
                        _isUserVerifiedToday.postValue(data.isProve)
                },
                onFailure = { errorCode ->
                    _isUserVerifiedToday.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

//    fun postChallengeFeed(challengeId: Long, image: MultipartBody.Part, description: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            handleApiCall(
//                call = { feedRepository.postChallengeFeed(challengeId, image, description) },
//                onSuccess = { data ->
//                    _postChallengeFeedResponse.postValue(data)
//                },
//                onFailure = { errorCode ->
//                    _postChallengeFeedResponse.postValue(null)
//                    _code.postValue(errorCode)
//                }
//            )
//        }
//    }
//
//    fun postComment(feedId: Int, comment: Map<String, String>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            handleApiCall(
//                call = { feedRepository.postComment(challengeId, feedId, comment) },
//                onSuccess = { data ->
//                    _postCommentResponse.postValue(data)
//                },
//                onFailure = { errorCode ->
//                    _postCommentResponse.postValue(null)
//                    _code.postValue(errorCode)
//                }
//            )
//        }
//    }
}