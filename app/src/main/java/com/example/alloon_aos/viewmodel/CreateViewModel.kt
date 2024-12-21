package com.example.alloon_aos.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alloon_aos.data.model.ActionApiResponse
import com.example.alloon_aos.data.model.MyData
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.ExamImgResponse
import com.example.alloon_aos.data.remote.RetrofitClient
import com.example.alloon_aos.data.repository.ChallengeRepository
import com.example.alloon_aos.data.repository.ChallengeRepositoryCallback
import com.example.alloon_aos.data.repository.ErrorHandler

data class Image(val content : String, val image : String, var select : Boolean)
data class RuleItem(val rule : String, var select : Boolean)

class CreateViewModel : ViewModel() {
    companion object {
        private const val TAG = "CREATE"
    }

    private val challengeService = RetrofitClient.challengeService
    private val repository = ChallengeRepository(challengeService)

    val apiResponse = MutableLiveData<ActionApiResponse>()

    var name = ""
    var isPublic = true
    var goal = ""
    var proveTime = ""
    var endDate = ""
    var rules: List<Rule> = listOf()
    var hashtags: List<HashTag> = listOf()
    var imgFile = ""
    var isUri = false

    fun resetApiResponseValue() {
        apiResponse.value = ActionApiResponse()
    }

    fun setTitleData(title: String) { name = title }
    fun setTimeData(time: String) { proveTime = time }
    fun setGoalData(goal: String) { this.goal = goal }
    fun setDateDate(date: String) { endDate = date }
    fun setImgData(img: String) {
        select(4)
        selectURL = img
    }
    fun setRuleData(rules: List<Rule>) {
        for (rule in rules) {
            val matchedRuleItem = defaultRules.find { it.rule == rule.rule }

            if (matchedRuleItem != null) {
                selectNum++
                matchedRuleItem.select = true
            } else
                addRule(rule.rule)
        }
    }
    fun setHashData(hashs: List<HashTag>) {
        for(hash in hashs) {
            addHash(hash.hashtag)
        }
    }

    fun getData() : MyData {
        var data = MyData(name, isPublic, goal, proveTime, endDate, rules, hashtags)
        return data
    }


    //create image
    var selectURL = ""
    var _selectURL = MutableLiveData<String>()

    var examImages : ArrayList<Image> = arrayListOf()
    var select = 0

    fun setExamImg(list: List<String>) {
        val imgList = arrayListOf<Image> (
            Image("럭키데이", list[0], false),
            Image("인증샷", list[1], false),
            Image("오운완", list[2], false),
            Image("스터디", list[3], false)
        )
        examImages = imgList
    }

    fun getExamImg() {
        repository.getExamImg(object : ChallengeRepositoryCallback<ExamImgResponse> {
            override fun onSuccess(data: ExamImgResponse) {
                val result = data.code
                val mes = data.message
                val data = data.data
                setExamImg(data.list)
                apiResponse.value = ActionApiResponse(result)
                Log.d(TAG, "getExamImg: $mes $result")
            }

            override fun onFailure(error: Throwable) {
                handleFailure(error)
            }
        })
    }

    fun handleFailure(error: Throwable) {
        val errorCode = ErrorHandler.handle(error)
        apiResponse.value = ActionApiResponse(errorCode)
    }

    fun initImage() {
        select(select)
        if (select == 0)
            _selectURL.value = examImages[0].image
    }

    fun select(pos : Int) {
        for (n in 0..3) {
            if (n == pos)
                examImages[n].select = true
            else
                examImages[n].select = false
        }
        select = pos
    }

    fun setCustomImg() {
        _selectURL.value = selectURL
    }

    fun setImageURL() {
        _selectURL.value = examImages[select].image
        isUri = false
    }

    fun setUriToURL(uri: Uri) {
        _selectURL.value = uri.toString()
        isUri = true
    }

    fun setImageFile(file: String) {
        imgFile = file
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
        selectNum++
        customRules.add(RuleItem(rule, true))
        customRule.value = customRules
    }

    fun deleteRule(item : RuleItem) {
        if (item.select)
            selectNum--
        customRules.remove(item)
        customRule.value = customRules
    }

    fun setRuleList() {
        val selectedRules: List<Rule> = (defaultRules + customRules)
            .filter { it.select }
            .map { Rule(it.rule) }

        rules = selectedRules
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

    fun setHashList(){
        val hashTagList: List<HashTag> = _hashs.map { HashTag(it) }
        hashtags = hashTagList
    }
}