package com.example.alloon_aos.viewmodel

import androidx.lifecycle.ViewModel

data class FeedItem(
    val id: String,
    val time: String,
    var url: String? = null,
    var isClick: Boolean
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
}