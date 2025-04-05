package com.example.alloon_aos.data.model.response

import com.example.alloon_aos.data.model.request.HashTag
import com.example.alloon_aos.data.model.request.Rule

data class ChallengeFeedsData(
    val content: List<Feed>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

sealed class FeedUiItem {
    data class Header(val dateLabel: String) : FeedUiItem()
    data class Content(val feed: Feed) : FeedUiItem()
}

data class Feed(
    val id: Int, // feed id
    val username: String, // "photi"
    val imageUrl: String, // "https://url.kr/5MhHhD"
    val createdDateTime: String, // "2024-12-08T07:44:51.349Z"
    val proveTime: String, // "13:00"
    var isLike : Boolean
)


data class FeedChallengeData(
    val name: String, // 챌린지 이름
    val goal: String, // 챌린지 목표
    val imageUrl: String, // 챌린지 이미지 URL
    val currentMemberCnt: Int, // 현재 참여 멤버 수
    val isPublic: Boolean, // 공개 여부
    val proveTime: String, // 인증 시간
    val endDate: String, // 종료 날짜
    val rules: List<Rule>, // 규칙 리스트
    val hashtags: List<HashTag>, // 해시태그 리스트
    val memberImages: List<MemberImage> // 멤버 이미지 리스트
)

data class ChallengeInfoData(
    val goal: String,
    val proveTime: String,
    val startDate: String,
    val endDate: String,
    val rules: List<Rule>
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
    var goal: String?       // "열심히 운동하기!!"
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

data class VerifiedMemberCount (
   val feedMemberCnt : Int,
)

data class UserVerificationStatus (
    val isProve : Boolean,
)


data class SuccessMessageReponse(
    val successMessage: String // 성공 메시지
)
