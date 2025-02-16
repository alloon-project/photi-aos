package com.example.alloon_aos.data.model.response

import com.squareup.moshi.Json

data class AuthResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: AuthData
)

data class AuthData(
    @field:Json(name = "userId")
    val userId: Int,
    @field:Json(name = "username")
    val username: String,
    @field:Json(name = "imageUrl")
    val imageUrl: String,
    @field:Json(name = "temporaryPasswordYn")
    val temporaryPasswordYn: Boolean
)

data class TokenResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val data: TokenData
)

data class TokenData(
    @field:Json(name = "successMessage")
    val successMessage: String
)


data class InquiryResponse(
    val successMessage: String
)
