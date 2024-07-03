package com.example.alloon_aos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val title:String, val date: String)

class PhotiViewModel: ViewModel() {

    //인기있는 챌린지
    val hotItemsListData = MutableLiveData<ArrayList<Item>>()
    val hotItems = ArrayList<Item>()

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

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

    //해시태그별 모아보기

    val hashItemsListData = MutableLiveData<ArrayList<Item>>()
    val hashItems = ArrayList<Item>()


    fun addHashItem(item: Item) {
        hashItems.add(item)
        hashItemsListData.value = hotItems // let the observer know the livedata changed
    }


}