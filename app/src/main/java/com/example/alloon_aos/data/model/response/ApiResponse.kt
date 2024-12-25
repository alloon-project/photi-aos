package com.example.alloon_aos.data.model.response

// 공통 응답 구조
data class ApiResponse<T>(
    val code: String, // "200 OK"
    val message: String, // "성공"
    val data: T // 제네릭 타입으로 데이터 구조 재사용
)
