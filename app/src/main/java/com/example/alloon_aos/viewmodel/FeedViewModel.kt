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
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.paging.EndedChallengePagingSource
import com.example.alloon_aos.data.paging.feed.ChallengeFeedsPagingSource
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler
import com.example.alloon_aos.data.repository.FeedRepository
import com.example.alloon_aos.data.repository.handleApiCall
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.threeten.bp.LocalDate

data class FeedOutItem(
    val daysAgo: Int,
    val feedInItems: ArrayList<FeedInItem> = ArrayList()
)

data class FeedInItem(
    val id: String,
    val time: String,
    var url: String? = null,
    var isClick: Boolean, //본인이 하트를 눌렀는지
    var heartCnt: Int = 0, //다른 사용자들이 누른 하트 수
    val comments: ArrayList<Comments> = ArrayList()
)

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

    val feedInItems = arrayListOf<FeedInItem>(
        FeedInItem("photi", "방금", "https://ifh.cc/g/6HRkxa.jpg", true, 2, comments),
        FeedInItem("seul", "1분전", "https://ifh.cc/g/AA0NMd.jpg", false, 5, comments),
        FeedInItem("HB", "18분전", "https://ifh.cc/g/09y6Mo.jpg", false, 10),
        FeedInItem("photi1", "30분전", "https://ifh.cc/g/KB2Vh1.jpg", false, 1, comments),
        FeedInItem("photi2", "방금", "https://ifh.cc/g/yxgmBH.webp", false),
    )

    val feedOutItems = arrayListOf<FeedOutItem>(
        FeedOutItem(0, feedInItems),
        FeedOutItem(1, feedInItems),
        FeedOutItem(2, feedInItems),
        FeedOutItem(3, feedInItems)
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

    private val _challengeFeeds = MutableStateFlow<PagingData<Feed>>(PagingData.empty())
    val challengeFeeds: StateFlow<PagingData<Feed>> = _challengeFeeds

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

    fun fetchChallengeFeeds() {
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20, enablePlaceholders = false,)
            ) {
                ChallengeFeedsPagingSource(feedRepository,challengeId = challengeId)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _challengeFeeds.value = pagingData
                }
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