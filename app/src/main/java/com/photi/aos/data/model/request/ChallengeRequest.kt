package com.photi.aos.data.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody

data class CreateData(
    var name: String? = null,
    var isPublic: Boolean = true,
    var goal: String? = null,
    var proveTime: String? = null,
    var endDate: String? = null,
    var preSignedUrl: String? = null,
    var rules: List<Rule> = emptyList(),
    var hashtags: List<HashTag> = emptyList()
)
data class ModifyData(
    var name: String? = null,
    var goal: String? = null,
    var proveTime: String? = null,
    var endDate: String? = null,
    var preSignedUrl: String? = null,
    var rules: List<Rule> = emptyList(),
    var hashtags: List<HashTag> = emptyList()
)

@Parcelize
data class Rule(
    val rule: String
): Parcelable
@Parcelize
data class HashTag(
    val hashtag: String
): Parcelable
@Parcelize
data class MemberImg(
    val memberImage: String
): Parcelable
data class Goal(
    val goal: String
)


