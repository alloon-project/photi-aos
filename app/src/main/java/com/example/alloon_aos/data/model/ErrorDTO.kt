package com.example.alloon_aos.data.model

import com.squareup.moshi.Json

data class ErrorDTO(
    @field:Json(name = "code")
    val code : String,
    @field:Json(name = "message")
    val message: String,
)
