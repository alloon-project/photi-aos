package com.example.alloon_aos.data.model

object ApiConfig {
    // 토큰을 추가할 API 목록을 Map으로 관리 (URL, Method) :: 하나씩 추가
    val tokenRequiredApis = mapOf(
        // same
        "/api/users" to mapOf(
            "PATCH" to "회원 탈퇴", // 회원 탈퇴
            "GET" to "사용자 정보 조회" // 사용자 정보 조회
        ),
        "/api/challenges/{challengeId}" to mapOf(
            "DELETE" to "챌린지 탈퇴", // 챌린지 탈퇴
            "PATCH" to "챌린지 수정" // 챌린지 수정
        ),

        // auth
        "/api/users/password" to "PATCH", // 비밀번호 변경
        "/api/users/token" to "POST", // 토큰 재발급
        "/api/inquiries" to "POST",
        "/api/reports/{targetId}" to "POST", // 신고 등록

        // challenge
        "/api/challenges/{challengeId}/invitation-code" to "GET", // 챌린지 초대 코드 조회
        "/api/challenges/{challengeId}/challenge-members/goal" to "PATCH", // 챌린지 개인 목표 작성
        "/api/challenges" to "POST", // 챌린지 생성
        "/api/challenges/{challengeId}/join/public" to "POST", // 공개 챌린지 참여하기
        "/api/challenges/{challengeId}/join/private" to "POST", // 비공개 챌린지 참여하기

        // user
        "/api/users/my-challenges" to "GET", // 사용자 참여중인 챌린지 조회
        "/api/users/feeds" to "GET",
        "/api/users/feeds-by-date" to "GET",
        "/api/users/feed-history" to "GET",
        "/api/users/ended-challenges" to "GET",
        "/api/users/challenges" to "GET", // 사용자 참여중인 챌린지 갯수 조회
        "/api/users/challenge-history" to "GET", // 챌린지 기록 조회
        "/api/users/image" to "POST", // 이미지 업로드

        //feed
        "/api/challenges/{challengeId}/info" to "GET",
        "/api/challenges/{challengeId}/challenge-members" to "GET",
        "/api/challenges/{challengeId}/feeds/v2" to "GET",
        "/api/challenges/{challengeId}/feed-members" to "GET",
        "/api/challenges/{challengeId}/feeds" to "POST",
        "/api/challenges/{challengeId}/feeds/{feedId}/like" to mapOf(
                "DELETE" to "좋아요 삭제",
                "POST" to "좋아요 추가",
        ),
        "/api/challenges/{challengeId}/feeds/{feedId}" to mapOf(
            "DELETE" to "피드 삭제",
            "GET" to "피드 개별 조회",
        ), //삭제 토스트 안뜸 리스트도 재 정렬해얗마
        "/api/users/challenges/{challengeId}/prove" to "GET",
        "/api/challenges/feeds/{feedId}/comments" to "GET",
        "/api/challenges/{challengeId}/feed-existence" to "GET",
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