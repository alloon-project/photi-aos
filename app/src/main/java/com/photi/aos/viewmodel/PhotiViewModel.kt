package com.photi.aos.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.MyData
import com.photi.aos.data.model.CommendData
import com.photi.aos.data.model.MyChallengeData
import com.photi.aos.data.model.request.HashTag
import com.photi.aos.data.model.request.MemberImg
import com.photi.aos.data.model.request.Rule
import com.photi.aos.data.model.response.ApiResponse
import com.photi.aos.data.model.response.ChallengeContent
import com.photi.aos.data.model.response.ChallengeData
import com.photi.aos.data.model.response.ChallengeListResponse
import com.photi.aos.data.model.response.ChallengeRecordData
import com.photi.aos.data.model.response.ChallengeResponse
import com.photi.aos.data.model.response.ChipListResponse
import com.photi.aos.data.model.response.EndedChallengeContent
import com.photi.aos.data.model.response.FeedByDate
import com.photi.aos.data.model.response.FeedHistoryContent
import com.photi.aos.data.model.response.MyChallengeCount
import com.photi.aos.data.model.response.ProfileImageData
import com.photi.aos.data.model.response.UserProfile
import com.photi.aos.data.paging.AllChallengesPagingSource
import com.photi.aos.data.paging.EndedChallengePagingSource
import com.photi.aos.data.paging.FeedHistoryPagingSource
import com.photi.aos.data.paging.HashChallengePagingSource
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.ChallengeRepository
import com.photi.aos.data.repository.ChallengeRepositoryCallback
import com.photi.aos.data.repository.ErrorHandler
import com.photi.aos.data.repository.FeedRepository
import com.photi.aos.data.repository.UserRepository
import com.photi.aos.data.repository.handleApiCall
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.threeten.bp.LocalDate

data class Item(
    val title: String,
    val date: String,
    val url: String? = null,
    var hashtag: MutableList<String> = mutableListOf()
)

data class ChipItem(val hash: String, var select: Boolean)

class PhotiViewModel : ViewModel() {
    companion object {
        private const val TAG = "PHOTI"
    }

    private val challengeService = RetrofitClient.challengeService
    private val challenge_repository = ChallengeRepository(challengeService)

    private val feedService = RetrofitClient.feedService
    private val feed_repository = FeedRepository(feedService)

    private val userService = RetrofitClient.userService
    private val user_repository = UserRepository(userService)

    val apiResponse = MutableLiveData<ActionApiResponse>()
    val popularResponse = MutableLiveData<ActionApiResponse>()
    val hashListResponse = MutableLiveData<ActionApiResponse>()
    val homeResponse = MutableLiveData<ActionApiResponse>()

