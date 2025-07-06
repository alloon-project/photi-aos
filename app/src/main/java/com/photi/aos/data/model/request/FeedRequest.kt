package com.photi.aos.data.model.request

import com.photi.aos.data.enum.CategoryType
import com.photi.aos.data.enum.ReasonType

data class ChallengeFeedsRequest(
    val challengeId: Long,
    val page: Int = 0,
    val size: Int = 10,
    val sort: String = "LATEST" // Available values: LATEST, POPULAR
)

data class ReportRequest(
    val category: CategoryType,
    val reason: ReasonType,
    val content: String
)