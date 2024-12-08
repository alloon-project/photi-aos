package com.example.alloon_aos.data.model

data class ActionApiResponse(
    val code: String = "UNKNOWN", // 응답 코드 (ex. "200 OK")
    val action: String = "NONE"
)