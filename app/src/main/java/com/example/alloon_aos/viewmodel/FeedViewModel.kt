package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.MemberImg
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.CodeResponse
import com.example.alloon_aos.data.model.response.FeedChallengeData
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.MessageResponse
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler
import com.example.alloon_aos.data.repository.FeedRepository
import com.example.alloon_aos.data.repository.handleApiCall
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
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
    val comments: ArrayList<Comment> = ArrayList()
)

data class Comment(
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


    var challengeId = -1
    var name = ""
    var isPublic = false
    var goal = ""
    var proveTime = ""
    var endDate = ""
    var rules: List<Rule> = listOf()
    var hashtags: List<HashTag> = listOf()
    var imgFile = ""
    var currentMemberCnt = 0
    var memberImages: List<MemberImg> = listOf()
    var invitecode = ""

    fun getData(): MyData {
        var data = MyData(name, isPublic, goal, proveTime, endDate, rules, hashtags)
        return data
    }

    fun setChallengeData(data: MyData) {
        name = data.name
        isPublic = data.isPublic
        goal = data.goal
        proveTime = data.proveTime
        endDate = data.endDate
        rules = data.rules
        hashtags = data.hashtags
        imgFile = data.imageUrl.toString()
    }

    fun deleteChallenge() {
        repository.deleteChallenge(challengeId, object : ChallengeRepositoryCallback<MessageResponse> {
            override fun onSuccess(data: MessageResponse) {
                val result = data.code
                val mes = data.message
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "deleteChallenge: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun getInviteCode() {
        repository.getChallengeCode(challengeId, object : ChallengeRepositoryCallback<CodeResponse> {
            override fun onSuccess(data: CodeResponse) {
                val result = data.code
                val mes = data.message
                invitecode = data.data.invitationCode
                //apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getInviteCode: $mes $result $invitecode")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        apiResponse.value = ActionApiResponse(errorCode)
    }

    /////////////////////////////////////////////////////////


    var id = "myId" //api에서 받아올 나의 아이디
    val isMissionClear = MutableLiveData(false) //api에서 받아올 오늘 미션관련 플래그

    //피드뷰
    val comments = arrayListOf<Comment>(
        Comment("aaa", "이 책 좋네요"),
        Comment("abc", "멋져요"),
        Comment("baa", "와우"),
        Comment("Seul", "우왕굳 ㅋㅋ"),
        Comment("HB", "짱~!"),
        Comment("aaa", "엄청긴댓글입니다아홉열열하나다여"),
        Comment("aaa", "이 책 좋네요"),
        Comment("abc", "멋져요"),
        Comment("aaa", "엄청긴댓글입니다아홉열열하나다여"),
        Comment("긴아이디입니다아아앙아앙", "오늘은 정말 기분 좋은 날이야."),
        Comment("긴아이디입니다아아앙아앙", "행복한 하루가 되길 진심으로 바랍니다.행복한 하루가 되길 진심으로 바랍니다.행복한 하루가 되길 진심으로 바랍니다.행복한 하루가 되길 진심으로 바랍니다.")
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

    private val _challengeFeeds = MutableLiveData<ChallengeFeedsData?>()
    val challengeFeeds: MutableLiveData<ChallengeFeedsData?> get() = _challengeFeeds

    private val _challenge = MutableLiveData<FeedChallengeData?>()
    val challenge: LiveData<FeedChallengeData?> get() = _challenge

    private val _challengeInfo = MutableLiveData<ChallengeInfoData?>()
    val challengeInfo: LiveData<ChallengeInfoData?> get() = _challengeInfo

    private val _challengeFeedDetail = MutableLiveData<FeedDetailData?>()
    val challengeFeedDetail: LiveData<FeedDetailData?> get() = _challengeFeedDetail

    private val _challengeMembers = MutableLiveData<List<ChallengeMember>?>()
    val challengeMembers: LiveData<List<ChallengeMember>?> get() = _challengeMembers

    private val _feedComments = MutableLiveData<FeedCommentsData?>()
    val feedComments: LiveData<FeedCommentsData?> get() = _feedComments

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

    fun fetchChallengeFeeds(
        page: Int = 0,
        size: Int = 10,
        sort: String = "LATEST"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feedRepository.getChallengeFeeds(challengeId, page, size, sort) },
                onSuccess = { data ->
                    _challengeFeeds.postValue(data)
                },
                onFailure = { errorCode ->
                    _challengeFeeds.postValue(null)
                    _code.postValue(errorCode)
                }
            )
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
                    _feedComments.postValue(data)
                },
                onFailure = { errorCode ->
                    _feedComments.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

//    fun updateGoal( goal: Map<String, String>) {
//        viewModelScope.launch(Dispatchers.IO) {
//            handleApiCall(
//                call = { feedRepository.updateGoal(challengeId, goal) },
//                onSuccess = { data ->
//                    _updateGoalResponse.postValue(data)
//                },
//                onFailure = { errorCode ->
//                    _updateGoalResponse.postValue(null)
//                    _code.postValue(errorCode)
//                }
//            )
//        }
//    }

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