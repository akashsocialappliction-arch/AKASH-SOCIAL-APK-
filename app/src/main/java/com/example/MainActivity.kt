package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CallType
import com.example.model.PostType
import com.example.ui.MainTab
import com.example.ui.StarPulseViewModel
import com.example.ui.components.StarPulseBottomBar
import com.example.ui.components.StarPulseTopBar
import com.example.ui.components.shareAppInvite
import com.example.ui.screens.*
import com.example.ui.theme.StarPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarPulseTheme {
                StarPulseApp()
            }
        }
    }
}

@Composable
fun StarPulseApp(viewModel: StarPulseViewModel = viewModel()) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val creators by viewModel.creators.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeChatMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
    val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()
    val liveStreams by viewModel.liveStreams.collectAsStateWithLifecycle()
    val adminUpdates by viewModel.adminUpdates.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val isModeratorAuthenticated by viewModel.isModeratorAuthenticated.collectAsStateWithLifecycle()
    val spotlights by viewModel.spotlights.collectAsStateWithLifecycle()

    // Secondary screen states
    val viewingCreatorId by viewModel.viewingCreatorId.collectAsStateWithLifecycle()
    val activeChatUserId by viewModel.activeChatUserId.collectAsStateWithLifecycle()
    val isViewingConversations by viewModel.isViewingConversations.collectAsStateWithLifecycle()
    val activeLiveStreamId by viewModel.activeLiveStreamId.collectAsStateWithLifecycle()
    val isViewingNotifications by viewModel.isViewingNotifications.collectAsStateWithLifecycle()
    val isViewingAdminUpdateCenter by viewModel.isViewingAdminUpdateCenter.collectAsStateWithLifecycle()
    val isViewingHelpSupport by viewModel.isViewingHelpSupport.collectAsStateWithLifecycle()
    val isViewingSpotlightModal by viewModel.isViewingSpotlightModal.collectAsStateWithLifecycle()
    val spotlightFilter by viewModel.spotlightFilter.collectAsStateWithLifecycle()
    val reportingTarget by viewModel.reportingTarget.collectAsStateWithLifecycle()

    // Camera Studio (Reels & Stories CameraX Viewfinder)
    var isCameraStudioOpen by remember { mutableStateOf(false) }
    var activeCameraMode by remember { mutableStateOf(CameraMode.STORY) }

    // Music & AI Assistant states
    val audioTracks by viewModel.audioTracks.collectAsStateWithLifecycle()
    val playingAudioId by viewModel.playingAudioId.collectAsStateWithLifecycle()
    val selectedAudioForPost by viewModel.selectedAudioForPost.collectAsStateWithLifecycle()
    val isAudioPickerOpen by viewModel.isAudioPickerOpen.collectAsStateWithLifecycle()
    val isAiHelpOpen by viewModel.isAiHelpOpen.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val aiChatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()

    val unreadNotifications = notifications.count { !it.isRead }
    val unreadMessages = conversations.sumOf { it.unreadCount }
    val hasLiveStreams = liveStreams.any { it.isLive }

    // System BackHandler
    BackHandler(
        enabled = activeCall != null ||
                activeLiveStreamId != null ||
                isCameraStudioOpen ||
                activeChatUserId != null ||
                isViewingConversations ||
                isViewingNotifications ||
                isViewingAdminUpdateCenter ||
                isViewingHelpSupport ||
                viewingCreatorId != null
    ) {
        when {
            activeCall != null -> viewModel.endCall()
            activeLiveStreamId != null -> viewModel.closeLiveStream()
            isCameraStudioOpen -> isCameraStudioOpen = false
            activeChatUserId != null -> viewModel.closeChat()
            isViewingConversations -> viewModel.closeConversationsList()
            isViewingNotifications -> viewModel.closeNotifications()
            isViewingAdminUpdateCenter -> viewModel.closeAdminUpdateCenter()
            isViewingHelpSupport -> viewModel.closeHelpSupport()
            viewingCreatorId != null -> viewModel.closeCreatorProfile()
        }
    }

    // Call Overlay takes highest priority
    if (activeCall != null) {
        CallOverlayScreen(
            call = activeCall!!,
            onAnswer = { viewModel.answerCall() },
            onEndCall = { viewModel.endCall() },
            onToggleMute = { viewModel.toggleMute() },
            onToggleSpeaker = { viewModel.toggleSpeaker() },
            onToggleCamera = { viewModel.toggleCamera() },
            onFlipCamera = { viewModel.flipCamera() }
        )
        return
    }

    // Live Stream Viewer takes second priority
    if (activeLiveStreamId != null) {
        val stream = liveStreams.find { it.id == activeLiveStreamId } ?: liveStreams.first()
        LiveStreamScreen(
            stream = stream,
            currentUser = currentUser,
            onSendComment = { text -> viewModel.sendLiveComment(stream.id, text) },
            onSendSpark = { viewModel.sendLiveSpark(stream.id) },
            onToggleFan = { viewModel.toggleFan(stream.creator.id) },
            onClose = { viewModel.closeLiveStream() }
        )
        return
    }

    // Camera Studio (Reels & Stories CameraX Viewfinder) takes third priority
    if (isCameraStudioOpen) {
        CameraCaptureScreen(
            initialMode = activeCameraMode,
            onMediaCaptured = { result ->
                isCameraStudioOpen = false
                val type = if (result.mode == CameraMode.STORY) PostType.STORY else PostType.REEL
                val defaultCaption = if (result.caption.isNotBlank()) {
                    result.caption
                } else if (result.mode == CameraMode.STORY) {
                    "Moments on AKASH SOCIAL ✨ #DailyStory"
                } else {
                    "New Reel video recorded with CameraX! 🎬 #Reels #Creator"
                }
                viewModel.createPost(
                    type = type,
                    caption = defaultCaption,
                    hashtags = if (result.mode == CameraMode.STORY) listOf("#DailyStory", "#AkashSocial") else listOf("#Reels", "#AkashSocial"),
                    mentions = emptyList(),
                    durationSeconds = result.durationSeconds
                )
            },
            onClose = { isCameraStudioOpen = false }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Hide top bar if on full chat or deep sub-screens
            if (activeChatUserId == null && !isViewingConversations && !isViewingNotifications && !isViewingAdminUpdateCenter && !isViewingHelpSupport && viewingCreatorId == null) {
                StarPulseTopBar(
                    streakDays = currentUser.streakDays,
                    unreadMessages = unreadMessages,
                    unreadNotifications = unreadNotifications,
                    hasLiveStreams = hasLiveStreams,
                    onStreakClick = { viewModel.selectTab(MainTab.TASKS) },
                    onLiveClick = {
                        val firstLive = liveStreams.firstOrNull()?.id ?: "live_aarav"
                        viewModel.openLiveStream(firstLive)
                    },
                    onNotificationsClick = { viewModel.openNotifications() },
                    onMessagesClick = { viewModel.openConversationsList() },
                    onAdminClick = { viewModel.openAdminUpdateCenter() },
                    onSupportClick = { viewModel.openHelpSupport() }
                )
            }
        },
        bottomBar = {
            // Hide bottom bar when inside chat or sub-screens
            if (activeChatUserId == null && !isViewingConversations && !isViewingNotifications && !isViewingAdminUpdateCenter && !isViewingHelpSupport && viewingCreatorId == null) {
                StarPulseBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Direct Messages (Active Chat)
                activeChatUserId != null -> {
                    val partner = creators.find { it.id == activeChatUserId } ?: creators.first()
                    val chatMessages = activeChatMessages[activeChatUserId] ?: emptyList()
                    ChatScreen(
                        currentUserId = currentUser.id,
                        partner = partner,
                        messages = chatMessages,
                        onSendMessage = { text -> viewModel.sendMessage(partner.id, text) },
                        onStartCall = { user, type -> viewModel.startCall(user, type) },
                        onBack = { viewModel.closeChat() }
                    )
                }

                // Conversations List
                isViewingConversations -> {
                    ConversationsScreen(
                        conversations = conversations,
                        onSelectConversation = { userId -> viewModel.openChatWith(userId) },
                        onBack = { viewModel.closeConversationsList() }
                    )
                }

                // Notifications Center
                isViewingNotifications -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = { viewModel.closeNotifications() }
                    )
                }

                // Admin / Owner Update & Moderator Dashboard Center
                isViewingAdminUpdateCenter -> {
                    AdminUpdateCenterScreen(
                        updates = adminUpdates,
                        users = creators,
                        currentUser = currentUser,
                        posts = posts,
                        liveStreams = liveStreams,
                        reports = reports,
                        isAuthenticated = isModeratorAuthenticated,
                        onAuthenticate = { passcode -> viewModel.authenticateModerator(passcode) },
                        onLockSession = { viewModel.lockModeratorSession() },
                        onUpdateUserStats = { userId, fans, friends, reach, streak, missions, likes, shares, comments ->
                            viewModel.updateUserStats(userId, fans, friends, reach, streak, missions, likes, shares, comments)
                        },
                        onToggleUserVipStar = { userId -> viewModel.toggleUserVipStar(userId) },
                        onToggleUserCreator = { userId -> viewModel.toggleUserCreator(userId) },
                        onToggleUserModerator = { userId -> viewModel.toggleUserModerator(userId) },
                        onToggleUserVerification = { userId -> viewModel.toggleUserVerification(userId) },
                        onToggleUserBlock = { userId -> viewModel.toggleUserBlock(userId) },
                        onResetUserData = { userId -> viewModel.resetUserData(userId) },
                        onDeletePost = { postId -> viewModel.deletePost(postId) },
                        onTogglePinPost = { postId -> viewModel.togglePinPost(postId) },
                        onUpdatePostStats = { postId, likes, comments, shares, reposts ->
                            viewModel.updatePostStats(postId, likes, comments, shares, reposts)
                        },
                        onModerateLiveChat = { streamId, action, msgId ->
                            viewModel.moderateLiveChat(streamId, action, msgId)
                        },
                        onEndLiveStream = { streamId -> viewModel.endLiveStream(streamId) },
                        onDismissReport = { repId -> viewModel.dismissReport(repId) },
                        onResolveReport = { repId, action -> viewModel.resolveReport(repId, action) },
                        onToggleApprove = { updateId -> viewModel.toggleApproveAdminUpdate(updateId) },
                        onBack = { viewModel.closeAdminUpdateCenter() }
                    )
                }

                // Help & Support Screen
                isViewingHelpSupport -> {
                    HelpSupportScreen(
                        onContactSupport = { ctx -> viewModel.openContactSupport(ctx) },
                        onOpenAiHelp = { viewModel.openAiHelp() },
                        onBack = { viewModel.closeHelpSupport() }
                    )
                }

                // Viewing Another Creator's Profile
                viewingCreatorId != null -> {
                    val targetUser = if (viewingCreatorId == currentUser.id) currentUser
                    else creators.find { it.id == viewingCreatorId } ?: currentUser

                    val authorPosts = posts.filter { it.authorId == targetUser.id }
                    ProfileScreen(
                        user = targetUser,
                        isCurrentUser = targetUser.id == currentUser.id,
                        userPosts = authorPosts,
                        onToggleFan = { userId -> viewModel.toggleFan(userId) },
                        onStartCall = { user, type -> viewModel.startCall(user, type) },
                        onOpenChat = { userId -> viewModel.openChatWith(userId) },
                        onOpenHelpSupport = { viewModel.openHelpSupport() },
                        onOpenAdminCenter = { viewModel.openAdminUpdateCenter() },
                        onOpenAiHelp = { viewModel.openAiHelp() },
                        onBlockUser = { userId -> viewModel.blockUser(userId) },
                        onReportUser = { userId -> viewModel.promptReport("USER", userId) },
                        onUpdateProfile = { name, username, bio, avatarUri ->
                            viewModel.updateUserProfile(name, username, bio, avatarUri)
                        },
                        onBack = { viewModel.closeCreatorProfile() }
                    )
                }

                // Main Navigation Tabs
                else -> {
                    when (currentTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                currentUser = currentUser,
                                creators = creators,
                                posts = posts,
                                spotlights = spotlights,
                                onToggleFan = { userId -> viewModel.toggleFan(userId) },
                                onToggleLike = { postId -> viewModel.toggleLikePost(postId) },
                                onToggleSave = { postId -> viewModel.toggleSavePost(postId) },
                                onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                                onCreatorClick = { userId -> viewModel.openCreatorProfile(userId) },
                                onOpenLiveStream = { streamId -> viewModel.openLiveStream(streamId) },
                                onOpenSpotlightModal = { metric -> viewModel.openSpotlightModal(metric) },
                                onOpenAiHelp = { viewModel.openAiHelp() },
                                onReportPost = { postId -> viewModel.promptReport("POST", postId) },
                                onOpenStoryCamera = {
                                    activeCameraMode = CameraMode.STORY
                                    isCameraStudioOpen = true
                                },
                                onOpenReelCamera = {
                                    activeCameraMode = CameraMode.REEL
                                    isCameraStudioOpen = true
                                },
                                onSharePost = { postId -> viewModel.sharePost(postId) },
                                onDirectMessagePost = { partnerId, postId ->
                                    viewModel.sendMessage(
                                        partnerId = partnerId,
                                        text = "Shared a post with you on AKASH SOCIAL 🚀",
                                        sharedPostId = postId
                                    )
                                },
                                onShareApp = {
                                    shareAppInvite(context)
                                }
                            )
                        }

                        MainTab.DISCOVER -> {
                            DiscoverScreen(
                                creators = creators,
                                posts = posts,
                                spotlights = spotlights,
                                selectedSpotlightFilter = spotlightFilter,
                                onSetSpotlightFilter = { metric -> viewModel.setSpotlightFilter(metric) },
                                onToggleFan = { userId -> viewModel.toggleFan(userId) },
                                onCreatorClick = { userId -> viewModel.openCreatorProfile(userId) },
                                onPostClick = {}
                            )
                        }

                        MainTab.CREATE -> {
                            CreatePostScreen(
                                currentUser = currentUser,
                                selectedAudio = selectedAudioForPost,
                                onOpenAudioPicker = { viewModel.openAudioPicker() },
                                onRemoveAudio = { viewModel.removeAudioFromPost() },
                                onPublish = { type, caption, tags, mentions, mediaRes, duration ->
                                    viewModel.createPost(type, caption, tags, mentions, mediaRes, duration)
                                }
                            )
                        }

                        MainTab.TASKS -> {
                            TasksScreen(
                                currentUser = currentUser,
                                tasks = tasks,
                                achievements = achievements,
                                onClaimTask = { taskId -> viewModel.claimTaskReward(taskId) },
                                onClaimAchievement = { achId -> viewModel.claimAchievementReward(achId) },
                                onClaimLaunchVipStar = { viewModel.claimLaunchVipStar() },
                                onRenewVipStar = { viewModel.renewVipStar() }
                            )
                        }

                        MainTab.PROFILE -> {
                            val myPosts = posts.filter { it.authorId == currentUser.id }
                            ProfileScreen(
                                user = currentUser,
                                isCurrentUser = true,
                                userPosts = myPosts,
                                onToggleFan = {},
                                onStartCall = { _, _ -> },
                                onOpenChat = {},
                                onOpenHelpSupport = { viewModel.openHelpSupport() },
                                onOpenAdminCenter = { viewModel.openAdminUpdateCenter() },
                                onOpenAiHelp = { viewModel.openAiHelp() },
                                onBlockUser = {},
                                onReportUser = {},
                                onUpdateProfile = { name, username, bio, avatarUri ->
                                    viewModel.updateUserProfile(name, username, bio, avatarUri)
                                },
                                onBack = null
                            )
                        }
                    }
                }
            }

            // Music / Audio Library Bottom Sheet
            if (isAudioPickerOpen) {
                MusicAudioSheet(
                    tracks = audioTracks,
                    playingTrackId = playingAudioId,
                    onPlayToggle = { trackId -> viewModel.toggleAudioPlay(trackId) },
                    onSelectTrack = { track -> viewModel.selectAudioForPost(track) },
                    onSearch = { query, category -> viewModel.searchAudio(query, category) },
                    onDismiss = { viewModel.closeAudioPicker() }
                )
            }

            // Gemini AI Support Chatbot Bottom Sheet
            if (isAiHelpOpen) {
                GeminiAiHelpSheet(
                    messages = aiChatMessages,
                    isThinking = isAiThinking,
                    onSendMessage = { text -> viewModel.sendAiQuestion(text) },
                    onDismiss = { viewModel.closeAiHelp() }
                )
            }

            // Creator Spotlight Modal
            if (isViewingSpotlightModal) {
                SpotlightDetailModal(
                    spotlights = spotlights,
                    selectedFilter = spotlightFilter,
                    onSetFilter = { metric -> viewModel.setSpotlightFilter(metric) },
                    onCreatorClick = { userId -> viewModel.openCreatorProfile(userId) },
                    onToggleFan = { userId -> viewModel.toggleFan(userId) },
                    onDismiss = { viewModel.closeSpotlightModal() }
                )
            }

            // Safety Report Dialog
            if (reportingTarget != null) {
                ReportDialog(
                    targetType = reportingTarget!!.first,
                    onConfirm = { reason -> viewModel.reportTarget(reason) },
                    onDismiss = { viewModel.dismissReport() }
                )
            }
        }
    }
}
