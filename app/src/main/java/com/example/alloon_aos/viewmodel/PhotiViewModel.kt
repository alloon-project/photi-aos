package com.example.alloon_aos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val title:String, val date: String, val url: String? = null)
data class Chip(val id:String)

class PhotiViewModel: ViewModel() {

    //인기있는 챌린지
    val hotItemsListData = MutableLiveData<ArrayList<Item>>()
    val hotItems = arrayListOf<Item>(Item("헬스 미션","~ 2024. 8. 22","https://ifh.cc/g/V4Bb5Q.jpg"),
        Item("요리 미션","~ 2024. 12. 1","https://ifh.cc/g/09y6Mo.jpg"),
        Item("면접 연습하기","~ 2024. 8. 22","https://ifh.cc/g/PJpN7X.jpg"),
        Item("멋진 개발자가 되어서 초코칩 만들기","~ 2024. 12. 1","https://ifh.cc/g/Okx9DW.jpg"))

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
    val hashItems = arrayListOf<Item>(Item("영화 미션","~ 2024. 12. 1"),Item("소설 필사하기","~ 2024. 9. 1"),Item("멋진 개발자가 되어서 초코칩 만들기","~2024. 10. 12"))


    //최신순 챌린지
    val latestItems = arrayListOf<Item>(Item("러닝 미션","~ 2024. 12. 1"),Item("영화 미션","~ 2024. 12. 1"),Item("면접 연습하기","~ 2024. 8. 22")
        ,Item("헬스 미션","~ 2024. 12. 1"),Item("요리 미션","~ 2024. 12. 1"),Item("스터디 미션","~ 2024. 12. 1"))


    //해시태그 칩 목록
    val hashChipsListData = MutableLiveData<ArrayList<Chip>>()
    val hashChips = ArrayList<Chip>()
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


}