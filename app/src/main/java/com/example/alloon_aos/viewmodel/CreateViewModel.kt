package com.example.alloon_aos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.R

data class Image(val content : String, val image : Int, var select : Boolean)
data class RuleItem(val rule : String, var select : Boolean)

class CreateViewModel : ViewModel() {
    //create image

    var selectImage = MutableLiveData<Int>()

    var images = arrayListOf<Image>(
        Image("럭키데이", R.drawable.image222, true),
        Image("인증샷", R.drawable.image33, false),
        Image("오운완", R.drawable.image44, false),
        Image("스터디", R.drawable.image555, false)
    )

    fun initImage(): Boolean {
        for (pos in images) {
            if (pos.select) {
                selectImage.value = pos.image
                return true
            }
        }
        return false
    }

    fun select(pos : Int) {
        for (n in 0..3) {
            if (n == pos)
                images[n].select = true
            else
                images[n].select = false
        }
        if (pos < 4)
            selectImage.value = images[pos].image
        else
            selectImage = MutableLiveData<Int>()
    }


    //create rule

    var defaultRule = MutableLiveData<ArrayList<RuleItem>>()
    var customRule = MutableLiveData<ArrayList<RuleItem>>()

    var defaultRules = arrayListOf<RuleItem>(
        RuleItem("일주일 3회 이상 인증하기", false),
        RuleItem("매일 챌린지 참여하기", false),
        RuleItem("타인을 비방하지 말기", false),
        RuleItem("챌린지와 관련된 사진 올리기", false),
        RuleItem("팀원 인증글에 하트 누르기", false),
        RuleItem("사적인 질문하지 않기", false)
    )

    var customRules = arrayListOf<RuleItem>()
    var selectNum = 0

    fun updateDefaultRule(pos : Int, item : RuleItem) {
        defaultRules[pos] = item
        defaultRule.value = defaultRules
    }

    fun updateCustomRule(pos : Int, item : RuleItem) {
        customRules[pos] = item
        customRule.value = customRules
    }

    fun addRule(rule : String) {
        customRules.add(RuleItem(rule, true))
        customRule.value = customRules
        selectNum++
    }

    fun deleteRule(item : RuleItem) {
        if (item.select)
            selectNum--
        customRules.remove(item)
        customRule.value = customRules
    }


    //create hash

    var hashs = MutableLiveData<ArrayList<String>>()
    var _hashs = arrayListOf<String>()

    fun addHash(hash : String) {
        _hashs.add(hash)
        hashs.value = _hashs
    }

    fun deleteHash(hash : String){
        _hashs.remove(hash)
        hashs.value = _hashs
    }
}