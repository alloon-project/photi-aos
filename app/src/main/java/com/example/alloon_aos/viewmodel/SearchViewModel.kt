package com.example.alloon_aos.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.SearchData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.MemberImg
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.ChallengeData
import com.example.alloon_aos.data.model.response.ChallengeResponse
import com.example.alloon_aos.data.model.response.PagingListResponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler


class SearchViewModel : ViewModel() {
    companion object {
        private const val TAG = "SEARCH"
    }

    private val challengeService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(challengeService)

    val apiResponse = MutableLiveData<ActionApiResponse>()
    val challengeResponse = MutableLiveData<ActionApiResponse>()

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

    var page = 0
    var latestPage = false

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
        challengeResponse.value = ActionApiResponse()
    }
    fun resetSearchList() {
        searchList = arrayListOf()
        page = 0
        latestPage = false
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

    fun getSearchName() {
        isHash = false
        repository.getSearchName(search, page, 10, object :
            ChallengeRepositoryCallback<PagingListResponse> {
            override fun onSuccess(data: PagingListResponse) {
                val result = data.code
                val mes = data.message
                val data = data.data
                val content = data.content
                setNameList(content)
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getSearchName: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                apiResponse.value = ActionApiResponse(ErrorHandler.handle(error))
            }
        })
    }

    fun getSearchHash() {
        isHash = true
        repository.getSearchHashtag(search, page, 10, object :
            ChallengeRepositoryCallback<PagingListResponse> {
            override fun onSuccess(data: PagingListResponse) {
                val result = data.code
                val mes = data.message
                val data = data.data
                val content = data.content
                setHashList(content)
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getSearchHash: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                apiResponse.value = ActionApiResponse(ErrorHandler.handle(error))
            }
        })
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
    var _search = MutableLiveData<String>()
    var search = ""
    var isHash = false

    val _searchList = MutableLiveData<ArrayList<SearchData>>()
    var searchList : ArrayList<SearchData> = arrayListOf()

    fun setNameList(list: List<ChallengeData>) {
        for (item in list) {
            searchList.add(SearchData(item.id, item.name, item.imageUrl,
                item.currentMemberCnt, item.endDate, item.memberImages))
        }
        _searchList.value = searchList
    }

    fun setHashList(list: List<ChallengeData>) {
        for (item in list) {
            searchList.add(SearchData(item.id, item.name, item.imageUrl,
                item.currentMemberCnt, item.endDate, item.memberImages, item.hashtags))
        }
        _searchList.value = searchList
    }
}