    var id = -1
    var name = ""
    var isPublic = true
    var goal = ""
    var proveTime = ""
    var endDate = ""
    var rules: List<Rule> = listOf()
    var hashtags: List<HashTag> = listOf()
    var memberImg: List<MemberImg> = listOf()
    var memberCnt = -1
    var imgFile = ""
    var feedId = -1

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
    }
    fun resetHomeResponseValue() {
        homeResponse.value = ActionApiResponse()
    }
    fun resetPopularResponseValue() {
        popularResponse.value = ActionApiResponse()
    }
    fun resetHashResponseValue() {
        hashListResponse.value = ActionApiResponse()
    }

    fun resetAllResponseValue() {
        apiResponse.value = ActionApiResponse()
        popularResponse.value = ActionApiResponse()
        hashListResponse.value = ActionApiResponse()
        homeResponse.value = ActionApiResponse()
    }


    //userData

    //사용x
    private val _userProfile = MutableLiveData<ApiResponse<UserProfile>?>(null)
    val userProfile: LiveData<ApiResponse<UserProfile>?> = _userProfile


    //사용 o
    private val _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code

    private val _profileImage = MutableLiveData<ProfileImageData?>(null)
    val profileImage: LiveData<ProfileImageData?> = _profileImage

    private val _challengeRecodData = MutableLiveData<ChallengeRecordData?>(null)
    val challengeRecodData: LiveData<ChallengeRecordData?> = _challengeRecodData

    private val _feedCalendarData = MutableLiveData<List<CalendarDay>?>()
    val feedCalendarData: LiveData<List<CalendarDay>?> get() = _feedCalendarData

    private val _feedsByDateData = MutableLiveData<List<FeedByDate>?>()
    val feedsByDateData: LiveData<List<FeedByDate>?> get() = _feedsByDateData

    private val _challengeCount = MutableLiveData<MyChallengeCount?>(null)
    val challengeCount: LiveData<MyChallengeCount?> get() = _challengeCount

    private val _feedUploadPhoto = MutableLiveData<Boolean>()
    val feedUploadPhoto: LiveData<Boolean> get() = _feedUploadPhoto



    fun getData(): MyData {
        var data =
            MyData(name, isPublic, goal, proveTime, endDate, rules, hashtags, memberCnt, memberImg)
        return data
    }

    fun setChallengeData(data: ChallengeData) {
        name = data.name
        isPublic = data.isPublic
        goal = data.goal
        proveTime = data.proveTime
        endDate = data.endDate
        rules = data.rules
        hashtags = data.hashtags
        memberCnt = data.currentMemberCnt
        memberImg = data.memberImages
        imgFile = data.imageUrl
    }

    fun changeToCommendData(data: List<ChallengeData>): ArrayList<CommendData> {
        var listData: ArrayList<CommendData> = arrayListOf()
        for (item in data) {
            val newData = CommendData(
                item.id, item.name, item.imageUrl, item.goal, item.currentMemberCnt,
                item.proveTime, item.endDate, item.hashtags, item.memberImages
            )
            listData.add(newData)
        }
        return listData
    }

    fun changeToCommendDataLast(data: List<ChallengeData>): ArrayList<CommendData> {
        var listData: ArrayList<CommendData> = arrayListOf()
        for (item in data) {
            val newData = CommendData(
                id = item.id,
                name = item.name,
                imageUrl = item.imageUrl,
                endDate = item.endDate,
                hashtags = item.hashtags
            )
            listData.add(newData)
        }
        return listData
    }


    //챌린지 참여 여부
    fun checkUserInChallenge(): Boolean {
        for (id in myIdList) {
            if (this.id == id)
                return true
        }
        return false
    }


    //최신순 챌린지
    private val _latestChallengeData = MutableStateFlow<PagingData<ChallengeData>>(PagingData.empty())
    val latestChallengeData: StateFlow<PagingData<ChallengeData>> = _latestChallengeData

    fun clearLatestChallengeData() {
        _latestChallengeData.value = PagingData.empty()
    }

    fun fetchLatestChallenge() {
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20,enablePlaceholders = false ),
            ) {
                AllChallengesPagingSource(challenge_repository)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _latestChallengeData.value = pagingData
                }
        }
    }


    //API
    fun getChallenge() {
        challenge_repository.getChallenge(
            id,
            object : ChallengeRepositoryCallback<ChallengeResponse> {
                override fun onSuccess(data: ChallengeResponse) {
                    val result = data.code
                    val mes = data.message
                    val data = data.data
                    setChallengeData(data)
                    apiResponse.value = ActionApiResponse(result)
                    Log.d(TAG, "getChallenge: $mes $result")
                }

                override fun onFailure(error: Throwable) {
                    apiResponse.value = ActionApiResponse(ErrorHandler.handle(error))
                }
            })
    }

    fun getChallengeHome() {
        challenge_repository.getChallenge(
            id,
            object : ChallengeRepositoryCallback<ChallengeResponse> {
                override fun onSuccess(data: ChallengeResponse) {
                    val result = data.code
                    val mes = data.message
                    val data = data.data
                    setChallengeData(data)
                    homeResponse.value = ActionApiResponse(result)
                    Log.d(TAG, "getChallengeHome: $mes $result")
                }

                override fun onFailure(error: Throwable) {
                    homeResponse.value = ActionApiResponse(ErrorHandler.handle(error))
                }
            })
    }

    fun getChallengePopular() {
        challenge_repository.getChallengePopular(object :
            ChallengeRepositoryCallback<ChallengeListResponse> {
            override fun onSuccess(data: ChallengeListResponse) {
                val result = data.code
                val mes = data.message
                val data = data.data
                addHotItem(changeToCommendData(data))
                popularResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getChallengePopular: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                popularResponse.value = ActionApiResponse(ErrorHandler.handle(error))
            }
        })
    }

    fun getHashList() {
        challenge_repository.getHashList(object : ChallengeRepositoryCallback<ChipListResponse> {
            override fun onSuccess(data: ChipListResponse) {
                val result = data.code
                val mes = data.message
                val data = data.data
                addHashList(data)
                hashListResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getHashList: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                hashListResponse.value = ActionApiResponse(ErrorHandler.handle(error))
            }
        })
    }


    //인기있는 챌린지
    val hotItemsListData = MutableLiveData<ArrayList<CommendData>>()
    var hotItems: ArrayList<CommendData> = arrayListOf()

    fun addHotItem(list: ArrayList<CommendData>) {
        hotItems = list
        hotItemsListData.value = hotItems
    }

    
    //챌린지 없음
    val _photoItem = MutableLiveData<CommendData>() //현재 item
    fun setCurrentPhoto(photoItem: CommendData) {
        id = photoItem.id
        _photoItem.value = photoItem
    }


    //해시태그별 챌린지
    private val _hashChallengeData = MutableStateFlow<PagingData<ChallengeData>>(PagingData.empty())
    val hashChallengeData: StateFlow<PagingData<ChallengeData>> = _hashChallengeData

    fun clearHashChallengeData() {
        _hashChallengeData.value = PagingData.empty()
    }

    fun fetchChallengeHashtag(hash: String) {
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 20, pageSize = 10,enablePlaceholders = false ),
            ) {
                HashChallengePagingSource(hash, challenge_repository)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _hashChallengeData.value = pagingData
                }
        }
    }


    //해시태그 칩 목록
    val hashChipsListData = MutableLiveData<ArrayList<ChipItem>>()
    var hashChips = arrayListOf<ChipItem>()

    fun addHashList(list: List<HashTag>) {
        hashChips = arrayListOf()
        list.forEach { hashChips.add(ChipItem(it.hashtag, false)) }
        hashChipsListData.value = hashChips
    }

    fun clickAllChip() {
        clearHashChallengeData()
        hashChips.forEach { it.select = false }
        hashChipsListData.value = hashChips
        fetchChallengeHashtag("전체")
    }

    fun clickOneChip(pos: Int, hash: String) {
        clearHashChallengeData()
        hashChips.forEachIndexed { index, chipItem ->
            chipItem.select = (index == pos)
        }
        hashChipsListData.value = hashChips
        fetchChallengeHashtag(hash)
    }


    // 내 챌린지 조회
    var proofItems = arrayListOf<MyChallengeData>()
    var completeItems = arrayListOf<MyChallengeData>()
    val _allItems = MutableLiveData<ArrayList<MyChallengeData>>()
    var allItems = arrayListOf<MyChallengeData>()

    val proofPos = MutableLiveData<Int>() //인증 pos
    var currentPos = 0 //현재 pos
    var currentItem: MyChallengeData? = null
    var completeProof = false

    var myIdList = arrayListOf<Int>()

    fun updateCurrentItem(item: MyChallengeData) {
        currentItem = item
    }

    fun resetMyItems() {
        proofItems = arrayListOf()
        completeItems = arrayListOf()
        allItems = arrayListOf()
        myIdList = arrayListOf()
    }

    fun setMyChallenge(list: List<ChallengeContent>) {
        for (item in list) {
            val newItem = MyChallengeData(item.id, item.name, item.challengeImageUrl, item.proveTime,
                item.endDate, item.hashtags, item.feedImageUrl, item.isProve, item.feedId)
            if (item.isProve)
                completeItems.add(newItem)
            else
                proofItems.add(newItem)
            allItems.add(newItem)
            myIdList.add(item.id)
        }
        _allItems.value = allItems

        if (completeProof) {
            val index = allItems.indexOfFirst { it.id == currentItem!!.id }
            proofPos.value = index
            completeProof = false
        }
    }

    fun fetchMyChallenges() {
        resetMyItems()
        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getMyChallenges(0, 20) },
                onSuccess = { data ->
                    setMyChallenge(data!!.content)
                    _code.postValue("200 OK")
                },
                onFailure = { errorCode ->
                    _code.postValue(errorCode)
                }
            )
        }
    }


    // 내 챌린지 갯수 조회
    fun fetchChallengeCount() {
        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getChallengesCount() },
                onSuccess = { data ->
                    _challengeCount.postValue(data)
                    _code.postValue("200 OK")
                },
                onFailure = { errorCode ->
                    _challengeCount.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }


    //피드 인증
    fun fetchChallengeFeed(image: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            handleApiCall(
                call = { feed_repository.postChallengeFeed(id, image) },
                onSuccess = { data ->
                    _feedUploadPhoto.postValue(true)
                },
                onFailure = { errorCode ->
                    _feedUploadPhoto.postValue(false)
                    _code.postValue(errorCode)
                }
            )
        }
    }



    fun fetchChallengeHistory() {
        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getChallengeHistory() },
                onSuccess = { data ->
                    _challengeRecodData.postValue(data)
                    _code.postValue("200 OK")
                },
                onFailure = { errorCode ->
                    _challengeRecodData.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    fun fetchCalendarData() {
        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getFeeds() },
                onSuccess = { data ->
                    val calendarDayList = data?.list?.mapNotNull { dateString ->
                        try {
                            val localDate = LocalDate.parse(dateString)
                            CalendarDay.from(localDate)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                _feedCalendarData.postValue(calendarDayList)
                            },
                onFailure = { errorCode ->
                    _feedCalendarData.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }



    fun fetchFeedsByDate(date: String) {
        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getFeedsByDate(date)},
                onSuccess = { data: List<FeedByDate>? ->
                    if (data != null) {
                        _feedsByDateData.postValue(data)
                        _code.postValue("200 OK")
                    } else {
                        Log.e("fetchFeedsByDate", "Data is null!")
                        _feedsByDateData.postValue(emptyList())
                        _code.postValue("NO_DATA")
                    }
                },
                onFailure = { errorCode ->
                    _feedsByDateData.postValue(null)
                    _code.postValue(errorCode)
                }
            )
        }
    }

    private val _feedHistoryData = MutableStateFlow<PagingData<FeedHistoryContent>>(PagingData.empty())
    val feedHistoryData: StateFlow<PagingData<FeedHistoryContent>> = _feedHistoryData

    private val _endedChallengeData = MutableStateFlow<PagingData<EndedChallengeContent>>(PagingData.empty())
    val endedChallengeData: StateFlow<PagingData<EndedChallengeContent>> = _endedChallengeData

    fun clearEndedChallengeData() {
        _endedChallengeData.value = PagingData.empty()
    }

    fun clearFeedHistoryData() {
        _feedHistoryData.value = PagingData.empty()
    }

    fun fetchFeedHistory() {
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20, enablePlaceholders = false)
            ) {
                FeedHistoryPagingSource(user_repository)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _feedHistoryData.value = pagingData
                }
        }
    }

    fun fetchEndedChallenges() {
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20, enablePlaceholders = false,)
            ) {
                EndedChallengePagingSource(user_repository)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _endedChallengeData.value = pagingData
                }
        }
    }
}
