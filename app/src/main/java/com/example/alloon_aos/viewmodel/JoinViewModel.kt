package com.example.alloon_aos.viewmodel

import androidx.lifecycle.ViewModel

data class Rule(val rule:String)
data class Hash(val chip:String)

class InquiryViewModel : ViewModel() {

    val rules = arrayListOf<Rule>(
        Rule("장소 나오게 찍기 장소 나오게 찍기장소 나오게 찍기 장소 나오게 찍기"),
        Rule("일주일 3회 이상 인증하기"),
        Rule("매일 챌린지 참여하기"),
        Rule("팀원 인증글에 하트 자주자주자주 눌러주셔야 합니다"),
        Rule("사적인 질문하지 않기")
    )

    val hashs = arrayListOf<Hash>(
        Hash("러닝"), Hash("취뽀"), Hash("독서"), Hash("맛집"), Hash("안드로이드")
    )
}