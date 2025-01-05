package com.example.alloon_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeFeedsData
import com.example.alloon_aos.data.model.response.ChallengeInfoData
import com.example.alloon_aos.data.model.response.ChallengeMember
import com.example.alloon_aos.data.model.response.FeedCommentsData
import com.example.alloon_aos.data.model.response.FeedDetailData
import com.example.alloon_aos.data.model.response.SuccessMessageReponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

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
    var challengeId = -1
    var name = ""
    var isPublic = false
    var goal = ""
    var proveTime = ""
    var endDate = ""
    var rules: List<Rule> = listOf()
    var hashtags: List<HashTag> = listOf()
    var imgFile = ""

    fun getData(): MyData {
        var data = MyData(name, isPublic, goal, proveTime, endDate, rules, hashtags)
        return data
    }

    fun setChallengeData(data: MyData) {
        name = data.name
        isPublic = data.isPublic
        goal = data.goal
        proveTime = data.proveTime
        endDate = data.endData
        rules = data.rules
        hashtags = data.hashtags
        imgFile = data.imageUrl.toString()
    }


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
        Comment("aaa", "엄청긴댓글입니다아홉열열하나다여")
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
    private val feedService = RetrofitClient.feedService
    private val feedRepository = FeedRepository(feedService)

    // LiveData 변수들
    private val _challengeFeeds = MutableLiveData<ApiResponse<ChallengeFeedsData>>()
    val challengeFeeds: LiveData<ApiResponse<ChallengeFeedsData>> get() = _challengeFeeds

    private val _challengeInfo = MutableLiveData<ApiResponse<ChallengeInfoData>>()
    val challengeInfo: LiveData<ApiResponse<ChallengeInfoData>> get() = _challengeInfo

    private val _challengeFeedDetail = MutableLiveData<ApiResponse<FeedDetailData>>()
    val challengeFeedDetail: LiveData<ApiResponse<FeedDetailData>> get() = _challengeFeedDetail

    private val _challengeMembers = MutableLiveData<ApiResponse<List<ChallengeMember>>>()
    val challengeMembers: LiveData<ApiResponse<List<ChallengeMember>>> get() = _challengeMembers

    private val _feedComments = MutableLiveData<ApiResponse<FeedCommentsData>>()
    val feedComments: LiveData<ApiResponse<FeedCommentsData>> get() = _feedComments

    private val _updateGoalResponse = MutableLiveData<ApiResponse<SuccessMessageReponse>>()
    val updateGoalResponse: LiveData<ApiResponse<SuccessMessageReponse>> get() = _updateGoalResponse

    private val _postChallengeFeedResponse = MutableLiveData<ApiResponse<SuccessMessageReponse>>()
    val postChallengeFeedResponse: LiveData<ApiResponse<SuccessMessageReponse>> get() = _postChallengeFeedResponse

    private val _postCommentResponse = MutableLiveData<ApiResponse<SuccessMessageReponse>>()
    val postCommentResponse: LiveData<ApiResponse<SuccessMessageReponse>> get() = _postCommentResponse


    // Fetch functions

    fun fetchChallengeFeeds(
        challengeId: Long,
        page: Int = 0,
        size: Int = 10,
        sort: String = "LATEST"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.getChallengeFeeds(challengeId, page, size, sort)
                if (response.isSuccessful) {
                    _challengeFeeds.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchChallengeInfo(challengeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.getChallengeInfo(challengeId)
                if (response.isSuccessful) {
                    _challengeInfo.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchChallengeFeedDetail(challengeId: Long, feedId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.getChallengeFeedDetail(challengeId, feedId)
                if (response.isSuccessful) {
                    _challengeFeedDetail.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchChallengeMembers(challengeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.getChallengeMembers(challengeId)
                if (response.isSuccessful) {
                    _challengeMembers.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchFeedComments(feedId: Long, page: Int = 0, size: Int = 10) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.getFeedComments(feedId, page, size)
                if (response.isSuccessful) {
                    _feedComments.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateGoal(challengeId: Long, goal: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.updateGoal(challengeId, goal)
                _updateGoalResponse.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postChallengeFeed(challengeId: Long, image: MultipartBody.Part, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.postChallengeFeed(challengeId, image, description)
                _postChallengeFeedResponse.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun postComment(challengeId: Long, feedId: Long, comment: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = feedRepository.postComment(challengeId, feedId, comment)
                _postCommentResponse.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}