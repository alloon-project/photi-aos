package com.example.alloon_aos.data.model

object ApiConfig {
    // 토큰을 추가할 API 목록을 Map으로 관리 (URL, Method) :: 하나씩 추가
    val tokenRequiredApis = mapOf(
        "/api/users" to "PATCH",       // 회원 탈퇴
        "/api/users/password" to "PATCH",         // 비밀번호 변경
        "/api/users/token" to "POST"         // 토큰 재발급
    )
}