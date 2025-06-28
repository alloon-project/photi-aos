package com.photi.aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.photi.aos.data.model.ActionApiResponse
import com.photi.aos.data.model.MyData
import com.photi.aos.data.model.request.HashTag
import com.photi.aos.data.model.request.MemberImg
import com.photi.aos.data.model.request.Rule
import com.photi.aos.data.model.response.ChallengeData
import com.photi.aos.data.model.response.ChallengeResponse
import com.photi.aos.data.paging.SearchHashPagingSource
import com.photi.aos.data.paging.SearchNamePagingSource
import com.photi.aos.data.remote.RetrofitClient
import com.photi.aos.data.repository.ChallengeRepository
import com.photi.aos.data.repository.ChallengeRepositoryCallback
import com.photi.aos.data.repository.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchViewModel : ViewModel() {
    companion object {
        private const val TAG = "SEARCH"
    }

    private val challengeService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(challengeService)

    val challengeResponse = MutableLiveData<ActionApiResponse>()
    val page = MutableLiveData<Int>()

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
    var myIdList = arrayListOf<Int>()

    fun resetApiResponseValue() {
        challengeResponse.value = ActionApiResponse()
    }

    fun checkUserInChallenge(): Boolean {
        for (id in myIdList) {
            if (this.id == id)
                return true
        }
        return false
    }

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

    fun getChallenge() {
        repository.getChallenge(id, object : ChallengeRepositoryCallback<ChallengeResponse> {
                override fun onSuccess(data: ChallengeResponse) {
                    val result = data.code
                    val mes = data.message
                    val data = data.data
                    setChallengeData(data)
                    challengeResponse.value = ActionApiResponse(result)
                    Log.d(TAG, "getChallenge: $mes $result")
                }

                override fun onFailure(error: Throwable) {
                    challengeResponse.value = ActionApiResponse(ErrorHandler.handle(error))
                }
            })
    }


    private val _searchNameData = MutableStateFlow<PagingData<ChallengeData>>(PagingData.empty())
    val searchNameData: StateFlow<PagingData<ChallengeData>> = _searchNameData

    private val _searchHashData = MutableStateFlow<PagingData<ChallengeData>>(PagingData.empty())
    val searchHashData: StateFlow<PagingData<ChallengeData>> = _searchHashData

    fun clearSearchChallengeData() {
        _searchNameData.value = PagingData.empty()
        _searchHashData.value = PagingData.empty()
    }

    fun fetchSearchName() {
        isHash = false
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20,enablePlaceholders = false ),
            ) {
                SearchNamePagingSource(search, this@SearchViewModel, repository)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _searchNameData.value = pagingData
                }
        }
    }

    fun fetchSearchHash() {
        isHash = true
        viewModelScope.launch {
            Pager(
                PagingConfig(initialLoadSize = 40, pageSize = 20,enablePlaceholders = false ),
            ) {
                SearchHashPagingSource(search, this@SearchViewModel, repository)
            }.flow.cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _searchHashData.value = pagingData
                }
        }
    }


    //before
    var hashs = MutableLiveData<ArrayList<String>>()
    val _hashs = arrayListOf<String>()

    fun addHash(){
        // 이미 있으면 지웠다 더하기
        if(_hashs.contains(search))
            _hashs.remove(search)
        _hashs.add(0,search)
        hashs.value = _hashs
    }
    fun deleteHash(hash : String){
        _hashs.remove(hash)
        hashs.value = _hashs
    }
    fun deleteAllHash(){
        _hashs.clear()
        hashs.value = _hashs
    }


    var searchText = MutableLiveData<String>()
    fun setSearchText(text : String){
        searchText.value = text
    }


    // after
    var search = ""
    var isHash = false

}