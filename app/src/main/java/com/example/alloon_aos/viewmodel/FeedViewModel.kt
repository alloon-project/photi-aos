package com.example.alloon_aos.viewmodel

import androidx.lifecycle.ViewModel

data class FeedItem(
    val id: String,
    val time: String,
    var url: String? = null,
    var isClick: Boolean
)


data class PartyItem(
    val id: String,
    val time: String,
    var text: String = "",
    var isMe: Boolean = false
)

class FeedViewModel : ViewModel(){

    //피드뷰
    val feedItems = arrayListOf<FeedItem>(
        FeedItem("photi","방금","https://ifh.cc/g/6HRkxa.jpg",true),
        FeedItem("id","1분전","https://ifh.cc/g/AA0NMd.jpg",false),
        FeedItem("id","18분전","https://ifh.cc/g/09y6Mo.jpg",false),
        FeedItem("photi","30분전","https://ifh.cc/g/KB2Vh1.jpg",false),
        FeedItem("photi","방금","https://ifh.cc/g/yxgmBH.webp",true),
    )

    //파티원
    val paryItem = arrayListOf<PartyItem>(
        PartyItem("photi_1","1","열심히 운동해서 바디프로필 찍기!!!",true),
        PartyItem("photi_2","10","일찍 자고 일찍 일어나기"),
        PartyItem("photi_3","11223"),
        PartyItem("photi_4","2","내용")
    )
}