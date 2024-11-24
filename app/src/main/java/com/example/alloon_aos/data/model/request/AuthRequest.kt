package com.example.alloon_aos.data.model.request;

data class UserData(
        var email: String? = null,
        var verificationCode: String? = null,
        var username: String? = null,
        var password: String? = null,
        var passwordReEnter: String? = null
)

data class NewPwd(
        var password: String? = null,
        var newPassword: String? = null,
        var newPasswordReEnter: String? = null
)

data class EmailCode(
        var email: String? = null,
        var verificationCode: String? = null
)

data class RefreshTokenRequest(
        val refreshToken: String
)

