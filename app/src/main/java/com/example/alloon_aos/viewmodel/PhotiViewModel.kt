package com.example.alloon_aos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val title:String, val date: String, val url: String? = null, var hashtag: MutableList<String> = mutableListOf()  )
data class Chip(val id:String)
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
    var url: String? = null,
    var hashtag: MutableList<String>,
    var proof: Boolean
)

class PhotiViewModel: ViewModel() {

    //인기있는 챌린지
    val hotItemsListData = MutableLiveData<ArrayList<Item>>()
    val hotItems = arrayListOf<Item>(Item("헬스 미션","~ 2024. 8. 22","https://ifh.cc/g/V4Bb5Q.jpg", mutableListOf("요가","헬스")),
        Item("요리 챌린지","~ 2024. 12. 1","https://ifh.cc/g/09y6Mo.jpg",mutableListOf("요리")),
        Item("면접 연습하기","~ 2024. 8. 22","https://ifh.cc/g/PJpN7X.jpg",mutableListOf("취뽀","스피치")),
        Item("멋진 개발자가 되어서 초코칩 만들기","~ 2024. 12. 1","https://ifh.cc/g/Okx9DW.jpg",mutableListOf("코딩","안드로이드")))

    fun addHotItem(item: Item) {
        hotItems.add(item)
        hotItemsListData.value = hotItems // let the observer know the livedata changed
    }

    fun updateItem(pos: Int, item: Item) {
        hotItems[pos] = item
        hotItemsListData.value = hotItems // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }

    fun deleteItem(pos: Int) {
        hotItems.removeAt(pos)
        hotItemsListData.value = hotItems
    }


    //해시태그별 챌린지
    val hashItems = arrayListOf<Item>(Item("영화 미션","~ 2024. 12. 1","https://ifh.cc/g/6HRkxa.jpg", mutableListOf("영화관람")),
        Item("소설 필사하기","~ 2024. 9. 1","https://ifh.cc/g/yxgmBH.webp", mutableListOf("고능해지자","독서")),
        Item("멋진 개발자가 되어서 초코칩 만들기","~2024. 10. 12","https://ifh.cc/g/Okx9DW.jpg", mutableListOf("코딩","ios")),
        Item("바빌론 감상평쓰기","~2024. 12. 1","https://ifh.cc/g/Vwdj5C.jpg", mutableListOf("감상평","글쓰기"))
        )


    //최신순 챌린지
    val latestItems = arrayListOf<Item>(Item("코딩 챌린지","~ 2024. 12. 1","https://ifh.cc/g/rKbcYM.jpg",
        mutableListOf("ios","android")
    ),
        Item("영화 챌린지","~ 2024. 12. 1","https://ifh.cc/g/6HRkxa.jpg", mutableListOf("영화관람")),
        Item("면접 연습하기","~ 2024. 8. 22","https://ifh.cc/g/PJpN7X.jpg",mutableListOf("취뽀","스피치")),
        Item("헬스 챌린지","~ 2024. 12. 1","https://ifh.cc/g/AA0NMd.jpg", mutableListOf("헬스","요가")),
        Item("요리 챌린지","~ 2024. 12. 1","https://ifh.cc/g/09y6Mo.jpg",mutableListOf("요리")),
        Item("스터디 챌린지","~ 2024. 12. 1","https://ifh.cc/g/KB2Vh1.jpg", mutableListOf("어학","자격증")),
        Item("헬스 미션","~ 2024. 8. 22","https://ifh.cc/g/V4Bb5Q.jpg", mutableListOf("요가","헬스")),
        Item("소설 필사하기","~ 2024. 9. 1","https://ifh.cc/g/yxgmBH.webp", mutableListOf("고능해지자","독서"))
    )


    //해시태그 칩 목록
    val hashChipsListData = MutableLiveData<ArrayList<Chip>>()
    val hashChips = arrayListOf<Chip>(
        Chip("러닝"), Chip("취뽀"), Chip("독서"), Chip("맛집"), Chip("안드로이드")
    )

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1


    fun addHashChip(chip: Chip) {
        hashChips.add(chip)
        hashChipsListData.value = hashChips
    }

//    fun setClickChip(pos: Int) {
//        for(i in 1..hashChips.size) {
//            if (i != pos){
//                val chip = hashChips.get(i)
//                chip.
//            }
//        }
//    }


    // 챌린지 없을 때
    val _photoItem = MutableLiveData<ChallengeItem>() //현재 item
    val photoItems = arrayListOf(
        ChallengeItem("영화 챌린지","~ 2024. 12. 1","10시까지","영화를 봅시다","https://ifh.cc/g/6HRkxa.jpg", mutableListOf("영화관람"),"3명"),
        ChallengeItem("면접 연습하기","~ 2024. 8. 22","8시까지","면접 연습을 해야해요","https://ifh.cc/g/PJpN7X.jpg",mutableListOf("취뽀","스피치"),"2명"),
        ChallengeItem("헬스 챌린지","~ 2024. 12. 1","7시까지","헬스는 꾸쭌히!","https://ifh.cc/g/AA0NMd.jpg", mutableListOf("헬스","요가"),"네명"),
        ChallengeItem("요리 챌린지","~ 2024. 12. 1","2시까찌","요리는 즐거워~~","https://ifh.cc/g/09y6Mo.jpg",mutableListOf("요리"),"다슷명"),
        ChallengeItem("스터디 챌린지","~ 2024. 12. 1","8시까지","공부해라","https://ifh.cc/g/KB2Vh1.jpg", mutableListOf("어학","자격증"),"두명"),
        ChallengeItem("소설 필사하기","~ 2024. 9. 1","12시까지","당신의 최애 소설은?","https://ifh.cc/g/yxgmBH.webp", mutableListOf("고능해지자","독서"),"7명")

    )//data 받을 list 얘는 페이지 새로 받아올때마다 초기화 하면된다

    val photoItemList = MutableLiveData<ArrayList<ChallengeItem>>()
    fun setCurrentPhoto(photoItem: ChallengeItem) {
        _photoItem.value = photoItem
    }


    // 챌린지 있을 때
    val _proofItem = MutableLiveData<ProofShotItem>() //현재 item
    val proofItems = arrayListOf(
        ProofShotItem("영화 챌린지","~ 2024. 12. 1","10시까지",null, mutableListOf("영화관람"),false),
        ProofShotItem("면접 연습하기","~ 2024. 8. 22","8시까지",null, mutableListOf("취뽀","스피치"),false),
        ProofShotItem("헬스 챌린지","~ 2024. 12. 1","7시까지",null, mutableListOf("헬스","요가"),false),
        ProofShotItem("요리 챌린지","~ 2024. 12. 1","2시까찌","https://ifh.cc/g/09y6Mo.jpg",mutableListOf("요리"),true),
        ProofShotItem("스터디 챌린지","~ 2024. 12. 1","8시까지","https://ifh.cc/g/KB2Vh1.jpg", mutableListOf("어학","자격증"),true),
        ProofShotItem("소설 필사하기","~ 2024. 9. 1","12시까지","https://ifh.cc/g/yxgmBH.webp", mutableListOf("고능해지자","독서"),true)

    )//data 받을 list 얘는 페이지 새로 받아올때마다 초기화 하면된다

    val proofItemList = MutableLiveData<ArrayList<ProofShotItem>>()
    fun setCurrentChallenge(proofItem: ProofShotItem) {
        _proofItem.value = proofItem
    }

}