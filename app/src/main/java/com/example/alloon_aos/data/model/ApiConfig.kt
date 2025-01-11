package com.example.alloon_aos.data.model

object ApiConfig {
    // 토큰을 추가할 API 목록을 Map으로 관리 (URL, Method) :: 하나씩 추가
    val tokenRequiredApis = mapOf(
        // auth
        "/api/users" to "PATCH", // 회원 탈퇴
        "/api/users/password" to "PATCH", // 비밀번호 변경
        "/api/users/token" to "POST", // 토큰 재발급
        "/api/inquiries" to "POST",

        // challenge
        "/api/challenges/{challengeId}" to mapOf(
            "DELETE" to "챌린지 탈퇴", // DELETE 메소드에 대한 구분
            "PATCH" to "챌린지 수정"   // PATCH 메소드에 대한 구분
        ),
        "/api/challenges/{challengeId}/invitation-code" to "GET", // 챌린지 초대 코드 조회
        "/api/challenges/{challengeId}/challenge-members/goal" to "PATCH", // 챌린지 개인 목표 작성
        "/api/challenges" to "POST", // 챌린지 생성

        // user
        "/api/users" to "GET",
        "/api/my-challenges" to "GET",
        "/api/feeds" to "GET",
        "/api/users/feeds-by-date" to "GET",
        "/api/users/feed-history" to "GET",
        "/api/users/ended-challenges" to "GET",
        "/api/users/challenges" to "GET", // 챌린지 카운트 조회
        "/api/users/challenge-history" to "GET", // 챌린지 기록 조회
        "/api/users/image" to "POST" // 이미지 업로드
    )


    fun convertTemplateToRegex(urlTemplate: String): String {
        return urlTemplate.replace(Regex("\\{([^/]+)\\}")) { match ->
            "([^/]+)"  // {challengeId}와 {feedId}를 정규식 패턴으로 변경
        }
    }

    fun isTokenRequired(url: String, method: String): Boolean {
        return tokenRequiredApis.entries.any { (template, apiMethod) ->
            // URL이 메서드와 일치하는지 확인
            val regexPattern = convertTemplateToRegex(template)
            val isUrlMatch = url.matches(Regex("^$regexPattern$"))

            // 경로가 메소드별 구분이 필요한 경우
            if (apiMethod is Map<*, *>) {
                // 메소드가 일치하는지 확인
                isUrlMatch && apiMethod.containsKey(method)
            } else {
                // 메소드가 구분되지 않으면 단순히 일치하는지 확인
                isUrlMatch && method == apiMethod
            }
        }
    }
}