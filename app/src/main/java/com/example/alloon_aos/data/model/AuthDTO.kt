package com.example.alloon_aos.data.model


import com.squareup.moshi.Json

data class AuthDTO(
    @field:Json(name = "code")
    val code : String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val `data`: List<Data?>?
)

data class Data(
    @field:Json(name = "userId")
    val userId : Int,
    @field:Json(name = "username")
    val username : String
)

data class UserData(
    var email : String? =null ,
    var verificationCode : String?=null,
    var username : String? =null,
    var password : String? =null,
    var passwordReEntered : String? =null
)

data class NewPwd(
    var password : String? = null,
    var newPassword : String? = null,
    var newPasswordReEntered : String ?= null
)

data class EmailCode(
    var email : String? = null,
    var verificationCode : String ?= null
)

