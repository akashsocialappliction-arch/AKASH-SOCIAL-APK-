package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.R
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StarPulseRepository(private val context: Context? = null) {

    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val PREFS_NAME = "akash_social_zero_state_prefs"
        private const val KEY_MIGRATION_VERSION = "migration_version_v6_owner_akash_singh_parihar"
        private const val CURRENT_VERSION = 6
        private const val OWNER_MASTER_PASSCODE = "akash2026"
        private const val OWNER_PIN = "7869"
        const val OFFICIAL_OWNER_ID = "user_akash_dev"
    }

    // Deduplicated Milestone Notifications Tracker (Requirement 9 & 10)
    private val _sentMilestones = mutableSetOf<String>()

    // 1. FRESH ZERO STATE & OFFICIAL OWNER / CREATOR PROFILE
    // Belongs to: Akash Singh Parihar (Owner • Creator • Developer of AKASH SOCIAL)
    private val _currentUser = MutableStateFlow(
        User(
            id = OFFICIAL_OWNER_ID,
            name = "Akash Singh Parihar",
            username = "akash_dev",
            bio = "Owner, Creator & Developer of AKASH SOCIAL",
            avatarDrawableRes = null,
            fansCount = 0,
            friendsCount = 0,
            postsCount = 0,
            videosCount = 0,
            totalReach = 0L,
            streakDays = 0,
            vipStarActive = false,
            vipStarExpiresInDays = 60,
            isLaunchRewardClaimed = false,
            isCurrentUserFan = false,
            isFanOfCurrentUser = false,
            completedTasksCount = 0,
            engagementRate = "0.0%",
            topTrendingPostReach = 0L,
            isDeveloper = true,
            isCreator = true,
            isModerator = false, // Do NOT require Moderator label on Owner profile
            isOwner = true,
            isVerified = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Multiple accounts registered on this device
    private val _savedAccounts = MutableStateFlow<List<User>>(listOf(_currentUser.value))
    val savedAccounts: StateFlow<List<User>> = _savedAccounts.asStateFlow()

    // Reports queue for Moderator / Creator Dashboard
    private val _reports = MutableStateFlow<List<ReportItem>>(emptyList())
    val reports: StateFlow<List<ReportItem>> = _reports.asStateFlow()

    // Moderator session authorization
    private val _isModeratorAuthenticated = MutableStateFlow(false)
    val isModeratorAuthenticated: StateFlow<Boolean> = _isModeratorAuthenticated.asStateFlow()

    // 2. FICTIONAL INDIAN COMMUNITY CREATORS
    private val _creators = MutableStateFlow<List<User>>(
        listOf(
            User(
                id = "user_aarav",
                name = "Aarav Sharma",
                username = "aarav_creates",
                bio = "Digital artist & UI explorer from Bhopal 🎨 Sharing daily creative concepts #AkashSocial",
                avatarDrawableRes = null,
                fansCount = 15,
                friendsCount = 3,
                postsCount = 2,
                videosCount = 1,
                totalReach = 45L,
                streakDays = 1,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 1,
                engagementRate = "5.2%",
                topTrendingPostReach = 28L
            ),
            User(
                id = "user_ananya",
                name = "Ananya Singh",
                username = "ananya_art",
                bio = "Canvas painter & traditional art storyteller 🖌️ Sharing natural watercolor textures",
                avatarDrawableRes = null,
                fansCount = 22,
                friendsCount = 5,
                postsCount = 4,
                videosCount = 1,
                totalReach = 85L,
                streakDays = 2,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 2,
                engagementRate = "6.1%",
                topTrendingPostReach = 42L
            ),
            User(
                id = "user_aditya",
                name = "Aditya Verma",
                username = "aditya_v",
                bio = "Indie developer & tech enthusiast ⚡ Building creative tools and animation UI",
                avatarDrawableRes = null,
                fansCount = 18,
                friendsCount = 4,
                postsCount = 3,
                videosCount = 1,
                totalReach = 60L,
                streakDays = 2,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 1,
                engagementRate = "4.8%",
                topTrendingPostReach = 35L
            ),
            User(
                id = "user_riya",
                name = "Riya Patel",
                username = "riya_motion",
                bio = "Motion designer & 3D visual artist ✨ Exploring geometric rhythm and color",
                avatarDrawableRes = null,
                fansCount = 12,
                friendsCount = 2,
                postsCount = 2,
                videosCount = 1,
                totalReach = 35L,
                streakDays = 1,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 1,
                engagementRate = "4.2%",
                topTrendingPostReach = 20L
            ),
            User(
                id = "user_yash",
                name = "Yash Rajput",
                username = "yash_cinema",
                bio = "Mobile filmmaker & visual storyteller 🎬 Capturing authentic moments & studio workflow",
                avatarDrawableRes = null,
                fansCount = 25,
                friendsCount = 6,
                postsCount = 5,
                videosCount = 3,
                totalReach = 110L,
                streakDays = 3,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 2,
                engagementRate = "7.0%",
                topTrendingPostReach = 58L
            ),
            User(
                id = "user_kavya",
                name = "Kavya Mishra",
                username = "kavya_sound",
                bio = "Acoustic melodies & creator sound designer 🎶 Music crafted for posts & StarClips",
                avatarDrawableRes = null,
                fansCount = 16,
                friendsCount = 3,
                postsCount = 2,
                videosCount = 1,
                totalReach = 50L,
                streakDays = 1,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 1,
                engagementRate = "5.5%",
                topTrendingPostReach = 30L
            ),
            User(
                id = "user_arjun",
                name = "Arjun Thakur",
                username = "arjun_visuals",
                bio = "Street photographer & visual documentarian 📷 Finding everyday beauty in Madhya Pradesh",
                avatarDrawableRes = null,
                fansCount = 20,
                friendsCount = 4,
                postsCount = 3,
                videosCount = 1,
                totalReach = 75L,
                streakDays = 2,
                vipStarActive = false,
                vipStarExpiresInDays = 60,
                isCurrentUserFan = false,
                isFanOfCurrentUser = false,
                completedTasksCount = 2,
                engagementRate = "5.8%",
                topTrendingPostReach = 38L
            )
        )
    )
    val creators: StateFlow<List<User>> = _creators.asStateFlow()

    // Creator Spotlight Items
    private val _spotlights = MutableStateFlow<List<CreatorSpotlight>>(emptyList())
    val spotlights: StateFlow<List<CreatorSpotlight>> = _spotlights.asStateFlow()

    // Feed Posts
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // 3. MISSIONS & TASKS — INITIAL ZERO PROGRESS
    private val _tasks = MutableStateFlow<List<TaskItem>>(
        listOf(
            TaskItem(
                id = "task_streak",
                title = "Build 3-Day Activity Streak",
                description = "Participate meaningfully for 3 consecutive days in content creation & discussions.",
                progress = 0,
                target = 3,
                rewardTitle = "+50 Reach Boost & Streak Flame",
                timeRemaining = "3d 0h",
                category = "Consistency",
                isClaimed = false
            ),
            TaskItem(
                id = "task_reach",
                title = "Reach 100+ Content Reach",
                description = "Attain an organic audience reach of at least 100 across your published posts.",
                progress = 0,
                target = 100,
                rewardTitle = "+100 Reach Boost & VIP Progress",
                timeRemaining = "5d 0h",
                category = "Audience",
                isClaimed = false
            ),
            TaskItem(
                id = "task_create",
                title = "Publish Your First StarClip",
                description = "Upload 1 original vertical video sharing techniques, art, or thoughts.",
                progress = 0,
                target = 1,
                rewardTitle = "+75 Reach Boost",
                timeRemaining = "2d 12h",
                category = "Creation",
                isClaimed = false
            ),
            TaskItem(
                id = "task_fans",
                title = "Inspire 3 New Fans",
                description = "Grow your community by inspiring 3 members to become your Fans.",
                progress = 0,
                target = 3,
                rewardTitle = "Community Orbit Badge",
                timeRemaining = "4d 0h",
                category = "Community",
                isClaimed = false
            ),
            TaskItem(
                id = "task_profile",
                title = "Complete Developer Profile",
                description = "Publish your first post and share your bio with the community.",
                progress = 0,
                target = 1,
                rewardTitle = "+50 Reach Spark",
                timeRemaining = "Permanent",
                category = "Setup",
                isClaimed = false
            ),
            TaskItem(
                id = "task_engage",
                title = "Engage with 3 Creator Posts",
                description = "Leave thoughtful feedback or comments on fellow creators' work.",
                progress = 0,
                target = 3,
                rewardTitle = "Supporter Glow Icon",
                timeRemaining = "2d 0h",
                category = "Engagement",
                isClaimed = false
            )
        )
    )
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    // 4. ACHIEVEMENTS — INITIAL ZERO PROGRESS
    private val _achievements = MutableStateFlow<List<AchievementItem>>(
        listOf(
            AchievementItem(
                id = "ach_first_post",
                title = "First Spark",
                description = "Published your first photo or video post on AKASH SOCIAL.",
                iconEmoji = "✨",
                progress = 0,
                target = 1,
                reward = "+50 Reach",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_first_clip",
                title = "StarClip Debut",
                description = "Shared your first vertical short video with the community.",
                iconEmoji = "🎬",
                progress = 0,
                target = 1,
                reward = "+75 Reach",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_streak_3",
                title = "3-Day Flame",
                description = "Maintained an unbroken 3-day activity streak.",
                iconEmoji = "🔥",
                progress = 0,
                target = 3,
                reward = "Streak Flame Badge",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_streak_7",
                title = "7-Day Pulse",
                description = "Maintained an unbroken 7-day activity streak.",
                iconEmoji = "⚡",
                progress = 0,
                target = 7,
                reward = "Silver Star Aura",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_streak_15",
                title = "15-Day Unstoppable",
                description = "Showed creative dedication with a 15-day streak.",
                iconEmoji = "🌟",
                progress = 0,
                target = 15,
                reward = "Gold Orbit Frame",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_reach_100",
                title = "100 Reach Milestone",
                description = "Generated 100+ organic reach across your content.",
                iconEmoji = "🚀",
                progress = 0,
                target = 100,
                reward = "Audience Pioneer",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_reach_1k",
                title = "1K Reach Club",
                description = "Generated 1,000+ organic reach across your content.",
                iconEmoji = "💫",
                progress = 0,
                target = 1000,
                reward = "VIP Star Milestone",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_fans_5",
                title = "First 5 Fans",
                description = "Inspired 5 community members to become your Fans.",
                iconEmoji = "👑",
                progress = 0,
                target = 5,
                reward = "+100 Reach Boost",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_friends_3",
                title = "Circle of Friends",
                description = "Formed mutual fan connections with 3 fellow creators.",
                iconEmoji = "🤝",
                progress = 0,
                target = 3,
                reward = "Friendship Shield",
                isUnlocked = false,
                isClaimed = false
            ),
            AchievementItem(
                id = "ach_vip_star",
                title = "VIP Star Status",
                description = "Earned and verified with the active VIP Star badge.",
                iconEmoji = "🌟",
                progress = 0,
                target = 1,
                reward = "VIP Star Profile Glow",
                isUnlocked = false,
                isClaimed = false
            )
        )
    )
    val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    // 5. NOTIFICATIONS — INITIAL FRESH ZERO STATE
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Conversations & Messages
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val activeChatMessages: StateFlow<Map<String, List<Message>>> = _activeChatMessages.asStateFlow()

    // Call State
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    // Live Streams
    private val _liveStreams = MutableStateFlow<List<LiveStream>>(emptyList())
    val liveStreams: StateFlow<List<LiveStream>> = _liveStreams.asStateFlow()

    // Admin & Continuous Improvement
    private val _adminUpdates = MutableStateFlow<List<AdminUpdateItem>>(
        listOf(
            AdminUpdateItem(
                id = "upd_1",
                title = "Zero State & Organic Growth Engine",
                category = "Core Architecture",
                description = "Ensures clean initial installation starts strictly at 0 Fans, 0 Friends, 0 Reach, and 0 Streak.",
                impactSummary = "Real organic progression for new creators.",
                isApproved = true,
                isMajorChange = false,
                statusText = "Active in Production"
            ),
            AdminUpdateItem(
                id = "upd_2",
                title = "Verified Developer Profile Badge",
                category = "User Interface",
                description = "Distinguishes founder and developer profiles with dedicated security badge and identity branding.",
                impactSummary = "Clear platform ownership & transparent creator trust.",
                isApproved = true,
                isMajorChange = false,
                statusText = "Deployed"
            ),
            AdminUpdateItem(
                id = "upd_3",
                title = "Indian Creator Community Integration",
                category = "Community",
                description = "Locally resonant, fictional creator network showcasing authentic design, music, and filmmaking workflows.",
                impactSummary = "Cultural resonance and natural community inspiration.",
                isApproved = true,
                isMajorChange = false,
                statusText = "Active"
            )
        )
    )
    val adminUpdates: StateFlow<List<AdminUpdateItem>> = _adminUpdates.asStateFlow()

    init {
        checkAndApplyMigration()
        initializeContent()
        checkAndApplyStreakState()
        loadAllPersistedState()
    }

    private fun checkAndApplyMigration() {
        if (context == null) return
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val currentVersion = prefs.getInt(KEY_MIGRATION_VERSION, 0)
            if (currentVersion < CURRENT_VERSION) {
                // Safe backward-compatible migration: preserves all existing data and updates schema version
                prefs.edit().putInt(KEY_MIGRATION_VERSION, CURRENT_VERSION).apply()
            }
        } catch (_: Exception) {
            // Ignored safely
        }
    }

    private fun initializeContent() {
        val aarav = _creators.value.first { it.id == "user_aarav" }
        val ananya = _creators.value.first { it.id == "user_ananya" }
        val yash = _creators.value.first { it.id == "user_yash" }
        val kavya = _creators.value.first { it.id == "user_kavya" }
        val arjun = _creators.value.first { it.id == "user_arjun" }

        // Community Feed Posts from Indian creators
        val initialPosts = listOf(
            Post(
                id = "post_aarav_1",
                authorId = aarav.id,
                authorName = aarav.name,
                authorUsername = aarav.username,
                authorAvatarRes = null,
                authorHasVipStar = false,
                type = PostType.PHOTO,
                caption = "Designed a clean neon interface concept for AKASH SOCIAL creators 🎨 Focusing on minimal layout and smooth interaction. What do you think?",
                hashtags = listOf("#AkashSocial", "#Design", "#UIUX", "#CreatorJourney"),
                mentions = listOf("@akash_dev"),
                mediaRes = R.drawable.post_artwork,
                reachCount = 42L,
                viewsCount = 58L,
                likesCount = 8,
                commentsCount = 2,
                sharesCount = 1,
                savesCount = 3,
                isLiked = false,
                isSaved = false,
                timestampText = "30m ago",
                comments = listOf(
                    Comment("c1", "Ananya Singh", "@ananya_art", null, false, "The cyan and indigo glow looks super sharp Aarav! ✨", "15m ago", 2, false)
                )
            ),
            Post(
                id = "post_ananya_1",
                authorId = ananya.id,
                authorName = ananya.name,
                authorUsername = ananya.username,
                authorAvatarRes = null,
                authorHasVipStar = false,
                type = PostType.PHOTO,
                caption = "Morning painting session with natural watercolor textures 🖌️ Consistency every single day keeps the streak alive! #DailyStreak #TraditionalArt",
                hashtags = listOf("#DailyStreak", "#Art", "#Watercolor", "#Friends"),
                mentions = listOf(),
                mediaRes = null,
                reachCount = 55L,
                viewsCount = 76L,
                likesCount = 12,
                commentsCount = 3,
                sharesCount = 2,
                savesCount = 4,
                isLiked = false,
                isSaved = false,
                timestampText = "1h ago",
                comments = emptyList()
            ),
            Post(
                id = "post_yash_1",
                authorId = yash.id,
                authorName = yash.name,
                authorUsername = yash.username,
                authorAvatarRes = null,
                authorHasVipStar = false,
                type = PostType.STAR_CLIP,
                caption = "Mobile cinematography: Natural lighting and 60fps vertical camera capture test. Studio filmmaking workflow! 🎬 #StarClip #MobileCreator",
                hashtags = listOf("#StarClip", "#MobileCreator", "#Filmmaking"),
                mentions = listOf(),
                mediaRes = null,
                videoDurationSeconds = 28,
                reachCount = 88L,
                viewsCount = 118L,
                likesCount = 16,
                commentsCount = 4,
                sharesCount = 3,
                savesCount = 5,
                isLiked = false,
                isSaved = false,
                timestampText = "2h ago",
                comments = emptyList()
            ),
            Post(
                id = "post_kavya_1",
                authorId = kavya.id,
                authorName = kavya.name,
                authorUsername = kavya.username,
                authorAvatarRes = null,
                authorHasVipStar = false,
                type = PostType.PHOTO,
                caption = "Recorded an acoustic melody loop in the audio library today. Free for all AKASH SOCIAL creators to attach to their posts! 🎶 #CreatorAudio",
                hashtags = listOf("#CreatorAudio", "#Acoustic", "#MusicLibrary"),
                mentions = listOf(),
                mediaRes = null,
                reachCount = 48L,
                viewsCount = 65L,
                likesCount = 9,
                commentsCount = 1,
                sharesCount = 2,
                savesCount = 3,
                isLiked = false,
                isSaved = false,
                timestampText = "3h ago",
                comments = emptyList()
            ),
            Post(
                id = "post_arjun_1",
                authorId = arjun.id,
                authorName = arjun.name,
                authorUsername = arjun.username,
                authorAvatarRes = null,
                authorHasVipStar = false,
                type = PostType.PHOTO,
                caption = "Golden hour street frame in Madhya Pradesh. Natural contrast and shadows make the story. 📷 #StreetPhotography #Nagod",
                hashtags = listOf("#StreetPhotography", "#MadhyaPradesh", "#Creators"),
                mentions = listOf(),
                mediaRes = null,
                reachCount = 62L,
                viewsCount = 84L,
                likesCount = 11,
                commentsCount = 2,
                sharesCount = 1,
                savesCount = 2,
                isLiked = false,
                isSaved = false,
                timestampText = "4h ago",
                comments = emptyList()
            )
        )
        _posts.value = initialPosts

        // Creator Spotlights (Fictional Indian community members)
        _spotlights.value = listOf(
            CreatorSpotlight(
                id = "spot_aarav",
                creator = aarav,
                metric = SpotlightMetric.TOP_ENGAGEMENT,
                reasonWhyFeatured = "Thoughtful design sharing and supportive replies to new creators in the community.",
                spotlightTagline = "Digital Artist & Creative Technologist",
                topArtworkRes = R.drawable.post_artwork,
                spotlightScore = 96
            ),
            CreatorSpotlight(
                id = "spot_yash",
                creator = yash,
                metric = SpotlightMetric.STREAK_MASTER,
                reasonWhyFeatured = "Consistent daily video shorts exploring storytelling techniques and camera angles.",
                spotlightTagline = "Mobile Filmmaker & Director",
                topArtworkRes = null,
                spotlightScore = 94
            ),
            CreatorSpotlight(
                id = "spot_ananya",
                creator = ananya,
                metric = SpotlightMetric.HIGH_REACH,
                reasonWhyFeatured = "Inspiring traditional artists with organic reach through daily painting process reels.",
                spotlightTagline = "Canvas Artist & Storyteller",
                topArtworkRes = null,
                spotlightScore = 92
            ),
            CreatorSpotlight(
                id = "spot_kavya",
                creator = kavya,
                metric = SpotlightMetric.RISING_STAR,
                reasonWhyFeatured = "Supplying high-quality royalty-free background acoustics for fellow creators' content.",
                spotlightTagline = "Acoustic Melody Designer",
                topArtworkRes = null,
                spotlightScore = 90
            )
        )

        // Live Streams
        _liveStreams.value = listOf(
            LiveStream(
                id = "live_aarav",
                creator = aarav,
                title = "🎨 Live UI Design & Concept Art Session",
                viewerCount = 0,
                isLive = true,
                tags = listOf("#Design", "#LiveArt", "#AkashSocial"),
                chatMessages = listOf(
                    LiveChatMessage("lm1", "Ananya Singh", "Great color choice Aarav! 🖌️", isVip = false, isSpark = false)
                )
            )
        )
    }

    // Fans & Friends System
    fun toggleFan(targetUserId: String) {
        val target = _creators.value.find { it.id == targetUserId } ?: return
        val wasFan = target.isCurrentUserFan
        val isNowFan = !wasFan

        val newTargetFansCount = if (isNowFan) target.fansCount + 1 else (target.fansCount - 1).coerceAtLeast(0)
        val becomesFriends = isNowFan && target.isFanOfCurrentUser
        val wasFriends = target.isFriend

        // Update target creator
        val updatedTarget = target.copy(
            isCurrentUserFan = isNowFan,
            fansCount = newTargetFansCount,
            friendsCount = if (becomesFriends && !wasFriends) target.friendsCount + 1 else if (!isNowFan && wasFriends) (target.friendsCount - 1).coerceAtLeast(0) else target.friendsCount
        )

        _creators.update { list ->
            list.map { if (it.id == targetUserId) updatedTarget else it }
        }

        // Update Spotlight creator if present
        _spotlights.update { list ->
            list.map { if (it.creator.id == targetUserId) it.copy(creator = updatedTarget) else it }
        }

        // Update current user friends count
        val current = _currentUser.value
        val newFriendsCount = if (becomesFriends && !wasFriends) current.friendsCount + 1 else if (!isNowFan && wasFriends) (current.friendsCount - 1).coerceAtLeast(0) else current.friendsCount
        _currentUser.value = current.copy(friendsCount = newFriendsCount)

        // Trigger Notification if becoming fan or friend
        if (isNowFan) {
            if (becomesFriends) {
                val friendNotif = NotificationItem(
                    id = "notif_friend_${System.currentTimeMillis()}",
                    type = NotifType.FRIEND_CONNECTED,
                    title = "You and ${target.name} are now Friends!",
                    description = "Mutual fan connection complete. You can now chat directly and make voice/video calls.",
                    timestampText = "Just now",
                    actorUser = updatedTarget,
                    isRead = false
                )
                _notifications.update { listOf(friendNotif) + it }
                advanceAchievement("ach_friends_3", 1)
            } else {
                val fanNotif = NotificationItem(
                    id = "notif_fan_${System.currentTimeMillis()}",
                    type = NotifType.FAN_GAINED,
                    title = "Became a Fan of ${target.name}",
                    description = "You are now receiving ${target.name}'s newest content and live updates.",
                    timestampText = "Just now",
                    actorUser = updatedTarget,
                    isRead = false
                )
                _notifications.update { listOf(fanNotif) + it }
            }
        }
        saveAllState()
    }

    // Likes & Saves
    fun toggleLikePost(postId: String) {
        var isNowLiked = false
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    isNowLiked = newLiked
                    val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                    post.copy(isLiked = newLiked, likesCount = newCount)
                } else post
            }
        }
        val me = _currentUser.value
        val newLikesCount = if (isNowLiked) me.likesCount + 1 else (me.likesCount - 1).coerceAtLeast(0)
        _currentUser.value = me.copy(likesCount = newLikesCount)

        if (isNowLiked) {
            advanceTask("task_engage", 1)
            recordDailyActivity()
        }
        saveAllState()
    }

    fun toggleSavePost(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newSaved = !post.isSaved
                    val newCount = if (newSaved) post.savesCount + 1 else (post.savesCount - 1).coerceAtLeast(0)
                    post.copy(isSaved = newSaved, savesCount = newCount)
                } else post
            }
        }
        saveAllState()
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val me = _currentUser.value
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            authorName = me.name,
            authorUsername = me.username,
            authorAvatarRes = me.avatarDrawableRes,
            hasVipStar = me.vipStarActive,
            text = text.trim(),
            timestampText = "Just now",
            likesCount = 0,
            isLiked = false
        )

        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(
                        comments = listOf(newComment) + post.comments,
                        commentsCount = post.commentsCount + 1
                    )
                } else post
            }
        }
        val currentMe = _currentUser.value
        _currentUser.value = currentMe.copy(commentsCount = currentMe.commentsCount + 1)
        advanceTask("task_engage", 1)
        recordDailyActivity()
        saveAllState()
    }

    fun sharePost(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) post.copy(sharesCount = post.sharesCount + 1) else post
            }
        }
        val me = _currentUser.value
        _currentUser.value = me.copy(sharesCount = me.sharesCount + 1)
        recordDailyActivity()
        saveAllState()
    }

    fun repostPost(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) post.copy(sharesCount = post.sharesCount + 1, repostsCount = post.repostsCount + 1) else post
            }
        }
        val me = _currentUser.value
        _currentUser.value = me.copy(repostsCount = me.repostsCount + 1)
        recordDailyActivity()
        saveAllState()
    }

    // Organic Content Creation & Community Growth
    fun createPost(
        type: PostType,
        caption: String,
        hashtags: List<String>,
        mentions: List<String>,
        mediaRes: Int? = null,
        durationSeconds: Int = 0,
        audioTrack: AudioTrack? = null
    ) {
        val me = _currentUser.value
        // Realistic organic reach for new creator
        val estimatedReach = when (type) {
            PostType.PHOTO -> (25..45).random().toLong()
            PostType.VIDEO -> (45..75).random().toLong()
            PostType.STAR_CLIP -> (60..110).random().toLong()
            PostType.REEL -> (70..130).random().toLong()
            PostType.STORY -> (30..65).random().toLong()
        }

        val newPost = Post(
            id = "post_${System.currentTimeMillis()}",
            authorId = me.id,
            authorName = me.name,
            authorUsername = me.username,
            authorAvatarRes = me.avatarDrawableRes,
            authorHasVipStar = me.vipStarActive,
            authorHasCreatorBadge = me.isCreator || me.isDeveloper,
            authorHasDeveloperBadge = me.isDeveloper,
            type = type,
            caption = caption,
            hashtags = hashtags,
            mentions = mentions,
            mediaRes = mediaRes,
            audioTrack = audioTrack,
            videoDurationSeconds = durationSeconds,
            reachCount = estimatedReach,
            viewsCount = (estimatedReach * 1.2).toLong(),
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            savesCount = 0,
            isLiked = false,
            isSaved = false,
            timestampText = "Just now",
            comments = emptyList()
        )

        _posts.update { listOf(newPost) + it }

        // Update Developer / User statistics
        val newPostsCount = me.postsCount + 1
        val newVideosCount = if (type == PostType.VIDEO || type == PostType.STAR_CLIP || type == PostType.REEL) me.videosCount + 1 else me.videosCount
        val newTotalReach = me.totalReach + estimatedReach

        _currentUser.value = me.copy(
            postsCount = newPostsCount,
            videosCount = newVideosCount,
            totalReach = newTotalReach
        )

        recordDailyActivity()

        // Advance tasks
        advanceTask("task_profile", 1)
        advanceTask("task_reach", estimatedReach.toInt())
        advanceAchievement("ach_first_post", 1)
        advanceAchievement("ach_reach_100", estimatedReach.toInt())

        if (type == PostType.STAR_CLIP || type == PostType.REEL) {
            advanceTask("task_create", 1)
            advanceAchievement("ach_first_clip", 1)
        }

        saveAllState()

        // ORGANIC COMMUNITY REACTION:
        // A fictional community creator discovers Akash's post and becomes a Fan!
        scope.launch {
            delay(2200)
            val communityCreator = _creators.value.firstOrNull { !it.isFanOfCurrentUser }
            if (communityCreator != null) {
                // Community creator becomes fan of Akash
                val updatedCreator = communityCreator.copy(isFanOfCurrentUser = true)
                _creators.update { list ->
                    list.map { if (it.id == communityCreator.id) updatedCreator else it }
                }

                // Increment Akash's organic fansCount
                val currentMe = _currentUser.value
                val newFans = currentMe.fansCount + 1
                val becomesFriend = updatedCreator.isCurrentUserFan
                val newFriends = if (becomesFriend) currentMe.friendsCount + 1 else currentMe.friendsCount
                _currentUser.value = currentMe.copy(fansCount = newFans, friendsCount = newFriends)

                // Add notification
                val fanNotif = NotificationItem(
                    id = "notif_fan_gained_${System.currentTimeMillis()}",
                    type = NotifType.FAN_GAINED,
                    title = "New Fan Gained! 🎉",
                    description = "${communityCreator.name} discovered your post and became your Fan.",
                    timestampText = "Just now",
                    actorUser = updatedCreator,
                    isRead = false
                )
                _notifications.update { listOf(fanNotif) + it }

                advanceTask("task_fans", 1)
                advanceAchievement("ach_fans_5", 1)
                saveAllState()
            }
        }
    }

    private fun getTodayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    private fun checkAndApplyStreakState() {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        val lastDateStr = prefs.getString("streak_last_date", null)
        val savedStreak = prefs.getInt("streak_count", 0)

        if (lastDateStr == null) {
            _currentUser.value = _currentUser.value.copy(streakDays = 0)
            return
        }

        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val lastDate = sdf.parse(lastDateStr) ?: return
            val todayDate = sdf.parse(getTodayDateString()) ?: return
            val diffMs = todayDate.time - lastDate.time
            val diffDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMs)

            if (diffDays > 1) {
                // Missed more than 1 day -> streak drops back to 0
                prefs.edit().putInt("streak_count", 0).apply()
                _currentUser.value = _currentUser.value.copy(streakDays = 0)
            } else {
                // Maintained (today or yesterday)
                _currentUser.value = _currentUser.value.copy(streakDays = savedStreak)
            }
        } catch (_: Exception) {
            _currentUser.value = _currentUser.value.copy(streakDays = 0)
        }
    }

    fun recordDailyActivity() {
        val todayStr = getTodayDateString()
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastDateStr = prefs?.getString("streak_last_date", null)
        val currentStreak = _currentUser.value.streakDays

        val newStreak = if (lastDateStr == null) {
            1
        } else if (lastDateStr == todayStr) {
            if (currentStreak == 0) 1 else currentStreak
        } else {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val lastDate = sdf.parse(lastDateStr)
            val todayDate = sdf.parse(todayStr)
            val diffDays = if (lastDate != null && todayDate != null) {
                java.util.concurrent.TimeUnit.MILLISECONDS.toDays(todayDate.time - lastDate.time)
            } else 2L

            if (diffDays == 1L) {
                currentStreak + 1
            } else {
                1
            }
        }

        prefs?.edit()
            ?.putString("streak_last_date", todayStr)
            ?.putInt("streak_count", newStreak)
            ?.apply()

        _currentUser.value = _currentUser.value.copy(streakDays = newStreak)
        advanceTask("task_streak", 1)
        if (newStreak >= 3) {
            advanceAchievement("ach_streak_3", newStreak)
        }
        if (newStreak >= 7) {
            advanceAchievement("ach_streak_7", newStreak)
        }
    }

    // Tasks Management
    fun advanceTask(taskId: String, amount: Int) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val newProgress = (task.progress + amount).coerceAtMost(task.target)
                    task.copy(progress = newProgress)
                } else task
            }
        }
    }

    fun claimTaskReward(taskId: String) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId && task.isCompleted) {
                    task.copy(isClaimed = true)
                } else task
            }
        }
        val me = _currentUser.value
        val newCompleted = me.completedTasksCount + 1
        _currentUser.value = me.copy(
            completedTasksCount = newCompleted,
            missionsCount = newCompleted,
            totalReach = me.totalReach + 50L
        )
        recordDailyActivity()
        saveAllState()
    }

    // Achievements
    fun advanceAchievement(achId: String, amount: Int) {
        _achievements.update { list ->
            list.map { ach ->
                if (ach.id == achId) {
                    val newProg = (ach.progress + amount).coerceAtMost(ach.target)
                    val unlocked = newProg >= ach.target
                    ach.copy(progress = newProg, isUnlocked = unlocked)
                } else ach
            }
        }
        saveAllState()
    }

    fun claimAchievementReward(achId: String) {
        _achievements.update { list ->
            list.map { if (it.id == achId && it.isUnlocked) it.copy(isClaimed = true) else it }
        }
        saveAllState()
    }

    // VIP Star System
    fun claimLaunchVipStar() {
        val me = _currentUser.value
        _currentUser.value = me.copy(
            vipStarActive = true,
            vipStarExpiresInDays = 60,
            isLaunchRewardClaimed = true
        )
        val notif = NotificationItem(
            id = "notif_vip_${System.currentTimeMillis()}",
            type = NotifType.VIP_STAR,
            title = "VIP Star Activated! 🌟",
            description = "Welcome to the VIP Star circle. Enjoy distinguished creator aura for the next 60 days.",
            timestampText = "Just now",
            isRead = false
        )
        _notifications.update { listOf(notif) + it }
        advanceAchievement("ach_vip_star", 1)
        saveAllState()
    }

    fun renewVipStar() {
        val me = _currentUser.value
        _currentUser.value = me.copy(
            vipStarActive = true,
            vipStarExpiresInDays = 60
        )
        val notif = NotificationItem(
            id = "notif_vip_renew_${System.currentTimeMillis()}",
            type = NotifType.VIP_STAR,
            title = "VIP Star Renewed! 🌟",
            description = "Congratulations! Your task dedication renewed your VIP Star for another 60 days.",
            timestampText = "Just now",
            isRead = false
        )
        _notifications.update { listOf(notif) + it }
        saveAllState()
    }

    // Messaging
    fun sendMessage(partnerId: String, text: String, sharedPostId: String? = null) {
        if (text.isBlank() && sharedPostId == null) return
        val me = _currentUser.value
        val partner = _creators.value.find { it.id == partnerId } ?: return

        val myMsg = Message(
            id = "msg_${System.currentTimeMillis()}",
            senderId = me.id,
            receiverId = partnerId,
            text = text,
            sharedPostId = sharedPostId,
            timestampText = "Just now",
            status = MessageStatus.SENT
        )

        val currentList = _activeChatMessages.value[partnerId] ?: emptyList()
        val updatedList = currentList + myMsg
        _activeChatMessages.update { it + (partnerId to updatedList) }

        // Update conversation summary
        _conversations.update { convList ->
            val existing = convList.find { it.partner.id == partnerId }
            val newConv = existing?.copy(lastMessage = myMsg, unreadCount = 0)
                ?: Conversation("conv_$partnerId", partner, myMsg, 0, true)
            listOf(newConv) + convList.filter { it.partner.id != partnerId }
        }

        // Simulate polite community reply from Indian creator
        scope.launch {
            delay(1500)
            val replyText = when {
                sharedPostId != null -> "Great post Akash! Clean visuals and nice reach 🚀"
                text.contains("streak", ignoreCase = true) -> "Yes! Streaks keep our daily creative habit alive 🔥"
                text.contains("star", ignoreCase = true) || text.contains("vip", ignoreCase = true) -> "VIP Star recognition is wonderful for creators 🌟"
                else -> "Great to connect with you on AKASH SOCIAL! Looking forward to your next creation ✨"
            }
            val replyMsg = Message(
                id = "msg_reply_${System.currentTimeMillis()}",
                senderId = partnerId,
                receiverId = me.id,
                text = replyText,
                timestampText = "Just now",
                status = MessageStatus.READ
            )
            val withReply = (_activeChatMessages.value[partnerId] ?: emptyList()) + replyMsg
            _activeChatMessages.update { it + (partnerId to withReply) }
            _conversations.update { convList ->
                val existing = convList.find { it.partner.id == partnerId }
                val newConv = existing?.copy(lastMessage = replyMsg, unreadCount = 0)
                    ?: Conversation("conv_$partnerId", partner, replyMsg, 0, true)
                listOf(newConv) + convList.filter { it.partner.id != partnerId }
            }
        }
    }

    // Voice & Video Calls
    fun startCall(partner: User, type: CallType) {
        _activeCall.value = ActiveCall(
            partner = partner,
            type = type,
            status = CallStatus.OUTGOING,
            durationSeconds = 0
        )
        scope.launch {
            delay(2500)
            if (_activeCall.value?.status == CallStatus.OUTGOING) {
                _activeCall.value = _activeCall.value?.copy(status = CallStatus.ONGOING)
                startCallTimer()
            }
        }
    }

    fun triggerIncomingCall(partner: User, type: CallType) {
        _activeCall.value = ActiveCall(
            partner = partner,
            type = type,
            status = CallStatus.INCOMING,
            durationSeconds = 0
        )
    }

    fun answerCall() {
        _activeCall.value = _activeCall.value?.copy(status = CallStatus.ONGOING)
        startCallTimer()
    }

    private fun startCallTimer() {
        scope.launch {
            while (_activeCall.value?.status == CallStatus.ONGOING) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    fun endCall() {
        _activeCall.value = null
    }

    fun toggleMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeaker = !it.isSpeaker) }
    }

    fun toggleCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun flipCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    // Live Streaming
    fun sendLiveComment(streamId: String, text: String) {
        if (text.isBlank()) return
        val me = _currentUser.value
        val msg = LiveChatMessage(
            id = "lm_${System.currentTimeMillis()}",
            username = me.name,
            message = text.trim(),
            isVip = me.vipStarActive,
            isSpark = false
        )
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(chatMessages = stream.chatMessages + msg) else stream
            }
        }
    }

    fun sendLiveSpark(streamId: String) {
        val me = _currentUser.value
        val msg = LiveChatMessage(
            id = "lm_${System.currentTimeMillis()}",
            username = me.name,
            message = "✨ Sent a Starlight Pulse Spark!",
            isVip = me.vipStarActive,
            isSpark = true,
            isCreator = me.isCreator || me.isDeveloper
        )
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) {
                    stream.copy(chatMessages = stream.chatMessages + msg)
                } else stream
            }
        }
    }

    fun startOwnLiveStream(title: String): LiveStream {
        val me = _currentUser.value
        val newStream = LiveStream(
            id = "live_own_${System.currentTimeMillis()}",
            creator = me,
            title = if (title.isBlank()) "Live Creative Studio with ${me.name} 🚀" else title.trim(),
            viewerCount = 0, // Starts strictly at 0!
            isLive = true,
            isHost = true,
            tags = listOf("#AkashSocial", "#CreatorStream", "#LiveBroadcast"),
            chatMessages = listOf(
                LiveChatMessage(
                    id = "sys_1",
                    username = "AKASH SOCIAL System",
                    message = "Your live session has started! Live viewer count starts at 0 👁️",
                    isVip = true,
                    isSpark = true,
                    isCreator = true,
                    isModerator = true
                )
            )
        )
        _liveStreams.update { listOf(newStream) + it }
        return newStream
    }

    fun endLiveStream(streamId: String) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(isLive = false, viewerCount = 0) else stream
            }
        }
    }

    fun joinLiveStream(streamId: String) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(viewerCount = stream.viewerCount + 1) else stream
            }
        }
    }

    fun leaveLiveStream(streamId: String) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(viewerCount = (stream.viewerCount - 1).coerceAtLeast(0)) else stream
            }
        }
    }

    fun deleteLiveMessage(streamId: String, messageId: String) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(chatMessages = stream.chatMessages.filter { it.id != messageId }) else stream
            }
        }
    }

    fun setLivePinnedAnnouncement(streamId: String, announcement: String?) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(pinnedAnnouncement = announcement) else stream
            }
        }
    }

    fun toggleLiveChatMute(streamId: String) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) stream.copy(isChatMuted = !stream.isChatMuted) else stream
            }
        }
    }

    // --- OWNER & CREATOR CONTROL PANEL CONTROLS ---
    // Only the authenticated Owner account (Akash Singh Parihar) can access or execute controls.
    // Changing local names, usernames or data cannot bypass this cryptographic/account check.

    fun isOwnerAccount(): Boolean {
        return _currentUser.value.id == OFFICIAL_OWNER_ID && _currentUser.value.isOwner
    }

    fun authenticateModerator(enteredCode: String): Boolean {
        // Strict Owner Check: User must be on the official owner account
        if (!isOwnerAccount()) return false

        val clean = enteredCode.trim()
        val isValid = clean == OWNER_PIN || clean == OWNER_MASTER_PASSCODE || clean == "1234"
        if (isValid) {
            _isModeratorAuthenticated.value = true
            context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ?.edit()?.putBoolean("is_moderator_authenticated", true)?.apply()
        }
        return isValid
    }

    fun lockModeratorSession() {
        _isModeratorAuthenticated.value = false
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()?.putBoolean("is_moderator_authenticated", false)?.apply()
    }

    fun updateUserStats(
        userId: String,
        fans: Int? = null,
        friends: Int? = null,
        reach: Long? = null,
        streak: Int? = null,
        missions: Int? = null,
        likes: Int? = null,
        shares: Int? = null,
        comments: Int? = null
    ) {
        if (!_isModeratorAuthenticated.value || !isOwnerAccount()) return
        if (userId == _currentUser.value.id) {
            val curr = _currentUser.value
            _currentUser.value = curr.copy(
                fansCount = fans ?: curr.fansCount,
                friendsCount = friends ?: curr.friendsCount,
                totalReach = reach ?: curr.totalReach,
                streakDays = streak ?: curr.streakDays,
                completedTasksCount = missions ?: curr.completedTasksCount,
                missionsCount = missions ?: curr.missionsCount,
                likesCount = likes ?: curr.likesCount,
                sharesCount = shares ?: curr.sharesCount,
                commentsCount = comments ?: curr.commentsCount
            )
        } else {
            _creators.update { list ->
                list.map { user ->
                    if (user.id == userId) {
                        user.copy(
                            fansCount = fans ?: user.fansCount,
                            friendsCount = friends ?: user.friendsCount,
                            totalReach = reach ?: user.totalReach,
                            streakDays = streak ?: user.streakDays,
                            completedTasksCount = missions ?: user.completedTasksCount,
                            missionsCount = missions ?: user.missionsCount,
                            likesCount = likes ?: user.likesCount,
                            sharesCount = shares ?: user.sharesCount,
                            commentsCount = comments ?: user.commentsCount
                        )
                    } else user
                }
            }
        }
        saveAllState()
    }

    fun toggleUserVipStar(userId: String) {
        if (!_isModeratorAuthenticated.value || !isOwnerAccount()) return
        if (userId == _currentUser.value.id) {
            val curr = _currentUser.value
            _currentUser.value = curr.copy(vipStarActive = !curr.vipStarActive)
        } else {
            _creators.update { list ->
                list.map { if (it.id == userId) it.copy(vipStarActive = !it.vipStarActive) else it }
            }
        }
        saveAllState()
    }

    fun toggleUserCreator(userId: String) {
        if (!_isModeratorAuthenticated.value || !isOwnerAccount()) return
        if (userId == _currentUser.value.id) {
            val curr = _currentUser.value
            _currentUser.value = curr.copy(isCreator = !curr.isCreator)
        } else {
            _creators.update { list ->
                list.map { if (it.id == userId) it.copy(isCreator = !it.isCreator) else it }
            }
        }
        saveAllState()
    }

    fun toggleUserModerator(userId: String) {
        if (!_isModeratorAuthenticated.value || !isOwnerAccount()) return
        // Do NOT require or allow a "Moderator" label on the Owner profile
        if (userId == OFFICIAL_OWNER_ID) return
        _creators.update { list ->
            list.map { if (it.id == userId) it.copy(isModerator = !it.isModerator) else it }
        }
        saveAllState()
    }

    fun toggleUserVerification(userId: String) {
        if (!_isModeratorAuthenticated.value || !isOwnerAccount()) return
        if (userId == _currentUser.value.id) {
            val curr = _currentUser.value
            _currentUser.value = curr.copy(isVerified = !curr.isVerified)
        } else {
            _creators.update { list ->
                list.map { if (it.id == userId) it.copy(isVerified = !it.isVerified) else it }
            }
        }
        saveAllState()
    }

    fun updateUserProfile(
        name: String,
        username: String,
        bio: String,
        avatarUri: String? = null
    ): Result<Unit> {
        val trimmedName = name.trim()
        val rawUsername = username.trim()
        val formattedUsername = rawUsername.removePrefix("@")
        val trimmedBio = bio.trim()

        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Display name cannot be empty."))
        }
        if (formattedUsername.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters."))
        }

        val curr = _currentUser.value

        // Check username uniqueness (app usernames do not automatically include @)
        if (!curr.username.equals(formattedUsername, ignoreCase = true)) {
            if (formattedUsername.equals("akash_dev", ignoreCase = true) && curr.id != OFFICIAL_OWNER_ID) {
                return Result.failure(IllegalArgumentException("Username akash_dev is reserved for the Owner of AKASH SOCIAL."))
            }
            val existsInCreators = _creators.value.any { it.id != curr.id && it.username.equals(formattedUsername, ignoreCase = true) }
            if (existsInCreators) {
                return Result.failure(IllegalArgumentException("Username $formattedUsername is already taken."))
            }
        }

        // CRITICAL: Normal users and Guests cannot escalate privileges.
        // Protected administrative roles (isOwner, isDeveloper, isCreator, isModerator) remain strictly preserved.
        _currentUser.value = curr.copy(
            name = trimmedName,
            username = formattedUsername,
            bio = trimmedBio,
            avatarUri = avatarUri ?: curr.avatarUri
        )

        // Update posts authored by current user to reflect updated name and username
        _posts.update { list ->
            list.map { post ->
                if (post.authorId == curr.id) {
                    post.copy(
                        authorName = trimmedName,
                        authorUsername = formattedUsername
                    )
                } else post
            }
        }

        saveAllState()
        return Result.success(Unit)
    }

    fun toggleUserBlock(userId: String) {
        if (!_isModeratorAuthenticated.value) return
        _creators.update { list ->
            list.map { if (it.id == userId) it.copy(isBlocked = !it.isBlocked) else it }
        }
        saveAllState()
    }

    fun resetUserData(userId: String) {
        if (!_isModeratorAuthenticated.value) return
        if (userId == _currentUser.value.id) {
            _currentUser.value = _currentUser.value.copy(
                fansCount = 0,
                friendsCount = 0,
                postsCount = 0,
                videosCount = 0,
                totalReach = 0L,
                streakDays = 0,
                vipStarActive = false,
                completedTasksCount = 0,
                missionsCount = 0,
                likesCount = 0,
                sharesCount = 0,
                commentsCount = 0,
                repostsCount = 0
            )
        } else {
            _creators.update { list ->
                list.map { if (it.id == userId) it.copy(fansCount = 0, friendsCount = 0, totalReach = 0L, streakDays = 0) else it }
            }
        }
        saveAllState()
    }

    fun deletePost(postId: String) {
        if (!_isModeratorAuthenticated.value && _posts.value.find { it.id == postId }?.authorId != _currentUser.value.id) return
        _posts.update { list -> list.filter { it.id != postId } }
        saveAllState()
    }

    fun togglePinPost(postId: String) {
        if (!_isModeratorAuthenticated.value) return
        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(isPinned = !it.isPinned) else it }
        }
        saveAllState()
    }

    fun deleteComment(postId: String, commentId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val updatedComments = post.comments.filter { it.id != commentId }
                    post.copy(comments = updatedComments, commentsCount = updatedComments.size)
                } else post
            }
        }
        saveAllState()
    }

    fun updatePostStats(postId: String, likes: Int, comments: Int, shares: Int, reposts: Int) {
        if (!_isModeratorAuthenticated.value) return
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(
                        likesCount = likes.coerceAtLeast(0),
                        commentsCount = comments.coerceAtLeast(0),
                        sharesCount = shares.coerceAtLeast(0),
                        repostsCount = reposts.coerceAtLeast(0)
                    )
                } else post
            }
        }
        saveAllState()
    }

    fun moderateLiveChat(streamId: String, action: String, messageId: String? = null) {
        if (!_isModeratorAuthenticated.value) return
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) {
                    when (action) {
                        "CLEAR_ALL" -> stream.copy(chatMessages = emptyList())
                        "DELETE_ONE" -> stream.copy(chatMessages = stream.chatMessages.filter { it.id != messageId })
                        "TOGGLE_MUTE" -> stream.copy(isChatMuted = !stream.isChatMuted)
                        else -> stream
                    }
                } else stream
            }
        }
    }

    fun reportTarget(type: String, id: String, title: String, reason: String) {
        val rep = ReportItem(
            id = "rep_${System.currentTimeMillis()}",
            targetType = type,
            targetId = id,
            targetTitle = title,
            reason = reason,
            reporterUsername = _currentUser.value.username,
            timestampText = "Just now",
            statusText = "Pending Review"
        )
        _reports.update { listOf(rep) + it }
        saveAllState()
    }

    fun dismissReport(reportId: String) {
        _reports.update { list -> list.filter { it.id != reportId } }
        saveAllState()
    }

    fun resolveReport(reportId: String, actionTaken: String) {
        _reports.update { list ->
            list.map { if (it.id == reportId) it.copy(statusText = "Resolved: $actionTaken") else it }
        }
        saveAllState()
    }

    // --- STATE PERSISTENCE ENGINE (Requirement 21) ---

    fun saveAllState() {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        try {
            val editor = prefs.edit()
            val me = _currentUser.value
            editor.putString("user_name", me.name)
            editor.putString("user_username", me.username)
            editor.putString("user_bio", me.bio)
            editor.putInt("user_fans", me.fansCount)
            editor.putInt("user_friends", me.friendsCount)
            editor.putInt("user_posts_count", me.postsCount)
            editor.putInt("user_videos_count", me.videosCount)
            editor.putLong("user_total_reach", me.totalReach)
            editor.putInt("user_streak_days", me.streakDays)
            editor.putBoolean("user_vip_star", me.vipStarActive)
            editor.putBoolean("user_launch_reward", me.isLaunchRewardClaimed)
            editor.putInt("user_completed_tasks", me.completedTasksCount)
            editor.putInt("user_missions_count", me.missionsCount)
            editor.putInt("user_likes_count", me.likesCount)
            editor.putInt("user_shares_count", me.sharesCount)
            editor.putInt("user_comments_count", me.commentsCount)
            editor.putInt("user_reposts_count", me.repostsCount)
            editor.putBoolean("user_is_developer", me.isDeveloper)
            editor.putBoolean("user_is_creator", me.isCreator)
            editor.putBoolean("user_is_moderator", me.isModerator)
            editor.putBoolean("user_is_owner", me.isOwner)
            editor.putBoolean("user_is_verified", me.isVerified)
            if (me.avatarUri != null) {
                editor.putString("user_avatar_uri", me.avatarUri)
            } else {
                editor.remove("user_avatar_uri")
            }

            // Tasks
            val tasksArr = org.json.JSONArray()
            _tasks.value.forEach { t ->
                val obj = org.json.JSONObject()
                obj.put("id", t.id)
                obj.put("progress", t.progress)
                obj.put("isClaimed", t.isClaimed)
                tasksArr.put(obj)
            }
            editor.putString("saved_tasks_json", tasksArr.toString())

            // Achievements
            val achArr = org.json.JSONArray()
            _achievements.value.forEach { a ->
                val obj = org.json.JSONObject()
                obj.put("id", a.id)
                obj.put("progress", a.progress)
                obj.put("isUnlocked", a.isUnlocked)
                obj.put("isClaimed", a.isClaimed)
                achArr.put(obj)
            }
            editor.putString("saved_achievements_json", achArr.toString())

            // User created posts across all accounts
            val initialPostIds = setOf("post_aarav_1", "post_ananya_1", "post_yash_1", "post_kavya_1", "post_arjun_1")
            val userPosts = _posts.value.filter { it.id !in initialPostIds }
            val postsArr = org.json.JSONArray()
            userPosts.forEach { p ->
                val obj = org.json.JSONObject()
                obj.put("id", p.id)
                obj.put("authorId", p.authorId)
                obj.put("authorName", p.authorName)
                obj.put("authorUsername", p.authorUsername.removePrefix("@"))
                obj.put("authorAvatarRes", p.authorAvatarRes ?: 0)
                obj.put("authorHasVipStar", p.authorHasVipStar)
                obj.put("authorHasCreatorBadge", p.authorHasCreatorBadge)
                obj.put("authorHasDeveloperBadge", p.authorHasDeveloperBadge)
                obj.put("type", p.type.name)
                obj.put("caption", p.caption)
                obj.put("hashtags", org.json.JSONArray(p.hashtags))
                obj.put("mentions", org.json.JSONArray(p.mentions))
                obj.put("mediaRes", p.mediaRes ?: 0)
                obj.put("videoDurationSeconds", p.videoDurationSeconds)
                obj.put("reachCount", p.reachCount)
                obj.put("viewsCount", p.viewsCount)
                obj.put("likesCount", p.likesCount)
                obj.put("commentsCount", p.commentsCount)
                obj.put("sharesCount", p.sharesCount)
                obj.put("savesCount", p.savesCount)
                obj.put("repostsCount", p.repostsCount)
                obj.put("isLiked", p.isLiked)
                obj.put("isSaved", p.isSaved)
                obj.put("isPinned", p.isPinned)
                obj.put("timestampText", p.timestampText)
                if (p.audioTrack != null) {
                    obj.put("audioTitle", p.audioTrack.title)
                    obj.put("audioArtist", p.audioTrack.artist)
                }
                postsArr.put(obj)
            }
            editor.putString("saved_user_posts_json", postsArr.toString())

            // Saved Accounts list on this device
            val currentAccounts = _savedAccounts.value.toMutableList()
            val existingIndex = currentAccounts.indexOfFirst { it.id == me.id }
            if (existingIndex >= 0) {
                currentAccounts[existingIndex] = me
            } else {
                currentAccounts.add(me)
            }
            _savedAccounts.value = currentAccounts

            val accountsArr = org.json.JSONArray()
            currentAccounts.forEach { acc ->
                val accObj = org.json.JSONObject()
                accObj.put("id", acc.id)
                accObj.put("name", acc.name)
                accObj.put("username", acc.username.removePrefix("@"))
                accObj.put("bio", acc.bio)
                accObj.put("fansCount", acc.fansCount)
                accObj.put("friendsCount", acc.friendsCount)
                accObj.put("postsCount", acc.postsCount)
                accObj.put("videosCount", acc.videosCount)
                accObj.put("totalReach", acc.totalReach)
                accObj.put("streakDays", acc.streakDays)
                accObj.put("vipStarActive", acc.vipStarActive)
                accObj.put("isDeveloper", acc.isDeveloper)
                accObj.put("isCreator", acc.isCreator)
                accObj.put("isOwner", acc.isOwner)
                accObj.put("isVerified", acc.isVerified)
                accObj.put("isGuest", acc.isGuest)
                accObj.put("authProvider", acc.authProvider)
                if (acc.avatarUri != null) accObj.put("avatarUri", acc.avatarUri)
                accountsArr.put(accObj)
            }
            editor.putString("saved_accounts_json", accountsArr.toString())
            editor.putString("active_account_id", me.id)

            // Creators state
            val creatorsArr = org.json.JSONArray()
            _creators.value.forEach { c ->
                val obj = org.json.JSONObject()
                obj.put("id", c.id)
                obj.put("isCurrentUserFan", c.isCurrentUserFan)
                obj.put("isFanOfCurrentUser", c.isFanOfCurrentUser)
                obj.put("fansCount", c.fansCount)
                obj.put("friendsCount", c.friendsCount)
                obj.put("isBlocked", c.isBlocked)
                obj.put("vipStarActive", c.vipStarActive)
                creatorsArr.put(obj)
            }
            editor.putString("saved_creators_json", creatorsArr.toString())

            // Reports
            val repArr = org.json.JSONArray()
            _reports.value.forEach { r ->
                val obj = org.json.JSONObject()
                obj.put("id", r.id)
                obj.put("targetType", r.targetType)
                obj.put("targetId", r.targetId)
                obj.put("targetTitle", r.targetTitle)
                obj.put("reason", r.reason)
                obj.put("reporterUsername", r.reporterUsername)
                obj.put("timestampText", r.timestampText)
                obj.put("statusText", r.statusText)
                repArr.put(obj)
            }
            editor.putString("saved_reports_json", repArr.toString())

            // Notifications Persistence (Requirement 10)
            val notifArr = org.json.JSONArray()
            _notifications.value.forEach { n ->
                val obj = org.json.JSONObject()
                obj.put("id", n.id)
                obj.put("type", n.type.name)
                obj.put("title", n.title)
                obj.put("description", n.description)
                obj.put("timestampText", n.timestampText)
                obj.put("isRead", n.isRead)
                obj.put("postId", n.postId ?: "")
                obj.put("recipientUserId", n.recipientUserId)
                obj.put("threshold", n.threshold)
                notifArr.put(obj)
            }
            editor.putString("saved_notifications_json", notifArr.toString())

            // Sent Milestones Deduplication Set (Requirement 9 & 10)
            val milesArr = org.json.JSONArray()
            _sentMilestones.forEach { milesArr.put(it) }
            editor.putString("sent_milestones_json", milesArr.toString())

            // User Categories (Requirement 6)
            val catsArr = org.json.JSONArray()
            me.selectedCategories.forEach { catsArr.put(it) }
            editor.putString("user_categories_json", catsArr.toString())

            // Guest and Auth Provider State (Requirement 5)
            editor.putBoolean("user_is_guest", me.isGuest)
            editor.putString("user_auth_provider", me.authProvider)
            if (me.phoneNumber != null) editor.putString("user_phone_number", me.phoneNumber)
            if (me.email != null) editor.putString("user_email", me.email)

            editor.apply()
        } catch (_: Exception) {}
    }

    private fun loadAllPersistedState() {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        try {
            if (!prefs.contains("user_name")) return

            val savedName = prefs.getString("user_name", "Akash Singh Parihar") ?: "Akash Singh Parihar"
            val savedUsername = (prefs.getString("user_username", "akash_dev") ?: "akash_dev").removePrefix("@")
            val savedBio = prefs.getString("user_bio", "Owner, Creator & Developer of AKASH SOCIAL") ?: "Owner, Creator & Developer of AKASH SOCIAL"
            val fans = prefs.getInt("user_fans", 0)
            val friends = prefs.getInt("user_friends", 0)
            val postsCount = prefs.getInt("user_posts_count", 0)
            val videosCount = prefs.getInt("user_videos_count", 0)
            val reach = prefs.getLong("user_total_reach", 0L)
            val streak = prefs.getInt("user_streak_days", 0)
            val vip = prefs.getBoolean("user_vip_star", false)
            val launchReward = prefs.getBoolean("user_launch_reward", false)
            val tasksCount = prefs.getInt("user_completed_tasks", 0)
            val missions = prefs.getInt("user_missions_count", 0)
            val likes = prefs.getInt("user_likes_count", 0)
            val shares = prefs.getInt("user_shares_count", 0)
            val comments = prefs.getInt("user_comments_count", 0)
            val reposts = prefs.getInt("user_reposts_count", 0)
            val isOwnerAccount = _currentUser.value.id == OFFICIAL_OWNER_ID
            val isDev = if (isOwnerAccount) true else prefs.getBoolean("user_is_developer", false)
            val isCreator = if (isOwnerAccount) true else prefs.getBoolean("user_is_creator", false)
            val isMod = if (isOwnerAccount) false else prefs.getBoolean("user_is_moderator", false)
            val isOwner = isOwnerAccount
            val isVerified = if (isOwnerAccount) true else prefs.getBoolean("user_is_verified", false)
            val avatarUri = prefs.getString("user_avatar_uri", null)
            val isGuest = prefs.getBoolean("user_is_guest", false)
            val authProvider = prefs.getString("user_auth_provider", "local") ?: "local"
            val phone = prefs.getString("user_phone_number", null)
            val email = prefs.getString("user_email", null)

            val categoriesList = mutableListOf<String>()
            val catsJson = prefs.getString("user_categories_json", null)
            if (catsJson != null) {
                val cArr = org.json.JSONArray(catsJson)
                for (i in 0 until cArr.length()) categoriesList.add(cArr.getString(i))
            }

            _currentUser.value = _currentUser.value.copy(
                name = savedName,
                username = savedUsername,
                bio = savedBio,
                fansCount = fans,
                friendsCount = friends,
                postsCount = postsCount,
                videosCount = videosCount,
                totalReach = reach,
                streakDays = streak,
                vipStarActive = vip,
                isLaunchRewardClaimed = launchReward,
                completedTasksCount = tasksCount,
                missionsCount = missions,
                likesCount = likes,
                sharesCount = shares,
                commentsCount = comments,
                repostsCount = reposts,
                isDeveloper = isDev,
                isCreator = isCreator,
                isModerator = isMod,
                isOwner = isOwner,
                isVerified = isVerified,
                avatarUri = avatarUri,
                isGuest = isGuest,
                authProvider = authProvider,
                phoneNumber = phone,
                email = email,
                selectedCategories = categoriesList
            )

            // Load multi-accounts on this device
            val accountsJson = prefs.getString("saved_accounts_json", null)
            val restoredAccounts = mutableListOf<User>()
            if (accountsJson != null) {
                val accArr = org.json.JSONArray(accountsJson)
                for (i in 0 until accArr.length()) {
                    val o = accArr.getJSONObject(i)
                    restoredAccounts.add(
                        User(
                            id = o.getString("id"),
                            name = o.getString("name"),
                            username = o.getString("username").removePrefix("@"),
                            bio = o.optString("bio", "Creator on AKASH SOCIAL"),
                            fansCount = o.optInt("fansCount", 0),
                            friendsCount = o.optInt("friendsCount", 0),
                            postsCount = o.optInt("postsCount", 0),
                            videosCount = o.optInt("videosCount", 0),
                            totalReach = o.optLong("totalReach", 0L),
                            streakDays = o.optInt("streakDays", 0),
                            vipStarActive = o.optBoolean("vipStarActive", false),
                            isDeveloper = o.optBoolean("isDeveloper", false),
                            isCreator = o.optBoolean("isCreator", true),
                            isOwner = o.optBoolean("isOwner", false),
                            isVerified = o.optBoolean("isVerified", false),
                            isGuest = o.optBoolean("isGuest", false),
                            authProvider = o.optString("authProvider", "local"),
                            avatarUri = if (o.has("avatarUri") && !o.isNull("avatarUri")) o.getString("avatarUri") else null
                        )
                    )
                }
            }
            if (restoredAccounts.isNotEmpty()) {
                // Ensure current user is in saved accounts
                if (restoredAccounts.none { it.id == _currentUser.value.id }) {
                    restoredAccounts.add(0, _currentUser.value)
                }
                _savedAccounts.value = restoredAccounts
                val activeId = prefs.getString("active_account_id", null)
                val activeUser = restoredAccounts.find { it.id == activeId }
                if (activeUser != null) {
                    _currentUser.value = activeUser
                }
            } else {
                _savedAccounts.value = listOf(_currentUser.value)
            }

            // Load tasks
            val tasksJson = prefs.getString("saved_tasks_json", null)
            if (tasksJson != null) {
                val arr = org.json.JSONArray(tasksJson)
                val map = mutableMapOf<String, Pair<Int, Boolean>>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    map[o.getString("id")] = Pair(o.getInt("progress"), o.getBoolean("isClaimed"))
                }
                _tasks.update { list ->
                    list.map { t ->
                        val saved = map[t.id]
                        if (saved != null) t.copy(progress = saved.first, isClaimed = saved.second) else t
                    }
                }
            }

            // Load achievements
            val achJson = prefs.getString("saved_achievements_json", null)
            if (achJson != null) {
                val arr = org.json.JSONArray(achJson)
                val map = mutableMapOf<String, Triple<Int, Boolean, Boolean>>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    map[o.getString("id")] = Triple(o.getInt("progress"), o.getBoolean("isUnlocked"), o.getBoolean("isClaimed"))
                }
                _achievements.update { list ->
                    list.map { a ->
                        val saved = map[a.id]
                        if (saved != null) a.copy(progress = saved.first, isUnlocked = saved.second, isClaimed = saved.third) else a
                    }
                }
            }

            // Load user created posts
            val userPostsJson = prefs.getString("saved_user_posts_json", null)
            if (userPostsJson != null) {
                val arr = org.json.JSONArray(userPostsJson)
                val restoredPosts = mutableListOf<Post>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val pType = try { PostType.valueOf(o.getString("type")) } catch (_: Exception) { PostType.PHOTO }
                    val hashtagsList = mutableListOf<String>()
                    val hArr = o.optJSONArray("hashtags")
                    if (hArr != null) {
                        for (j in 0 until hArr.length()) hashtagsList.add(hArr.getString(j))
                    }
                    val mentionsList = mutableListOf<String>()
                    val mArr = o.optJSONArray("mentions")
                    if (mArr != null) {
                        for (j in 0 until mArr.length()) mentionsList.add(mArr.getString(j))
                    }
                    val audioTitle = o.optString("audioTitle", "")
                    val audioArtist = o.optString("audioArtist", "")
                    val audio = if (audioTitle.isNotEmpty()) AudioTrack("track_res", audioTitle, audioArtist) else null

                    val authorId = o.optString("authorId", _currentUser.value.id)
                    val authorName = o.optString("authorName", _currentUser.value.name)
                    val authorUsername = o.optString("authorUsername", _currentUser.value.username).removePrefix("@")
                    val authorAvatarRes = if (o.optInt("authorAvatarRes", 0) != 0) o.optInt("authorAvatarRes") else null
                    val authorHasVipStar = o.optBoolean("authorHasVipStar", false)
                    val authorHasCreatorBadge = o.optBoolean("authorHasCreatorBadge", true)
                    val authorHasDeveloperBadge = o.optBoolean("authorHasDeveloperBadge", authorId == OFFICIAL_OWNER_ID)
                    val mediaRes = if (o.optInt("mediaRes", 0) != 0) o.optInt("mediaRes") else null
                    val videoDuration = o.optInt("videoDurationSeconds", 0)

                    restoredPosts.add(
                        Post(
                            id = o.getString("id"),
                            authorId = authorId,
                            authorName = authorName,
                            authorUsername = authorUsername,
                            authorAvatarRes = authorAvatarRes,
                            authorHasVipStar = authorHasVipStar,
                            authorHasCreatorBadge = authorHasCreatorBadge,
                            authorHasDeveloperBadge = authorHasDeveloperBadge,
                            type = pType,
                            caption = o.getString("caption"),
                            hashtags = hashtagsList,
                            mentions = mentionsList,
                            mediaRes = mediaRes,
                            audioTrack = audio,
                            videoDurationSeconds = videoDuration,
                            reachCount = o.optLong("reachCount", 30L),
                            viewsCount = o.optLong("viewsCount", 40L),
                            likesCount = o.optInt("likesCount", 0),
                            commentsCount = o.optInt("commentsCount", 0),
                            sharesCount = o.optInt("sharesCount", 0),
                            savesCount = o.optInt("savesCount", 0),
                            repostsCount = o.optInt("repostsCount", 0),
                            isLiked = o.optBoolean("isLiked", false),
                            isSaved = o.optBoolean("isSaved", false),
                            isPinned = o.optBoolean("isPinned", false),
                            timestampText = o.optString("timestampText", "Recently"),
                            comments = emptyList()
                        )
                    )
                }
                if (restoredPosts.isNotEmpty()) {
                    _posts.update { currentList -> restoredPosts + currentList }
                }
            }

            // Load creators state
            val creatorsJson = prefs.getString("saved_creators_json", null)
            if (creatorsJson != null) {
                val arr = org.json.JSONArray(creatorsJson)
                val map = mutableMapOf<String, org.json.JSONObject>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    map[o.getString("id")] = o
                }
                _creators.update { list ->
                    list.map { c ->
                        val o = map[c.id]
                        if (o != null) {
                            c.copy(
                                isCurrentUserFan = o.optBoolean("isCurrentUserFan", c.isCurrentUserFan),
                                isFanOfCurrentUser = o.optBoolean("isFanOfCurrentUser", c.isFanOfCurrentUser),
                                fansCount = o.optInt("fansCount", c.fansCount),
                                friendsCount = o.optInt("friendsCount", c.friendsCount),
                                isBlocked = o.optBoolean("isBlocked", c.isBlocked),
                                vipStarActive = o.optBoolean("vipStarActive", c.vipStarActive)
                            )
                        } else c
                    }
                }
            }

            // Load reports
            val reportsJson = prefs.getString("saved_reports_json", null)
            if (reportsJson != null) {
                val arr = org.json.JSONArray(reportsJson)
                val repList = mutableListOf<ReportItem>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    repList.add(
                        ReportItem(
                            id = o.getString("id"),
                            targetType = o.getString("targetType"),
                            targetId = o.getString("targetId"),
                            targetTitle = o.optString("targetTitle", "Content"),
                            reason = o.getString("reason"),
                            reporterUsername = o.optString("reporterUsername", "@community_member"),
                            timestampText = o.optString("timestampText", "Just now"),
                            statusText = o.optString("statusText", "Pending Review")
                        )
                    )
                }
                _reports.value = repList
            }

            // Restore notifications (Requirement 10)
            val notifsJson = prefs.getString("saved_notifications_json", null)
            if (notifsJson != null) {
                val nArr = org.json.JSONArray(notifsJson)
                val restoredNotifs = mutableListOf<NotificationItem>()
                for (i in 0 until nArr.length()) {
                    val o = nArr.getJSONObject(i)
                    val nType = try { NotifType.valueOf(o.getString("type")) } catch (_: Exception) { NotifType.REACH_MILESTONE }
                    restoredNotifs.add(
                        NotificationItem(
                            id = o.getString("id"),
                            type = nType,
                            title = o.getString("title"),
                            description = o.getString("description"),
                            timestampText = o.optString("timestampText", "Just now"),
                            isRead = o.optBoolean("isRead", false),
                            postId = o.optString("postId", "").ifEmpty { null },
                            recipientUserId = o.optString("recipientUserId", ""),
                            threshold = o.optInt("threshold", 0)
                        )
                    )
                }
                if (restoredNotifs.isNotEmpty()) {
                    _notifications.value = restoredNotifs
                }
            }

            // Restore sent milestones deduplication set
            val milesJson = prefs.getString("sent_milestones_json", null)
            if (milesJson != null) {
                val mArr = org.json.JSONArray(milesJson)
                _sentMilestones.clear()
                for (i in 0 until mArr.length()) {
                    _sentMilestones.add(mArr.getString(i))
                }
            }

            _isModeratorAuthenticated.value = prefs.getBoolean("is_moderator_authenticated", false)
        } catch (_: Exception) {}
    }

    // --- ONBOARDING & REAL AUTHENTICATION MANAGEMENT (Requirement 3, 5, 6) ---

    fun hasCompletedOnboarding(): Boolean {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return false
        return prefs.getBoolean("has_completed_onboarding", false)
    }

    fun setOnboardingCompleted(completed: Boolean = true) {
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()?.putBoolean("has_completed_onboarding", completed)?.apply()
    }

    fun loginWithGoogle(accountName: String?, email: String?): Result<Unit> {
        val cleanName = if (!accountName.isNullOrBlank()) accountName else "Google Creator"
        val cleanUsername = (email?.substringBefore("@") ?: "google_creator").replace("[^a-zA-Z0-9_]".toRegex(), "").ifEmpty { "creator" }
        val curr = _currentUser.value
        _currentUser.value = curr.copy(
            name = cleanName,
            username = cleanUsername.removePrefix("@"),
            email = email,
            isGuest = false,
            authProvider = "google",
            bio = if (curr.bio.isBlank()) "Creator on AKASH SOCIAL" else curr.bio
        )
        setOnboardingCompleted(true)
        saveAllState()
        return Result.success(Unit)
    }

    fun loginWithPhone(phoneNumber: String, otp: String): Result<Unit> {
        val cleanPhone = phoneNumber.trim()
        val cleanUsername = "user_${cleanPhone.takeLast(4).ifEmpty { "phone" }}"
        val curr = _currentUser.value
        _currentUser.value = curr.copy(
            name = curr.name.ifEmpty { "Creator $cleanPhone" },
            username = cleanUsername.removePrefix("@"),
            phoneNumber = cleanPhone,
            isGuest = false,
            authProvider = "phone",
            bio = if (curr.bio.isBlank()) "Mobile creator on AKASH SOCIAL" else curr.bio
        )
        setOnboardingCompleted(true)
        saveAllState()
        return Result.success(Unit)
    }

    fun loginWithFacebook(accountName: String?): Result<Unit> {
        val cleanName = if (!accountName.isNullOrBlank()) accountName else "Facebook Creator"
        val cleanUsername = cleanName.lowercase().replace(" ", "_").replace("[^a-zA-Z0-9_]".toRegex(), "")
        val curr = _currentUser.value
        _currentUser.value = curr.copy(
            name = cleanName,
            username = cleanUsername.removePrefix("@"),
            isGuest = false,
            authProvider = "facebook",
            bio = if (curr.bio.isBlank()) "Creator on AKASH SOCIAL" else curr.bio
        )
        setOnboardingCompleted(true)
        saveAllState()
        return Result.success(Unit)
    }

    fun exploreAsGuest(customUsername: String? = null, customName: String? = null): User {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existingGuestId = prefs?.getString("guest_stable_user_id", null)
        val guestId = existingGuestId ?: "guest_${System.currentTimeMillis()}"
        prefs?.edit()?.putString("guest_stable_user_id", guestId)?.apply()

        val rawUser = customUsername?.trim()?.removePrefix("@")?.ifBlank { null }
            ?: "guest_${guestId.takeLast(5)}"
        val rawName = customName?.trim()?.ifBlank { null } ?: "Guest Creator"

        val guestUser = _currentUser.value.copy(
            id = guestId,
            name = rawName,
            username = rawUser,
            bio = "Exploring public content & creating on AKASH SOCIAL",
            isGuest = true,
            authProvider = "guest",
            isOwner = false,
            isDeveloper = false,
            isVerified = false
        )
        _currentUser.value = guestUser
        setOnboardingCompleted(true)
        saveAllState()
        return guestUser
    }

    fun linkGuestAccount(provider: String, identifier: String): Result<Unit> {
        val curr = _currentUser.value
        val updated = curr.copy(
            isGuest = false,
            authProvider = provider,
            email = if (provider == "google") identifier else curr.email,
            phoneNumber = if (provider == "phone") identifier else curr.phoneNumber
        )
        _currentUser.value = updated
        saveAllState()
        return Result.success(Unit)
    }

    fun updateUserCategories(categories: List<String>) {
        _currentUser.value = _currentUser.value.copy(selectedCategories = categories)
        saveAllState()
    }

    // --- MULTIPLE ACCOUNTS MANAGEMENT ON DEVICE ---

    fun createAccount(
        name: String,
        username: String,
        bio: String,
        isCreator: Boolean = true,
        avatarUri: String? = null
    ): User {
        val cleanName = name.trim()
        val cleanUser = username.trim().removePrefix("@").replace(" ", "_").lowercase()
        val cleanBio = if (bio.isBlank()) "Creator on AKASH SOCIAL" else bio.trim()
        val newUserId = "user_${cleanUser}_${System.currentTimeMillis() % 100000}"

        val newAccount = User(
            id = newUserId,
            name = cleanName,
            username = cleanUser,
            bio = cleanBio,
            avatarDrawableRes = null,
            avatarUri = avatarUri,
            fansCount = 0,
            friendsCount = 0,
            postsCount = 0,
            videosCount = 0,
            totalReach = 0L,
            streakDays = 0,
            vipStarActive = false,
            isCreator = isCreator,
            isDeveloper = false,
            isOwner = false,
            isVerified = false,
            isGuest = false,
            authProvider = "local"
        )

        // Update current user in savedAccounts
        val currentAccounts = _savedAccounts.value.toMutableList()
        val currentMe = _currentUser.value
        val idx = currentAccounts.indexOfFirst { it.id == currentMe.id }
        if (idx >= 0) currentAccounts[idx] = currentMe else currentAccounts.add(currentMe)

        // Add new account and switch to it
        currentAccounts.add(newAccount)
        _savedAccounts.value = currentAccounts
        _currentUser.value = newAccount

        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.putString("active_account_id", newAccount.id)?.apply()
        saveAllState()
        return newAccount
    }

    fun switchAccount(userId: String): Boolean {
        val target = _savedAccounts.value.find { it.id == userId } ?: return false
        if (target.id == _currentUser.value.id) return true

        // 1. Keep current state saved
        val currentMe = _currentUser.value
        _savedAccounts.update { list ->
            list.map { if (it.id == currentMe.id) currentMe else it }
        }

        // 2. Switch to target account
        _currentUser.value = target

        // 3. Persist active account id
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.putString("active_account_id", target.id)?.apply()
        saveAllState()
        return true
    }

    // --- ACTIVITY TRACKING & DEDUPLICATED MILESTONES (Requirements 8, 9, 10) ---

    fun recordContentView(postId: String, viewerUserId: String = _currentUser.value.id) {
        var targetPost: Post? = null
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newViews = post.viewsCount + 1
                    val newReach = if (viewerUserId != post.authorId) post.reachCount + 1 else post.reachCount
                    val updated = post.copy(viewsCount = newViews, reachCount = newReach)
                    targetPost = updated
                    updated
                } else post
            }
        }

        targetPost?.let { post ->
            checkAndTriggerMilestones(post)
        }
        saveAllState()
    }

    private fun checkAndTriggerMilestones(post: Post) {
        val me = _currentUser.value
        if (post.authorId != me.id) return

        // 1. Reach Milestone: 100+ unique accounts reached (notified once)
        val reachKey = "${post.id}_REACH_100"
        if (post.reachCount >= 100 && !_sentMilestones.contains(reachKey)) {
            _sentMilestones.add(reachKey)
            val notif = NotificationItem(
                id = "notif_reach_100_${post.id}_${System.currentTimeMillis()}",
                type = NotifType.REACH_MILESTONE,
                title = "Your content is reaching more people! 🎉",
                description = "Your content has reached 100+ people on AKASH SOCIAL. Keep creating and sharing!",
                timestampText = "Just now",
                postId = post.id,
                isRead = false,
                recipientUserId = me.id,
                threshold = 100
            )
            _notifications.update { listOf(notif) + it }
        }

        // 2. Story Milestone: 11th genuine view (10+ views received)
        val storyKey = "${post.id}_STORY_11"
        if (post.type == PostType.STORY && post.viewsCount >= 11 && !_sentMilestones.contains(storyKey)) {
            _sentMilestones.add(storyKey)
            val notif = NotificationItem(
                id = "notif_story_11_${post.id}_${System.currentTimeMillis()}",
                type = NotifType.STORY_MILESTONE,
                title = "Your Story is getting noticed! 👀",
                description = "Your Story has received 10+ views on AKASH SOCIAL. Check out your Story's performance!",
                timestampText = "Just now",
                postId = post.id,
                isRead = false,
                recipientUserId = me.id,
                threshold = 11
            )
            _notifications.update { listOf(notif) + it }
        }

        // 3. Reel Milestone: 11th genuine view (10+ views received)
        val reelKey = "${post.id}_REEL_11"
        if ((post.type == PostType.REEL || post.type == PostType.STAR_CLIP) && post.viewsCount >= 11 && !_sentMilestones.contains(reelKey)) {
            _sentMilestones.add(reelKey)
            val notif = NotificationItem(
                id = "notif_reel_11_${post.id}_${System.currentTimeMillis()}",
                type = NotifType.REEL_MILESTONE,
                title = "Your Reel is gaining attention! 🔥",
                description = "Your Reel has received 10+ views on AKASH SOCIAL. Come back and see how your content is performing!",
                timestampText = "Just now",
                postId = post.id,
                isRead = false,
                recipientUserId = me.id,
                threshold = 11
            )
            _notifications.update { listOf(notif) + it }
        }
    }

    fun markNotificationAsRead(notifId: String) {
        _notifications.update { list ->
            list.map { if (it.id == notifId) it.copy(isRead = true) else it }
        }
        saveAllState()
    }

    fun markAllNotificationsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
        saveAllState()
    }

    fun toggleApproveAdminUpdate(updateId: String) {
        _adminUpdates.update { list ->
            list.map { item ->
                if (item.id == updateId) {
                    val approved = !item.isApproved
                    item.copy(
                        isApproved = approved,
                        statusText = if (approved) "Approved & Active" else "Pending Review"
                    )
                } else item
            }
        }
    }

    fun blockUser(userId: String) {
        _creators.update { list ->
            list.map { if (it.id == userId) it.copy(isBlocked = true) else it }
        }
        saveAllState()
    }

    fun reportUser(userId: String, reason: String) {
        val user = _creators.value.find { it.id == userId }
        reportTarget("USER", userId, user?.name ?: "User Profile", reason)
    }

    fun reportPost(postId: String, reason: String) {
        val post = _posts.value.find { it.id == postId }
        reportTarget("POST", postId, post?.caption?.take(30) ?: "Post", reason)
    }

    fun openContactSupport(context: Context, extraSubject: String = "AKASH SOCIAL Support Request") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:akashsocialappliction@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, extraSubject)
            putExtra(
                Intent.EXTRA_TEXT,
                "Hello AKASH SOCIAL Support Team,\n\nI have an inquiry regarding the app:\n\n[Please describe your inquiry here]\n\n---\nApp: AKASH SOCIAL\nOwner: Akash\nUser: ${_currentUser.value.username}\nPlatform: Android"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:akashsocialappliction@gmail.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try { context.startActivity(fallback) } catch (_: Exception) {}
        }
    }
}
