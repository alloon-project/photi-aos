package com.example.alloon_aos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class SearchChallenge(
    val title: String,
    val url : String,
    val date: String, // 날짜를 문자열로 저장 (예: "2024.04.01")
    val memberCount: Int, // 파티원 수
    var hashtag: MutableList<String>
)

class SearchViewModel : ViewModel() {

    //before
    var hashs = MutableLiveData<ArrayList<String>>()
    val _hashs = arrayListOf<String>(
        "러닝", "취뽀", "독서", "맛집", "안드로이드"
    )

    fun addHash(hash : String){
        // 이미 있으면 지웠다 더하기
        if(_hashs.contains(hash))
            _hashs.remove(hash)
        _hashs.add(0,hash)
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
    var search = MutableLiveData<String>()
    val _search = ""

    var isHash = false
    var list = MutableLiveData<ArrayList<SearchChallenge>>()
    val _list = listOf(
        SearchChallenge("산책챌린지","https://ifh.cc/g/6HRkxa.jpg", "2024.06.30", 1, mutableListOf("영화관람")),
        SearchChallenge("독서챌린지", "https://ifh.cc/g/AA0NMd.jpg","2024.07.15", 3, mutableListOf("취뽀","스피치")),
        SearchChallenge("요리챌린지", "https://ifh.cc/g/09y6Mo.jpg","2024.08.01", 12, mutableListOf("헬스","요가")),
        SearchChallenge("러닝챌린지", "https://ifh.cc/g/KB2Vh1.jpg","2024.09.10", 1, mutableListOf("요리","고능해지자","독서")),
        SearchChallenge("명상챌린지", "https://ifh.cc/g/yxgmBH.webp","2024.10.05", 20,mutableListOf("어학","자격증"))
    )
}