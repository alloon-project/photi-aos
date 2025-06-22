package com.photi.aos.data.model.response

data class UserProfile(
    val imageUrl: String,
    val username: String,
    val email: String
)

data class MyChallenges(
    val content: List<ChallengeContent>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

data class ChallengeContent(
    val id: Int,
    val name: String,
    val challengeImageUrl: String,
    val proveTime: String,
    val endDate: String,
    val hashtags: List<Hashtag>,
    val feedImageUrl: String,
    val feedId: Int?,
    val isProve: Boolean
)

data class Hashtag(
    val hashtag: String
)

data class FeedDate(
    val list:  List<String>
)


data class FeedByDate(
    val id: Int, //feedId
    val challengeId : Int,
    val imageUrl: String,
    val name: String,
    val proveTime: String
)

data class FeedHistoryData(
    val content: List<FeedHistoryContent>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

data class FeedHistoryContent(
    val feedId: Int,
    val challengeId : Int,
    val imageUrl: String,
    val createdDate: String,
    val name: String
)

// EndedChallenge Response
data class EndedChallengeData(
    val content: List<EndedChallengeContent>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)

data class EndedChallengeContent(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val endDate: String,
    val currentMemberCnt: Int,
    val memberImages: List<MemberImage>
)

data class MemberImage(
    val memberImage: String
)

data class MyChallengeCount(
    val username: String,
    val challengeCnt: Int
)

data class ChallengeRecordData(
    val username: String,
    val imageUrl: String,
    val feedCnt: Int,
    val endedChallengeCnt: Int
)

data class ProfileImageData(
    val imageUrl: String,
    val username: String,
    val email: String
)
