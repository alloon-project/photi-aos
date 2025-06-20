package com.example.alloon_aos.data.model

import android.os.Parcelable
import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.MemberImg
import com.example.alloon_aos.data.model.request.Rule
import com.example.alloon_aos.data.model.response.Hashtag
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyData(
    val name: String,
    val isPublic: Boolean,
    val goal: String,
    val proveTime: String,
    val endDate: String,
    val rules: List<Rule> = emptyList(),
    val hashtags: List<HashTag> = emptyList(),
    val currentMemberCnt: Int = 0,
    val memberImages: List<MemberImg> = emptyList(),
    val imageUrl: String? = null
): Parcelable

data class CommendData(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val goal: String? = null,
    val currentMemberCnt: Int = 0,
    val proveTime: String? = null,
    val endDate: String,
    val hashtags: List<HashTag>,
    val memberImages: List<MemberImg> = emptyList()
)

data class SearchData(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val currentMemberCnt: Int,
    val endDate: String,
    val memberImages: List<MemberImg>,
    val hashtags: List<HashTag> = emptyList()
)

data class MyChallengeData(
    val id: Int,
    val name: String,
    val challengeImageUrl: String,
    val proveTime: String,
    val endDate: String,
    val hashtags: List<Hashtag>,
    val feedImageUrl: String,
    val isProve: Boolean,
    val feedId: Int?
)
