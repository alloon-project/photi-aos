package com.example.alloon_aos.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.CommendData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.MemberImg
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.ApiResponse
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.model.response.ChallengeListResponse
import com.example.alloon_aos.data.model.response.ChallengeRecordData
import com.example.alloon_aos.data.model.response.ChallengeResponse
import com.example.alloon_aos.data.model.response.EndedChallengeContent
import com.example.alloon_aos.data.model.response.EndedChallengeData
import com.example.alloon_aos.data.model.response.FeedByDate
import com.example.alloon_aos.data.model.response.FeedHistoryContent
import com.example.alloon_aos.data.model.response.FeedHistoryData
import com.example.alloon_aos.data.model.response.MyChallengeCount
import com.example.alloon_aos.data.model.response.PagingListResponse
import com.example.alloon_aos.data.model.response.ProfileImageData
import com.example.alloon_aos.data.model.response.UserProfile
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler
import com.example.alloon_aos.data.repository.UserRepository
import com.example.alloon_aos.data.repository.handleApiCall
import com.prolificinteractive.materialcalendarview.CalendarDay
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
data class ChallengeItem(
    val title: String,
    val data: String,
    val time: String,
    val content: String,
    val url: String? = null,
    var hashtag: MutableList<String>,
    val member: String
)

data class ProofShotItem(
    val title: String,
    val date: String,
    val time: String,
    var url: Uri? = null,
    var hashtag: MutableList<String>
)

class PhotiViewModel : ViewModel() {
    companion object {
        private const val TAG = "PHOTI"
    }

    private val challengeService = RetrofitClient.challengeService
    private val challenge_repository = ChallengeRepository(challengeService)

    private val userService = RetrofitClient.userService
    private val user_repository = UserRepository(userService)

    val apiResponse = MutableLiveData<ActionApiResponse>()
    val popularResponse = MutableLiveData<ActionApiResponse>()
    val latestResponse = MutableLiveData<ActionApiResponse>()
    val hashResponse = MutableLiveData<ActionApiResponse>()
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

