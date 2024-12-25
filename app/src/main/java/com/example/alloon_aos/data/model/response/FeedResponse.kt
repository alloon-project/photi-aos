package com.example.alloon_aos.data.model.response

data class ChallengeFeedsData(
    val content: List<FeedContent>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

data class FeedContent(
    val createdDate: String, // "2024-10-09"
    val feedMemberCnt: Int, // 5
    val feeds: List<Feed>
)

data class Feed(
    val id: Long, // 1
    val username: String, // "photi"
    val imageUrl: String, // "https://url.kr/5MhHhD"
    val createdDateTime: String, // "2024-12-08T07:44:51.349Z"
    val proveTime: String // "13:00"
)

data class ChallengeInfoData(
    val rules: List<ChallengeRule>, // 규칙 목록
    val proveTime: String, // 인증 시간, 예: "13:00"
    val goal: String, // 목표
    val startDate: String, // 시작 날짜, 예: "2024-01-01"
    val endDate: String // 종료 날짜, 예: "2024-12-01"
)

// Challenge 규칙 구조
data class ChallengeRule(
    val rule: String // 규칙 설명
)

//개별조회
data class FeedDetailData(
    val username: String,        // "photi"
    val userImageUrl: String,    // "https://url.kr/5MhHhD"
    val feedImageUrl: String,    // "https://url.kr/5MhHhD"
    val createdDateTime: String, // "2024-12-08T07:55:51.190Z"
    val likeCnt: Int             // 10
)

//멤버조회
data class ChallengeMember(
    val id: Long,           // 1
    val username: String,   // "photi"
    val imageUrl: String,   // "https://url.kr/5MhHhD"
    val isCreator: Boolean, // true (파티장 여부)
    val duration: Int,      // 10 (참여 일수)
    val goal: String        // "열심히 운동하기!!"
)

//댓글조회
data class FeedCommentsData(
    val content: List<Comment>,  // 댓글 리스트
    val page: Int,               // 페이지 번호
    val size: Int,               // 페이지 크기
    val first: Boolean,          // 첫 번째 페이지 여부
    val last: Boolean            // 마지막 페이지 여부
)

data class Comment(
    val id: Long,         // 댓글 ID
    val username: String, // 작성자 이름
    val comment: String   // 댓글 내용
)