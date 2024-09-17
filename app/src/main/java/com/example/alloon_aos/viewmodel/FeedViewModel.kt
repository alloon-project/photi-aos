package com.example.alloon_aos.viewmodel

import androidx.lifecycle.ViewModel

data class FeedOutItem(
    val daysAgo : Int,
    val feedInItems : ArrayList<FeedInItem> = ArrayList()
)

data class FeedInItem(
    val id: String,
    val time: String,
    var url: String? = null,
    var isClick: Boolean,
    val heartCnt:Int = 0,
    val comments : ArrayList<Comment> = ArrayList()
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

class FeedViewModel : ViewModel(){

    //피드뷰
    val comments = arrayListOf<Comment>(
        Comment("aaa","이 책 좋네요"),
        Comment("abc","멋져요"),
        Comment("baa","와우"),
        Comment("Seul","우왕굳 ㅋㅋ"),
        Comment("HB","짱~!"),
        Comment("aaa","엄청긴댓글입니다아홉열열하나다여"),
        Comment("aaa","이 책 좋네요"),
        Comment("abc","멋져요"),
        Comment("aaa","엄청긴댓글입니다아홉열열하나다여")
    )

    val feedInItems = arrayListOf<FeedInItem>(
        FeedInItem("photi","방금","https://ifh.cc/g/6HRkxa.jpg",true,2,comments),
        FeedInItem("seul","1분전","https://ifh.cc/g/AA0NMd.jpg",false,5,comments),
        FeedInItem("HB","18분전","https://ifh.cc/g/09y6Mo.jpg",false,10),
        FeedInItem("photi1","30분전","https://ifh.cc/g/KB2Vh1.jpg",false,1,comments),
        FeedInItem("photi2","방금","https://ifh.cc/g/yxgmBH.webp",true),
    )

    val feedOutItems = arrayListOf<FeedOutItem>(
        FeedOutItem(0,feedInItems),
        FeedOutItem(1,feedInItems),
        FeedOutItem(2,feedInItems),
        FeedOutItem(3,feedInItems)
    )


    //파티원
    val paryItem = arrayListOf<PartyItem>(
        PartyItem("photi_1","1","열심히 운동해서 바디프로필 찍기!!!",true),
        PartyItem("photi_2","10","일찍 자고 일찍 일어나기"),
        PartyItem("photi_3","11223"),
        PartyItem("photi_4","2","내용")
    )
}