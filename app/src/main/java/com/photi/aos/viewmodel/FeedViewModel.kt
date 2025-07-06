package com.photi.aos.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.response.ChallengeInfoData
import com.photi.aos.data.model.response.ChallengeMember
import com.photi.aos.data.model.response.CodeResponse
import com.photi.aos.data.model.response.Comment
import com.photi.aos.data.model.response.CommentRequest
import com.photi.aos.data.model.response.FeedChallengeData
import com.photi.aos.data.model.response.FeedDetailData
import com.photi.aos.data.model.response.FeedUiItem
import com.photi.aos.data.model.response.MessageResponse
import com.photi.aos.data.model.response.SuccessMessageReponse
import com.photi.aos.data.paging.feed.ChallengeFeedsPagingSource
import com.photi.aos.data.paging.feed.FeedCommentsPagingSource
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.ChallengeRepository
import com.photi.aos.data.repository.ChallengeRepositoryCallback
import com.photi.aos.data.repository.ErrorHandler
import com.photi.aos.data.repository.FeedRepository
import com.photi.aos.data.repository.handleApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    var feedId = -1
    var memberId = -1


    fun deleteChallenge() {
        repository.deleteChallenge(challengeId, object : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.postValue( ActionApiResponse(result))
                Log.d(TAG, "deleteChallenge: $mes $result")
            }
            override fun onFailure(error: Throwable) {
                apiResponse.postValue(ActionApiResponse(ErrorHandler.handle(error)))
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
    val isMissionClear = MutableLiveData(false) //api에서 받아올 오늘 미션관련 플래그



    //파티원
    val paryItem = arrayListOf<PartyItem>(
        PartyItem("photi_1", "1", "열심히 운동해서 바디프로필 찍기!!!", true),
        PartyItem("photi_2", "10", "일찍 자고 일찍 일어나기"),
        PartyItem("photi_3", "11223"),
        PartyItem("photi_4", "2", "내용")
    )

    fun updateGoal(newGoal: String, myName: String) {
        _challengeMembers.value = _challengeMembers.value?.map {
            if (it.username == myName) it.copy(goal = newGoal) else it
        }
    }
    private val feedService = RetrofitClient.feedService
    private val feedRepository = FeedRepository(feedService)

    private val _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code

    private val _deleteFeedCode = MutableLiveData<Int>()
    val deleteFeedCode: LiveData<Int> get() = _deleteFeedCode

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

    private val _feedComments =  MutableStateFlow<PagingData<Comment>>(PagingData.empty())
    val feedComments: StateFlow<PagingData<Comment>> get() = _feedComments

    private val _scrollToBottom = MutableStateFlow(false)
    val scrollToBottom: StateFlow<Boolean> = _scrollToBottom.asStateFlow()

    private val _feedVerifiedUserCount = MutableLiveData<Int?>()
    val feedVerifiedUserCount: LiveData<Int?> get() = _feedVerifiedUserCount


    private val _isUserVerifiedToday= MutableLiveData<Boolean?>()
    val isUserVerifiedToday: LiveData<Boolean?> get() = _isUserVerifiedToday


    private val _isVerifiedFeedExist= MutableLiveData<Boolean?>()
    val isVerifiedFeedExistence: LiveData<Boolean?> get() = _isVerifiedFeedExist



    private val _updateGoalResponse = MutableLiveData<SuccessMessageReponse?>()
    val updateGoalResponse: LiveData<SuccessMessageReponse?> get() = _updateGoalResponse

    private val _postChallengeFeedResponse = MutableLiveData<SuccessMessageReponse?>()
    val postChallengeFeedResponse: LiveData<SuccessMessageReponse?> get() = _postChallengeFeedResponse

    private val _postCommentResponse = MutableLiveData<Comment?>()
    val postCommentResponse: LiveData<Comment?> get() = _postCommentResponse

    private val _deleteCommentResponse = MutableLiveData<SuccessMessageReponse?>()
    val deleteCommentResponse: LiveData<SuccessMessageReponse?> get() = _deleteCommentResponse

    private val _feedUploadPhoto = MutableLiveData<Boolean?>()
    val feedUploadPhoto: LiveData<Boolean?> get() = _feedUploadPhoto


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

    @RequiresApi(Build.VERSION_CODES.O)
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

    fun fetchFeedComments(feedId: Int) {
        _scrollToBottom.value = true
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 10, pageSize = 5,enablePlaceholders = false ),
            ) {
                FeedCommentsPagingSource(feedRepository,feedId)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _feedComments.value = pagingData
                }
        }
        viewModelScope.launch(Dispatchers.IO) {


        }
    }

    fun refreshFeedComments(feedId: Int) {
        _scrollToBottom.value = true
        _feedComments.value = PagingData.empty()
        fetchFeedComments(feedId)
    }



    fun fetchChallengeFeed(image: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.postChallengeFeed(challengeId, image) },
                onSuccess = { data ->
                    _feedUploadPhoto.postValue(true)
                },
                onFailure = { errorCode ->
                    _feedUploadPhoto.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }


    fun addFeedLike(feedId: Int, onComplete: () -> Unit,onFailure: () -> Unit) {
        viewModelScope.launch {
            handleApiCall(
                call = { feedRepository.postFeedLike(challengeId, feedId) },
                onSuccess = { data ->
                    _code.postValue( "201 CREATED")
                    onComplete()
                },
                onFailure = { errorCode ->
                    _code.postValue(errorCode)
                    onFailure()
                }
            )
        }
    }

    fun removeFeedLike(feedId: Int, onComplete: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            handleApiCall(
                call = { feedRepository.deleteFeedLike(challengeId, feedId) },
                onSuccess = { data ->
                    _code.postValue("201 CREATED")
                    onComplete()
                },
                onFailure = { errorCode ->
                    _code.postValue(errorCode)
                    onFailure()
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

    fun fetchIsVerifiedFeedExist() {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getIsVerifiedFeedExist(challengeId) },
                onSuccess = { data ->
                    if(data != null)
                        _isVerifiedFeedExist.postValue(data.hasFeed)
                },
                onFailure = { errorCode ->
                    _isVerifiedFeedExist.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.deleteFeed(challengeId, feedId = feedId ) },
                onSuccess = { data ->
                    Log.d("feedViewModel","data : $data ")
                    _deleteFeedCode.postValue(feedId)
                },
                onFailure = { errorCode ->
                    Log.d("feedViewModel","errorCode : $errorCode ")
                   // _deleteFeedCode.postValue(errorCode)
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
    fun postComment(feedId: Int, comment: String,myId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.postComment(challengeId, feedId, CommentRequest(comment = comment)) },
                onSuccess = { data ->
                    if (data != null) {
                        _postCommentResponse.postValue(Comment(id = data.id.toLong(), username = myId, comment = comment))
                    }
                    refreshFeedComments(feedId)
                },
                onFailure = { errorCode ->
                    _postCommentResponse.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun consumeScrollFlag() { _scrollToBottom.value = false }
    fun consumePostedComment() { _postCommentResponse.value = null }

    fun deleteComment(feedId: Int, commentId: Int,  onComplete: () -> Unit,onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.deleteComment(challengeId, feedId, commentId) },
                onSuccess = { data ->
                    if (data != null) {
                        onComplete()                   }
                },
                onFailure = { errorCode ->
                    onFailure()
                }
            )
        }
    }
}