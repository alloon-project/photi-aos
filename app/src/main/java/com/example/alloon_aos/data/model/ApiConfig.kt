package com.example.alloon_aos.data.model

object ApiConfig {
    // 토큰을 추가할 API 목록을 Map으로 관리 (URL, Method) :: 하나씩 추가
    val tokenRequiredApis = mapOf(
        //auth
        "/api/users" to "PATCH", // 회원 탈퇴
        "/api/users/password" to "PATCH", // 비밀번호 변경
        "/api/users/token" to "POST", // 토큰 재발급
        //challenge
        "/api/challenges/{challengeId}" to "DELETE", // 챌린지 탈퇴
        "/api/challenges/{challengeId}/invitation-code" to "GET", // 챌린지 초대 코드 조회
        "/api/challenges/{challengeId}" to "PATCH", // 챌린지 수정
        "/api/challenges/{challengeId}/challenge-members/goal" to "PATCH", // 챌린지 개인 목표 작성
        "/api/challenges" to "POST" // 챌린지 생성
    )

    // 경로 템플릿을 정규식으로 변환하는 함수
    fun convertTemplateToRegex(urlTemplate: String): String {
        return urlTemplate.replace(Regex("\\{([^/]+)\\}")) { match ->
            "([^/]+)"  // {challengeId}와 {feedId}를 정규식 패턴으로 변경
        }
    }

    // URL 경로와 메서드를 비교하여 동적 값을 치환한 후 비교하는 함수
    fun isTokenRequired(url: String, method: String): Boolean {
        return tokenRequiredApis.entries.any { (template, apiMethod) ->
            // URL이 메서드와 일치하는지 확인
            val regexPattern = convertTemplateToRegex(template)
            url.matches(Regex("^$regexPattern$")) && method == apiMethod
        }
    }
}