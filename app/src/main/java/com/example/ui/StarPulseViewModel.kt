package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StarPulseRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    DISCOVER,
    CREATE,
    TASKS,
    PROFILE
}

class StarPulseViewModel(
    val repository: StarPulseRepository = StarPulseRepository()
) : ViewModel() {

    // Shimmer / Skeleton Loading States for Feed and Profile
    private val _isFeedLoading = MutableStateFlow(true)
    val isFeedLoading: StateFlow<Boolean> = _isFeedLoading.asStateFlow()

    private val _isProfileLoading = MutableStateFlow(false)
    val isProfileLoading: StateFlow<Boolean> = _isProfileLoading.asStateFlow()

    // Multiple Accounts Management on this device
    val savedAccounts: StateFlow<List<User>> = repository.savedAccounts

    private val _isAccountSwitcherOpen = MutableStateFlow(false)
    val isAccountSwitcherOpen: StateFlow<Boolean> = _isAccountSwitcherOpen.asStateFlow()

    private val _isCreateAccountOpen = MutableStateFlow(false)
    val isCreateAccountOpen: StateFlow<Boolean> = _isCreateAccountOpen.asStateFlow()

    init {
        // Initial simulated network fetch with smooth perceived skeleton shimmer
        viewModelScope.launch {
            delay(650)
            _isFeedLoading.value = false
        }
    }

    // Main navigation tab
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Secondary screen states
    private val _viewingCreatorId = MutableStateFlow<String?>(null)
    val viewingCreatorId: StateFlow<String?> = _viewingCreatorId.asStateFlow()

    private val _activeChatUserId = MutableStateFlow<String?>(null)
    val activeChatUserId: StateFlow<String?> = _activeChatUserId.asStateFlow()

    private val _isViewingConversations = MutableStateFlow(false)
    val isViewingConversations: StateFlow<Boolean> = _isViewingConversations.asStateFlow()

    private val _activeLiveStreamId = MutableStateFlow<String?>(null)
    val activeLiveStreamId: StateFlow<String?> = _activeLiveStreamId.asStateFlow()

    private val _isViewingNotifications = MutableStateFlow(false)
    val isViewingNotifications: StateFlow<Boolean> = _isViewingNotifications.asStateFlow()

    private val _isViewingAdminUpdateCenter = MutableStateFlow(false)
    val isViewingAdminUpdateCenter: StateFlow<Boolean> = _isViewingAdminUpdateCenter.asStateFlow()

    private val _isViewingHelpSupport = MutableStateFlow(false)
    val isViewingHelpSupport: StateFlow<Boolean> = _isViewingHelpSupport.asStateFlow()

    private val _isViewingSpotlightModal = MutableStateFlow(false)
    val isViewingSpotlightModal: StateFlow<Boolean> = _isViewingSpotlightModal.asStateFlow()

    private val _spotlightFilter = MutableStateFlow<SpotlightMetric?>(null)
    val spotlightFilter: StateFlow<SpotlightMetric?> = _spotlightFilter.asStateFlow()

    // Safety and Reporting
    private val _reportingTarget = MutableStateFlow<Pair<String, String>?>(null) // (Type: "USER" or "POST", id)
    val reportingTarget: StateFlow<Pair<String, String>?> = _reportingTarget.asStateFlow()

    // Onboarding & Flow Control (Requirement 3, 5, 6)
    private val _isOnboardingActive = MutableStateFlow(!repository.hasCompletedOnboarding())
    val isOnboardingActive: StateFlow<Boolean> = _isOnboardingActive.asStateFlow()

    private val _isViewingCategories = MutableStateFlow(false)
    val isViewingCategories: StateFlow<Boolean> = _isViewingCategories.asStateFlow()

    // Home feed view mode: Social Feed vs Vertical Reels
    private val _isReelsMode = MutableStateFlow(false)
    val isReelsMode: StateFlow<Boolean> = _isReelsMode.asStateFlow()

    // --- Feature 1: Music & Audio Library ---
    val musicRepository = com.example.data.MusicAudioRepository()
    val audioTracks = musicRepository.tracks
    val playingAudioId = musicRepository.playingTrackId

    private val _selectedAudioForPost = MutableStateFlow<AudioTrack?>(null)
    val selectedAudioForPost: StateFlow<AudioTrack?> = _selectedAudioForPost.asStateFlow()

    private val _isAudioPickerOpen = MutableStateFlow(false)
    val isAudioPickerOpen: StateFlow<Boolean> = _isAudioPickerOpen.asStateFlow()

    // --- Feature 2: Gemini AI Support Assistant ---
    val geminiService = com.example.ai.GeminiAiService()

    private val _isAiHelpOpen = MutableStateFlow(false)
    val isAiHelpOpen: StateFlow<Boolean> = _isAiHelpOpen.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiChatMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                id = "ai_welcome",
                text = "✨ Hello! I'm your **AKASH SOCIAL NAGOD AI Assistant**.\n\nI can help you understand:\n• **Fans & Friends** system\n• Earning & renewing the **VIP Star**\n• Keeping your **Activity Streaks** ablaze\n• Hitting the **1,000+ Reach** milestone\n• Music licensing & creator audio tools\n\nHow can I help you level up your creator journey today?",
                isUser = false,
                timestampText = "Just now"
            )
        )
    )
    val aiChatMessages: StateFlow<List<AiChatMessage>> = _aiChatMessages.asStateFlow()

    // Expose repository state flows
    val currentUser = repository.currentUser
    val creators = repository.creators
    val posts = repository.posts
    val tasks = repository.tasks
    val achievements = repository.achievements
    val notifications = repository.notifications
    val conversations = repository.conversations
    val activeChatMessages = repository.activeChatMessages
    val activeCall = repository.activeCall
    val liveStreams = repository.liveStreams
    val adminUpdates = repository.adminUpdates
    val spotlights = repository.spotlights
    val reports = repository.reports
    val isModeratorAuthenticated = repository.isModeratorAuthenticated

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun openCreatorProfile(userId: String) {
        _viewingCreatorId.value = userId
    }

    fun closeCreatorProfile() {
        _viewingCreatorId.value = null
    }

    fun openConversationsList() {
        _isViewingConversations.value = true
    }

    fun closeConversationsList() {
        _isViewingConversations.value = false
    }

    fun openChatWith(userId: String) {
        _activeChatUserId.value = userId
        _isViewingConversations.value = true
    }

    fun closeChat() {
        _activeChatUserId.value = null
    }

    fun openLiveStream(streamId: String) {
        _activeLiveStreamId.value = streamId
    }

    fun closeLiveStream() {
        _activeLiveStreamId.value = null
    }

    fun openNotifications() {
        _isViewingNotifications.value = true
    }

    fun closeNotifications() {
        _isViewingNotifications.value = false
    }

    fun openAdminUpdateCenter() {
        _isViewingAdminUpdateCenter.value = true
    }

    fun closeAdminUpdateCenter() {
        _isViewingAdminUpdateCenter.value = false
    }

    fun openHelpSupport() {
        _isViewingHelpSupport.value = true
    }

    fun closeHelpSupport() {
        _isViewingHelpSupport.value = false
    }

    fun openSpotlightModal(metric: SpotlightMetric? = null) {
        _spotlightFilter.value = metric
        _isViewingSpotlightModal.value = true
    }

    fun closeSpotlightModal() {
        _isViewingSpotlightModal.value = false
    }

    fun setSpotlightFilter(metric: SpotlightMetric?) {
        _spotlightFilter.value = metric
    }

    fun promptReport(type: String, id: String) {
        _reportingTarget.value = type to id
    }

    fun dismissReport() {
        _reportingTarget.value = null
    }

    // Repository delegates
    fun toggleFan(userId: String) = repository.toggleFan(userId)
    fun toggleLikePost(postId: String) = repository.toggleLikePost(postId)
    fun toggleSavePost(postId: String) = repository.toggleSavePost(postId)
    fun addComment(postId: String, text: String) = repository.addComment(postId, text)
    fun createPost(
        type: PostType,
        caption: String,
        hashtags: List<String>,
        mentions: List<String>,
        mediaRes: Int? = null,
        durationSeconds: Int = 0
    ) {
        val audio = _selectedAudioForPost.value
        repository.createPost(type, caption, hashtags, mentions, mediaRes, durationSeconds, audio)
        _selectedAudioForPost.value = null
        _currentTab.value = MainTab.HOME
    }

    fun claimTaskReward(taskId: String) = repository.claimTaskReward(taskId)
    fun claimAchievementReward(achId: String) = repository.claimAchievementReward(achId)
    fun claimLaunchVipStar() = repository.claimLaunchVipStar()
    fun renewVipStar() = repository.renewVipStar()

    fun sendMessage(partnerId: String, text: String, sharedPostId: String? = null) =
        repository.sendMessage(partnerId, text, sharedPostId)

    fun startCall(partner: User, type: CallType) = repository.startCall(partner, type)
    fun answerCall() = repository.answerCall()
    fun endCall() = repository.endCall()
    fun toggleMute() = repository.toggleMute()
    fun toggleSpeaker() = repository.toggleSpeaker()
    fun toggleCamera() = repository.toggleCamera()
    fun flipCamera() = repository.flipCamera()

    fun sendLiveComment(streamId: String, text: String) = repository.sendLiveComment(streamId, text)
    fun sendLiveSpark(streamId: String) = repository.sendLiveSpark(streamId)
    fun startOwnLiveStream(title: String) {
        val stream = repository.startOwnLiveStream(title)
        _activeLiveStreamId.value = stream.id
    }
    fun endLiveStream(streamId: String) {
        repository.endLiveStream(streamId)
        if (_activeLiveStreamId.value == streamId) {
            _activeLiveStreamId.value = null
        }
    }
    fun joinLiveStream(streamId: String) = repository.joinLiveStream(streamId)
    fun leaveLiveStream(streamId: String) = repository.leaveLiveStream(streamId)
    fun deleteLiveMessage(streamId: String, messageId: String) = repository.deleteLiveMessage(streamId, messageId)
    fun setLivePinnedAnnouncement(streamId: String, announcement: String?) = repository.setLivePinnedAnnouncement(streamId, announcement)
    fun toggleLiveChatMute(streamId: String) = repository.toggleLiveChatMute(streamId)

    // Moderator & Owner Dashboard Controls & Security
    fun authenticateModerator(passcode: String): Boolean = repository.authenticateModerator(passcode)
    fun lockModeratorSession() = repository.lockModeratorSession()
    fun updateUserStats(
        userId: String,
        fans: Int?,
        friends: Int?,
        reach: Long?,
        streak: Int?,
        missions: Int?,
        likes: Int? = null,
        shares: Int? = null,
        comments: Int? = null
    ) = repository.updateUserStats(userId, fans, friends, reach, streak, missions, likes, shares, comments)
    fun toggleUserVipStar(userId: String) = repository.toggleUserVipStar(userId)
    fun toggleUserCreator(userId: String) = repository.toggleUserCreator(userId)
    fun toggleUserModerator(userId: String) = repository.toggleUserModerator(userId)
    fun toggleUserVerification(userId: String) = repository.toggleUserVerification(userId)
    fun updateUserProfile(name: String, username: String, bio: String, avatarUri: String? = null) =
        repository.updateUserProfile(name, username, bio, avatarUri)
    fun toggleUserBlock(userId: String) = repository.toggleUserBlock(userId)
    fun resetUserData(userId: String) = repository.resetUserData(userId)
    fun deletePost(postId: String) = repository.deletePost(postId)
    fun togglePinPost(postId: String) = repository.togglePinPost(postId)
    fun deleteComment(postId: String, commentId: String) = repository.deleteComment(postId, commentId)
    fun updatePostStats(postId: String, likes: Int, comments: Int, shares: Int, reposts: Int) =
        repository.updatePostStats(postId, likes, comments, shares, reposts)
    fun moderateLiveChat(streamId: String, action: String, messageId: String? = null) =
        repository.moderateLiveChat(streamId, action, messageId)
    fun dismissReport(reportId: String) = repository.dismissReport(reportId)
    fun resolveReport(reportId: String, actionTaken: String) = repository.resolveReport(reportId, actionTaken)
    fun repostPost(postId: String) = repository.repostPost(postId)
    fun sharePost(postId: String) = repository.sharePost(postId)

    fun toggleApproveAdminUpdate(updateId: String) = repository.toggleApproveAdminUpdate(updateId)
    fun blockUser(userId: String) = repository.blockUser(userId)
    fun reportTarget(reason: String) {
        val target = _reportingTarget.value ?: return
        if (target.first == "USER") {
            repository.reportUser(target.second, reason)
        } else {
            repository.reportPost(target.second, reason)
        }
        _reportingTarget.value = null
    }

    fun openContactSupport(context: Context) = repository.openContactSupport(context)

    // Music & Audio Actions
    fun openAudioPicker() {
        _isAudioPickerOpen.value = true
    }

    fun closeAudioPicker() {
        _isAudioPickerOpen.value = false
        musicRepository.stopAudio()
    }

    fun selectAudioForPost(track: AudioTrack) {
        _selectedAudioForPost.value = track
        _isAudioPickerOpen.value = false
        musicRepository.stopAudio()
    }

    fun removeAudioFromPost() {
        _selectedAudioForPost.value = null
    }

    fun toggleAudioPlay(trackId: String) {
        musicRepository.togglePlay(trackId)
    }

    fun searchAudio(query: String, category: String? = null) {
        musicRepository.filterTracks(query, category)
    }

    // Gemini AI Assistant Actions
    fun openAiHelp() {
        _isAiHelpOpen.value = true
    }

    fun closeAiHelp() {
        _isAiHelpOpen.value = false
    }

    fun sendAiQuestion(question: String) {
        if (question.isBlank()) return
        val userMsg = AiChatMessage(
            id = "user_${System.currentTimeMillis()}",
            text = question.trim(),
            isUser = true,
            timestampText = "Just now"
        )
        _aiChatMessages.value = _aiChatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val answer = geminiService.askAssistant(question)
            _isAiThinking.value = false
            val aiMsg = AiChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                text = answer,
                isUser = false,
                timestampText = "Just now"
            )
            _aiChatMessages.value = _aiChatMessages.value + aiMsg
        }
    }

    // --- Onboarding & Auth Actions (Requirement 3, 5, 6) ---

    fun completeOnboarding() {
        repository.setOnboardingCompleted(true)
        _isOnboardingActive.value = false
    }

    fun loginWithGoogle(accountName: String?, email: String?) {
        repository.loginWithGoogle(accountName, email)
    }

    fun loginWithPhone(phoneNumber: String, otp: String) {
        repository.loginWithPhone(phoneNumber, otp)
    }

    fun loginWithFacebook(accountName: String?) {
        repository.loginWithFacebook(accountName)
    }

    fun exploreAsGuest(customUsername: String? = null, customName: String? = null) {
        repository.exploreAsGuest(customUsername, customName)
    }

    fun linkGuestAccount(provider: String, identifier: String) {
        repository.linkGuestAccount(provider, identifier)
    }

    fun openCategoriesScreen() {
        _isViewingCategories.value = true
    }

    fun closeCategoriesScreen() {
        _isViewingCategories.value = false
    }

    fun saveCategories(categories: List<String>) {
        repository.updateUserCategories(categories)
        _isViewingCategories.value = false
    }

    fun setReelsMode(isReels: Boolean) {
        _isReelsMode.value = isReels
    }

    // Activity tracking & milestone checks (Requirements 8, 9, 10)
    fun recordContentView(postId: String) {
        repository.recordContentView(postId)
    }

    fun markNotificationAsRead(notifId: String) {
        repository.markNotificationAsRead(notifId)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
    }

    // --- Account Switching & Creation ---
    fun openAccountSwitcher() {
        _isAccountSwitcherOpen.value = true
    }

    fun closeAccountSwitcher() {
        _isAccountSwitcherOpen.value = false
    }

    fun openCreateAccount() {
        _isCreateAccountOpen.value = true
    }

    fun closeCreateAccount() {
        _isCreateAccountOpen.value = false
    }

    fun switchAccount(userId: String) {
        viewModelScope.launch {
            _isProfileLoading.value = true
            repository.switchAccount(userId)
            delay(500)
            _isProfileLoading.value = false
        }
    }

    fun createNewAccount(name: String, username: String, bio: String, isCreator: Boolean) {
        viewModelScope.launch {
            _isProfileLoading.value = true
            repository.createAccount(name, username, bio, isCreator)
            delay(500)
            _isProfileLoading.value = false
        }
    }

    // --- Shimmer Refresh Triggers ---
    fun refreshFeed() {
        viewModelScope.launch {
            _isFeedLoading.value = true
            delay(750)
            _isFeedLoading.value = false
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            _isProfileLoading.value = true
            delay(600)
            _isProfileLoading.value = false
        }
    }
}
