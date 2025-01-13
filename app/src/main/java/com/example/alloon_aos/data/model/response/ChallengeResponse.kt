package com.example.alloon_aos.data.model.response

import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.MemberImg
import com.example.alloon_aos.data.model.request.Rule
import com.squareup.moshi.Json

data class ChallengeResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: ChallengeData
)
data class ChallengeListResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: List<ChallengeData>
)
data class ChallengeData(
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "goal")
    val goal: String,
    @field:Json(name = "imageUrl")
    val imageUrl: String,
    @field:Json(name = "currentMemberCnt")
    val currentMemberCnt: Int,
    @field:Json(name = "isPublic")
    val isPublic: Boolean,
    @field:Json(name = "proveTime")
    val proveTime: String,
    @field:Json(name = "endDate")
    val endDate: String,
    @field:Json(name = "rules")
    val rules: List<Rule>,
    @field:Json(name = "hashtags")
    val hashtags: List<HashTag>,
    @field:Json(name = "memberImages")
    val memberImages: List<MemberImg>
)

data class ExamImgResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: ExamImgData
)
data class ExamImgData(
    @field:Json(name = "list")
    val list: List<String>
)

data class MessageResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: MessageData
)
data class MessageData(
    @field:Json(name = "successMessage")
    val successMessage: String
)

data class CodeResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: CodeData
)
data class CodeData(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "invitationCode")
    val invitationCode: String
)