    var latestPage = 0
    var lastLatestPage = false
    var hashPage = 0
    var lastHashPage = false
    var hashtag = "전체"

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
    }
    fun resetHomeResponseValue() {
        homeResponse.value = ActionApiResponse()
    }
    fun resetPopularResponseValue() {
        popularResponse.value = ActionApiResponse()
    }
    fun resetLatestResponseValue() {
        latestResponse.value = ActionApiResponse()
        latestPage = 0
        latestItems = arrayListOf()
        lastLatestPage = false
    }
    fun resetHashResponseValue() {
        hashListResponse.value = ActionApiResponse()
        hashResponse.value = ActionApiResponse()
        hashPage = 0
        hashItems = arrayListOf()
        hashtag = "전체"
        lastHashPage = false
    }

    fun resetAllResponseValue() {
        apiResponse.value = ActionApiResponse()
        popularResponse.value = ActionApiResponse()
        latestResponse.value = ActionApiResponse()
        hashResponse.value = ActionApiResponse()
        hashListResponse.value = ActionApiResponse()
        homeResponse.value = ActionApiResponse()
        resetPaging()
    }

    fun resetPaging() {
        latestPage = 0
        latestItems = arrayListOf()
        hashPage = 0
        hashItems = arrayListOf()
        hashtag = "전체"
        lastLatestPage = false
        lastHashPage = false
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

    private val _endedChallenges = MutableLiveData<MutableList<EndedChallengeContent>>()
    val endedChallenges: LiveData<MutableList<EndedChallengeContent>> get() = _endedChallenges

    private val _feedHistoryData = MutableLiveData<MutableList<FeedHistoryContent>>()
    val feedHistoryData: LiveData<MutableList<FeedHistoryContent>> get() = _feedHistoryData

    private val _challengeCount = MutableLiveData<MyChallengeCount?>(null)
    val challengeCount: LiveData<MyChallengeCount?> get() = _challengeCount



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


    fun getChallengeLatest() {
        challenge_repository.getChallengeLatest(
            latestPage,
            10,
            object : ChallengeRepositoryCallback<PagingListResponse> {
                override fun onSuccess(data: PagingListResponse) {
                    val result = data.code
                    val mes = data.message
                    val data = data.data
                    addLatestItem(changeToCommendDataLast(data.content))
                    latestResponse.value = ActionApiResponse(result)
                    Log.d(TAG, "getChallengeLatest: $mes $result")
                }

                override fun onFailure(error: Throwable) {
                    latestResponse.value = ActionApiResponse(ErrorHandler.handle(error))
                }
            })
    }

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

    }

    fun getChallengeHashtag() {
        challenge_repository.getChallengeHashtag(
            hashtag,
            hashPage,
            10,
            object : ChallengeRepositoryCallback<PagingListResponse> {
                override fun onSuccess(data: PagingListResponse) {
                    val result = data.code
                    val mes = data.message
                    val data = data.data
                    addHashItem(changeToCommendDataLast(data.content))
                    hashResponse.value = ActionApiResponse(result)
                    Log.d(TAG, "getChallengeHashtag: $mes $result")
                }

                override fun onFailure(error: Throwable) {
                    hashResponse.value = ActionApiResponse(ErrorHandler.handle(error))
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


    //최신순 챌린지
    val latestItemsListData = MutableLiveData<ArrayList<CommendData>>()
    var latestItems: ArrayList<CommendData> = arrayListOf()

    fun addLatestItem(list: ArrayList<CommendData>) {
        latestItems.addAll(list)
        latestItemsListData.value = latestItems
        if (list.size < 10)
            lastLatestPage = true
    }


    //해시태그별 챌린지
    val hashItemsListData = MutableLiveData<ArrayList<CommendData>>()
    var hashItems: ArrayList<CommendData> = arrayListOf()

    fun addHashItem(list: ArrayList<CommendData>) {
        hashItems.addAll(list)
        hashItemsListData.value = hashItems
        if (list.size < 10)
            lastHashPage = true
    }


    //해시태그 칩 목록
    val hashChipsListData = MutableLiveData<ArrayList<ChipItem>>()
    var hashChips = arrayListOf<ChipItem>(
        ChipItem("러닝", false),
        ChipItem("취뽀", false),
        ChipItem("독서", false),
        ChipItem("맛집", false),
        ChipItem("안드로이드", false)
    )

    fun addHashList(list: ArrayList<HashTag>) {
        hashChips = arrayListOf()
        list.forEach { hashChips.add(ChipItem(it.hashtag, false)) }
        hashChipsListData.value = hashChips
    }

    fun clickAllChip() {
        hashChips.forEach { it.select = false }
        hashChipsListData.value = hashChips
        hashPage = 0
        hashtag = "전체"
        hashItems = arrayListOf()
        getChallengeHashtag()
    }

    fun clickOneChip(pos: Int, hash: String) {
        hashChips.forEachIndexed { index, chipItem ->
            chipItem.select = (index == pos)
        }
        hashChipsListData.value = hashChips
        hashPage = 0
        hashItems = arrayListOf()
        hashtag = hash
        getChallengeHashtag()
    }


    // 내 챌린지 조회
    val proofPos = MutableLiveData<Int>() //현재 item
    val proofItems = arrayListOf(
        ProofShotItem("영화 챌린지", "~ 2024. 12. 1", "10시까지", null, mutableListOf("영화관람")),
        ProofShotItem("면접 연습하기", "~ 2024. 8. 22", "8시까지", null, mutableListOf("취뽀", "스피치")),
        ProofShotItem("헬스 챌린지", "~ 2024. 12. 1", "7시까지", null, mutableListOf("헬스", "요가")),
        ProofShotItem("요리 챌린지", "~ 2024. 12. 1", "2시까찌", null, mutableListOf("요리")),
        ProofShotItem("스터디 챌린지", "~ 2024. 12. 1", "8시까지", null, mutableListOf("어학", "자격증")),
        ProofShotItem("소설 필사하기", "~ 2024. 9. 1", "12시까지", null, mutableListOf("고능해지자", "독서"))
    )

    val completeItems = arrayListOf<ProofShotItem>()
    lateinit var currentItem: ProofShotItem

    fun completeProofShot(url: Uri) {
        proofItems.remove(currentItem)
        currentItem.url = url
        completeItems.add(currentItem)
        proofPos.value = completeItems.indexOf(currentItem)
    }

    fun updateCurrentItem(item: ProofShotItem) {
        currentItem = item
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

    // 페이징 상태
    private var currentPage = 0
    var isLoading = false
    var isLastPage = false

    fun resetPagingParam() {
        currentPage = 0
        isLastPage = false
        _endedChallenges.postValue(mutableListOf())
        _feedHistoryData.postValue(mutableListOf())
    }

    fun fetchEndedChallenge() {
        if (isLoading || isLastPage) return // 이미 로드 중이거나 마지막 페이지라면 호출 안 함
        isLoading = true

        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getEndedChallenges(currentPage, 10) }, // 페이지당 10개
                onSuccess = { data: EndedChallengeData? ->
                    if (data!!.content.isEmpty()) {
                        isLastPage = true
                    } else {
                        val currentList = _endedChallenges.value ?: mutableListOf()
                        currentList.addAll(data.content)
                        _endedChallenges.postValue(currentList) // 기존 데이터에 추가
                        currentPage ++ // 다음 페이지로 이동
                    }
                    isLoading = false
                },
                onFailure = { errorCode ->
                    isLoading = false
                    Log.e("fetchEndedChallenge", "Error: $errorCode")
                }
            )
        }
    }

    fun fetchFeedHistory() {
        if (isLoading || isLastPage) return
        isLoading = true

        viewModelScope.launch {
            handleApiCall(
                call = { user_repository.getFeedHistory(currentPage, 10) },
                onSuccess = { data: FeedHistoryData? ->
                    if (data!!.content.isEmpty()) {
                        isLastPage = true
                    } else {
                        val currentList = _feedHistoryData.value ?: mutableListOf()
                        currentList.addAll(data.content)
                        _feedHistoryData.postValue(currentList)
                        currentPage ++
                    }
                    isLoading = false
                },
                onFailure = { errorCode ->
                    isLoading = false
                    Log.e("fetchEndedChallenge", "Error: $errorCode")
                }
            )
        }
    }


}
