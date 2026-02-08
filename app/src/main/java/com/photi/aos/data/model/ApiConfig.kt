package com.photi.aos.data.model

object ApiConfig {
    // 토큰을 추가할 API 목록을 Map으로 관리 (URL, Method) :: 하나씩 추가
    val tokenRequiredApis = mapOf(
        // auth
        "/api/v2/auth" to mapOf(
            "PATCH" to "회원 탈퇴", // 회원 탈퇴
            "GET" to "사용자 정보 조회" // 사용자 정보 조회
        ),
        "/api/v2/auth/password" to "PATCH", // 비밀번호 변경
        "/api/v2/auth/token" to "POST", // 토큰 재발급
        "/api/v2/inquiries" to "POST", //문의하기

        "/api/v2/reports/{targetId}" to "POST", // 신고 등록

        //oauth
        "/api/v2/oauth/username" to "POST",
        "/api/v2/oauth/{provider}/withdraw" to "PATCH",


        // challenge
        "/api/v2/challenges/{challengeId}" to mapOf(
            "DELETE" to "챌린지 탈퇴", // 챌린지 탈퇴
            "PATCH" to "챌린지 수정" // 챌린지 수정
        ),
        "/api/v2/challenges/{challengeId}/invitation-code" to "GET", // 챌린지 초대 코드 조회
        "/api/v2/challenge-members/{challengeId}/goal" to "PATCH", // 챌린지 개인 목표 작성
        "/api/v2/challenges" to "POST", // 챌린지 생성
        "/api/v2/challenges/{challengeId}/join" to "POST", // 챌린지 참여하기

        // user
        "/api/v2/users" to "GET",
        "/api/v2/users/challenges" to "GET", // 사용자 참여중인 챌린지 조회
        "/api/v2/users/feed-dates" to "GET",
        "/api/v2/users/feeds-by-date" to "GET",
        "/api/v2/users/feed-history" to "GET",
        "/api/v2/users/ended-challenges" to "GET",
        "/api/v2/users/challenge-count" to "GET", // 사용자 참여중인 챌린지 갯수 조회
        "/api/v2/users/challenge-history" to "GET", // 챌린지 기록 조회
        "/api/v2/users/image" to "PATCH", // 이미지 업로드

        // feed
        "/api/v2/challenges/{challengeId}/intro" to "GET",                 // 챌린지 소개 조회
        "/api/v2/challenge-members/{challengeId}" to "GET",               // 챌린지 파티원 조회

        "/api/v2/feeds/{challengeId}/v2" to "GET",                        // 피드 조회 v2
        "/api/v2/feeds/{challengeId}/member-count" to "GET",              // 피드 당일 인증 파티원 수 조회
        "/api/v2/feeds/{challengeId}" to "POST",                          // 피드 인증(업로드)

        "/api/v2/feed-likes/{challengeId}/{feedId}" to mapOf(             // 피드 좋아요
            "DELETE" to "좋아요 삭제",
            "POST" to "좋아요 추가",
        ),

        "/api/v2/feeds/{challengeId}/{feedId}" to mapOf(                  // 피드 개별/삭제
            "DELETE" to "피드 삭제",
            "GET" to "피드 개별 조회",
        ),

        "/api/v2/users/{challengeId}/prove" to "GET",                     // 유저 오늘 인증 여부

        "/api/v2/feed-comments/{feedId}" to "GET",                        // 댓글 리스트 조회
        "/api/v2/feed-comments/{challengeId}/{feedId}" to "POST",         // 댓글 등록
        "/api/v2/feed-comments/{challengeId}/{feedId}/{commentId}" to "DELETE", // 댓글 삭제

        "/api/v2/challenges/{challengeId}/feed" to "GET",                 // 챌린지 인증 피드 존재 여부

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