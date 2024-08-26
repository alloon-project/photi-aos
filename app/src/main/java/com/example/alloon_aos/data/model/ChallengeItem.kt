package com.example.alloon_aos.data.model


data class ChallengeItem(
    val title: String,
    val data: String,
    val time: String,
    val content: String,
    val url: String? = null,
    var hashtag: MutableList<String>,
    val member: String
)
