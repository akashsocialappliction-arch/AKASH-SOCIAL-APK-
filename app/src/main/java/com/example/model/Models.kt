package com.example.model

enum class PostType {
    PHOTO,
    VIDEO,
    STAR_CLIP
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val bio: String,
    val avatarDrawableRes: Int? = null,
    val fansCount: Int = 0,
    val friendsCount: Int = 0,
    val postsCount: Int = 0,
    val videosCount: Int = 0,
    val totalReach: Long = 0L,
    val streakDays: Int = 0,
    val vipStarActive: Boolean = false,
    val vipStarExpiresInDays: Int = 60,
    val isLaunchRewardClaimed: Boolean = false,
    val isCurrentUserFan: Boolean = false,
    val isFanOfCurrentUser: Boolean = false,
    val completedTasksCount: Int = 0,
    val missionsCount: Int = 0,
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val engagementRate: String = "0.0%",
    val topTrendingPostReach: Long = 0L,
    val isBlocked: Boolean = false,
    val isDeveloper: Boolean = false,
    val isCreator: Boolean = false,
    val isModerator: Boolean = false,
    val isOwner: Boolean = false,
    val isVerified: Boolean = false,
    val avatarUri: String? = null,
    val doubleStreakTokensCount: Int = 0,
    val streakRestoreTokensCount: Int = 0,
    val isDoubleStreakBonusActiveToday: Boolean = false,
    val lastStreakActivityDate: String = ""
) {
    val isFriend: Boolean
        get() = isCurrentUserFan && isFanOfCurrentUser
}

data class DraftPost(
    val id: String,
    val authorId: String,
    val type: PostType,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val mentions: List<String> = emptyList(),
    val mediaUri: String? = null,
    val savedAtText: String = "Just now"
)

data class Comment(
    val id: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarRes: Int? = null,
    val hasVipStar: Boolean = false,
    val text: String,
    val timestampText: String = "Just now",
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Int = 30,
    val category: String = "Trending",
    val licenseInfo: String = "StarPulse Creator Royalty-Free License",
    val waveformPreview: List<Float> = listOf(0.3f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 0.7f, 0.95f, 0.6f, 0.4f, 0.8f, 0.5f)
)

data class AiChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestampText: String = "Just now",
    val isThinking: Boolean = false
)

data class Post(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorUsername: String,
    val authorAvatarRes: Int? = null,
    val authorHasVipStar: Boolean = false,
    val authorHasCreatorBadge: Boolean = false,
    val authorHasDeveloperBadge: Boolean = false,
    val type: PostType,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val mentions: List<String> = emptyList(),
    val mediaRes: Int? = null,
    val audioTrack: AudioTrack? = null,
    val videoDurationSeconds: Int = 0,
    val reachCount: Long,
    val viewsCount: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val savesCount: Int,
    val repostsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val timestampText: String = "10m ago",
    val comments: List<Comment> = emptyList(),
    val isPinned: Boolean = false
)

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val mediaRes: Int? = null,
    val sharedPostId: String? = null,
    val timestampText: String = "12:30 PM",
    val status: MessageStatus = MessageStatus.READ
)

data class Conversation(
    val id: String,
    val partner: User,
    val lastMessage: Message,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true
)

data class TaskItem(
    val id: String,
    val title: String,
    val description: String,
    val progress: Int,
    val target: Int,
    val rewardTitle: String,
    val timeRemaining: String,
    val category: String,
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean
        get() = progress >= target
}

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val progress: Int,
    val target: Int,
    val reward: String,
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false
)

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallStatus {
    IDLE,
    OUTGOING,
    INCOMING,
    ONGOING,
    MISSED
}

data class ActiveCall(
    val partner: User,
    val type: CallType,
    val status: CallStatus,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeaker: Boolean = true,
    val isCameraOn: Boolean = true,
    val isFrontCamera: Boolean = true
)

data class LiveChatMessage(
    val id: String,
    val username: String,
    val message: String,
    val isVip: Boolean = false,
    val isSpark: Boolean = false,
    val isCreator: Boolean = false,
    val isModerator: Boolean = false,
    val isPinned: Boolean = false,
    val timestampText: String = "Just now"
)

data class LiveStream(
    val id: String,
    val creator: User,
    val title: String,
    val viewerCount: Int = 0,
    val isLive: Boolean = true,
    val isHost: Boolean = false,
    val tags: List<String> = emptyList(),
    val chatMessages: List<LiveChatMessage> = emptyList(),
    val pinnedAnnouncement: String? = null,
    val isChatMuted: Boolean = false
)

data class ReportItem(
    val id: String,
    val targetType: String, // "USER" or "POST"
    val targetId: String,
    val targetTitle: String,
    val reason: String,
    val reporterUsername: String = "@community_member",
    val timestampText: String = "Just now",
    val statusText: String = "Pending Review"
)

enum class NotifType {
    FAN_GAINED,
    FRIEND_CONNECTED,
    REACH_MILESTONE,
    STREAK_MILESTONE,
    VIP_STAR,
    LIKE,
    COMMENT,
    CALL_MISSED,
    LIVE_STARTED
}

data class NotificationItem(
    val id: String,
    val type: NotifType,
    val title: String,
    val description: String,
    val timestampText: String,
    val actorUser: User? = null,
    val postId: String? = null,
    val isRead: Boolean = false
)

data class AdminUpdateItem(
    val id: String,
    val title: String,
    val category: String, // "Suggested Improvement", "Feature Idea", "Bug Report", "Performance Optimization", "Proposed UI Change"
    val description: String,
    val impactSummary: String,
    val isApproved: Boolean = false,
    val isMajorChange: Boolean = false,
    val statusText: String = "Pending Owner Review"
)

enum class SpotlightMetric(val label: String, val badgeText: String) {
    HIGH_REACH("Breakthrough Reach", "🚀 10K+ Reach"),
    TOP_ENGAGEMENT("Audience Magnet", "⚡ High Engagement"),
    STREAK_MASTER("Streak Legend", "🔥 Streak Master"),
    TASK_CHAMPION("Task Finisher", "🎯 All Tasks Done"),
    RISING_STAR("Rising Creator", "🌟 VIP Star Member")
}

data class CreatorSpotlight(
    val id: String,
    val creator: User,
    val metric: SpotlightMetric,
    val reasonWhyFeatured: String,
    val spotlightTagline: String,
    val topArtworkRes: Int? = null,
    val spotlightScore: Int = 98
)

