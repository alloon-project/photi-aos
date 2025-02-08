package com.example.alloon_aos.data.model.request

data class ChallengeFeedsRequest(
    val challengeId: Long,
    val page: Int = 0,
    val size: Int = 10,
    val sort: String = "LATEST" // Available values: LATEST, POPULAR
)

data class ReportRequest(
    val category: String,
    val reason: String,
    val content: String
)