package com.example.alloon_aos.data.model


import com.squareup.moshi.Json
import retrofit2.http.Header

data class AuthDTO(
    @field:Json(name = "code")
    val code : String,
    @field:Json(name = "message")
    val message: String,
    @field:Json(name = "data")
    val `data`: Data
)

data class Data(
    @field:Json(name = "userId")
    val userId : Int,
    @field:Json(name = "username")
    val username : String,
    @field:Json(name = "imageUrl")
    val imageUrl : String,
    @field:Json(name = "temporaryPasswordYn")
    val temporaryPasswordYn : Boolean
)

data class UserData(
    var email : String? =null ,
    var verificationCode : String?=null,
    var username : String? =null,
    var password : String? =null,
    var passwordReEnter : String? =null
)

data class NewPwd(
    var password : String? = null,
    var newPassword : String? = null,
    var newPasswordReEnter : String ?= null
)

data class EmailCode(
    var email : String? = null,
    var verificationCode : String ?= null
)

