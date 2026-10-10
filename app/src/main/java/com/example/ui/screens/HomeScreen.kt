package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    currentUser: User,
    creators: List<User>,
    posts: List<Post>,
    spotlights: List<CreatorSpotlight>,
    onToggleFan: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleSave: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onCreatorClick: (String) -> Unit,
    onOpenLiveStream: (String) -> Unit,
    onStartLive: () -> Unit = { onOpenLiveStream("own_stream") },
    onRepostPost: (String) -> Unit = {},
    onOpenSpotlightModal: (SpotlightMetric?) -> Unit,
    onOpenAiHelp: () -> Unit,
    onReportPost: (String) -> Unit,
    onOpenStoryCamera: () -> Unit = {},
    onOpenReelCamera: () -> Unit = {},
    onSharePost: (String) -> Unit = {},
    onDirectMessagePost: (String, String) -> Unit = { _, _ -> },
    onShareApp: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeCommentPostId by remember { mutableStateOf<String?>(null) }
    var activeSharePost by remember { mutableStateOf<Post?>(null) }
    var shareSnackbarVisible by remember { mutableStateOf(false) }
    var repostSnackbarVisible by remember { mutableStateOf(false) }

    val activeCommentPost = posts.find { it.id == activeCommentPostId }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_feed_list"),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Horizontal Creator Story / StarClips bar
            item {
                CreatorStoryBar(
                    currentUser = currentUser,
                    creators = creators,
                    onCreatorClick = onCreatorClick,
                    onOpenLive = { onOpenLiveStream("live_aarav") },
                    onStartLive = onStartLive,
                    onOpenStoryCamera = onOpenStoryCamera,
                    onOpenReelCamera = onOpenReelCamera
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Prominent GO LIVE Quick Action Card (Requirement 19)
            item {
                GoLiveQuickActionCard(onStartLive = onStartLive)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Creator Spotlight Spotlight Hero Banner
            if (spotlights.isNotEmpty()) {
                item {
                    val featuredSpotlight = spotlights.first()
                    SpotlightHeroCard(
                        spotlight = featuredSpotlight,
                        onCreatorClick = { onCreatorClick(featuredSpotlight.creator.id) },
                        onToggleFan = { onToggleFan(featuredSpotlight.creator.id) },
                        onSeeAll = { onOpenSpotlightModal(null) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // AI Help Quick Banner
            item {
                AiHelpBanner(onClick = onOpenAiHelp)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Official APK Download & Social Friends Invite Card (Requirement 4)
            item {
                AppDownloadCard(
                    onShareApp = onShareApp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Feed Posts
            items(posts) { post ->
                PostCard(
                    post = post,
                    currentUserId = currentUser.id,
                    onToggleLike = { onToggleLike(post.id) },
                    onToggleSave = { onToggleSave(post.id) },
                    onCommentClick = { activeCommentPostId = post.id },
                    onShareClick = {
                        activeSharePost = post
                        onSharePost(post.id)
                    },
                    onRepostClick = {
                        onRepostPost(post.id)
                        repostSnackbarVisible = true
                    },
                    onCreatorClick = { onCreatorClick(post.authorId) },
                    onReport = { onReportPost(post.id) },
                    onToggleFan = { onToggleFan(post.authorId) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Interactive Share Media Bottom Sheet (Direct DM Share + System Share)
        if (activeSharePost != null) {
            ShareMediaBottomSheet(
                post = activeSharePost!!,
                friends = creators.filter { it.id != currentUser.id },
                onSendDirectMessage = { partnerId, postId ->
                    onDirectMessagePost(partnerId, postId)
                    onSharePost(postId)
                },
                onNativeShareComplete = {
                    onSharePost(activeSharePost!!.id)
                },
                onDismiss = { activeSharePost = null }
            )
        }

        // Comments Bottom Sheet
        if (activeCommentPost != null) {
            CommentsBottomSheet(
                post = activeCommentPost,
                onAddComment = { text -> onAddComment(activeCommentPost.id, text) },
                onDismiss = { activeCommentPostId = null }
            )
        }

        // Share Confirmation Banner
        if (shareSnackbarVisible) {
            Snackbar(
                action = {
                    TextButton(onClick = { shareSnackbarVisible = false }) {
                        Text("OK", color = VipGold)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Post link copied & shared to your Friends!")
            }
        }

        // Repost Confirmation Banner
        if (repostSnackbarVisible) {
            Snackbar(
                action = {
                    TextButton(onClick = { repostSnackbarVisible = false }) {
                        Text("OK", color = ReachCyanGlow)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Reposted successfully to your Friends & Fans! 🔁")
            }
        }
    }
}

@Composable
fun CreatorStoryBar(
    currentUser: User,
    creators: List<User>,
    onCreatorClick: (String) -> Unit,
    onOpenLive: () -> Unit,
    onStartLive: () -> Unit = onOpenLive,
    onOpenStoryCamera: () -> Unit = {},
    onOpenReelCamera: () -> Unit = {}
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add Story (Your Pulse + Camera trigger)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOpenStoryCamera() }
                    .testTag("story_current_user")
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF59E0B))
                            )
                        )
                        .padding(2.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        UserAvatar(user = currentUser, size = 54.dp)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(StarPrimary)
                                .border(1.5.dp, Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add Story ✨",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // New Reel Quick Action Button
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOpenReelCamera() }
                    .testTag("story_new_reel_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(StarPrimary, ReachCyanGlow)
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CosmicDarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Videocam,
                                contentDescription = "Record Reel",
                                tint = ReachCyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "REEL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "New Reel 🎬",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReachCyanGlow
                )
            }
        }

        // GO LIVE (Host button)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStartLive() }
                    .testTag("story_go_live_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(Color.Red, Color(0xFFE11D48)))
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF1E1B4B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.Podcasts,
                                contentDescription = "Go Live",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "GO LIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Go Live 🔴",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }

        // StarLive Banner Item (Watch community streams)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOpenLive() }
                    .testTag("story_live_badge")
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(Color.Red, StreakFlame))
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CosmicDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "LIVE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Red)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "StarLive",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }

        // Community Creators
        items(creators) { creator ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onCreatorClick(creator.id) }
                    .testTag("story_creator_${creator.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            brush = Brush.linearGradient(
                                if (creator.isFriend) listOf(FriendEmerald, ReachCyan)
                                else listOf(StarPrimary, VipGold)
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(user = creator, size = 54.dp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = creator.name.split(" ").first(),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun SpotlightHeroCard(
    spotlight: CreatorSpotlight,
    onCreatorClick: () -> Unit,
    onToggleFan: () -> Unit,
    onSeeAll: () -> Unit
) {
    val creator = spotlight.creator
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(listOf(VipGoldDark.copy(alpha = 0.5f), StarPrimary.copy(alpha = 0.5f)))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .testTag("spotlight_hero_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VipGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌟", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CREATOR SPOTLIGHT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = VipGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ReachCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = spotlight.metric.badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReachCyanGlow,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                TextButton(
                    onClick = onSeeAll,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Explore All →", fontSize = 11.sp, color = StarPrimaryLight)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Creator Info & Reason
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreatorClick() }
            ) {
                UserAvatar(user = creator, size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = creator.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (creator.vipStarActive) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VipStarBadge(size = 14.dp)
                        }
                    }
                    Text(
                        text = spotlight.spotlightTagline,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FanFriendButton(
                    user = creator,
                    onToggleFan = onToggleFan,
                    compact = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reason Why Featured
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "“${spotlight.reasonWhyFeatured}”",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun AiHelpBanner(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StarPrimary.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimaryLight.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onClick() }
            .testTag("ai_help_home_banner")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(StarPrimary, ReachCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI Help",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AKASH SOCIAL NAGOD AI Assistant",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ask about Fans, VIP Star, Streaks, Reach & music tools",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = StarPrimaryLight
            )
        }
    }
}

@Composable
fun GoLiveQuickActionCard(onStartLive: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1B4B),
        border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color.Red, StreakFlame))),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onStartLive() }
            .testTag("home_go_live_action_card")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Podcasts,
                        contentDescription = "Go Live",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GO LIVE",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Red.copy(alpha = 0.35f)) {
                            Text("BROADCAST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Red, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        text = "Broadcast studio session with live chat & dynamic viewer count",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Button(
                onClick = onStartLive,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("home_go_live_button")
            ) {
                Text("Start", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    currentUserId: String,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onRepostClick: () -> Unit = {},
    onCreatorClick: () -> Unit,
    onReport: () -> Unit,
    onToggleFan: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .testTag("post_card_${post.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Post Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onCreatorClick() }
                        .weight(1f)
                ) {
                    UserAvatar(
                        user = User(
                            id = post.authorId,
                            name = post.authorName,
                            username = post.authorUsername,
                            bio = "",
                            fansCount = 0,
                            friendsCount = 0,
                            postsCount = 0,
                            videosCount = 0,
                            totalReach = 0,
                            streakDays = 0,
                            vipStarActive = post.authorHasVipStar,
                            isDeveloper = post.authorHasDeveloperBadge || post.authorId == "user_akash_dev",
                            isCreator = post.authorHasCreatorBadge || post.authorId == "user_akash_dev"
                        ),
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.authorId == "user_akash_dev") {
                                Spacer(modifier = Modifier.width(4.dp))
                                SpecialPremiumBadge(size = 11.dp, showLabel = false)
                                Spacer(modifier = Modifier.width(3.dp))
                                OwnerBadge(size = 11.dp, showLabel = false)
                                Spacer(modifier = Modifier.width(3.dp))
                                CreatorBadge(size = 11.dp, showLabel = false)
                                Spacer(modifier = Modifier.width(3.dp))
                                DeveloperBadge(size = 11.dp, showLabel = false)
                            } else {
                                if (post.authorHasCreatorBadge) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    CreatorBadge(size = 11.dp, showLabel = false)
                                }
                                if (post.authorHasDeveloperBadge) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    DeveloperBadge(size = 11.dp, showLabel = false)
                                }
                            }
                            if (post.authorHasVipStar) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VipStarBadge(size = 13.dp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorUsername,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = " • ${post.timestampText}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Post Type Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (post.type) {
                            PostType.PHOTO -> StarPrimary.copy(alpha = 0.15f)
                            PostType.VIDEO -> StreakFlame.copy(alpha = 0.15f)
                            PostType.STAR_CLIP -> ReachCyan.copy(alpha = 0.15f)
                            PostType.REEL -> Color(0xFFE11D48).copy(alpha = 0.15f)
                            PostType.STORY -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when (post.type) {
                                PostType.PHOTO -> "Photo"
                                PostType.VIDEO -> "Video"
                                PostType.STAR_CLIP -> "StarClip 🎬"
                                PostType.REEL -> "Reel 🎬"
                                PostType.STORY -> "Story ✨"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (post.type) {
                                PostType.PHOTO -> StarPrimaryLight
                                PostType.VIDEO -> StreakFlameLight
                                PostType.STAR_CLIP -> ReachCyanGlow
                                PostType.REEL -> Color(0xFFFB7185)
                                PostType.STORY -> Color(0xFFC084FC)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(onClick = onReport) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Media Display
            if (post.mediaRes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CosmicDarkSurface)
                ) {
                    Image(
                        painter = painterResource(id = post.mediaRes),
                        contentDescription = "Post Artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else if (post.type == PostType.STAR_CLIP || post.type == PostType.REEL || post.type == PostType.STORY) {
                // Short-form vertical preview canvas
                val typeLabel = when (post.type) {
                    PostType.REEL -> "Reel Video 🎬 • ${if (post.videoDurationSeconds > 0) "${post.videoDurationSeconds}s" else "45s"}"
                    PostType.STORY -> "Story ✨ • ${if (post.videoDurationSeconds > 0) "${post.videoDurationSeconds}s" else "Captured Photo"}"
                    else -> "StarClip Video • ${post.videoDurationSeconds}s"
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(CosmicDarkSurface, CosmicDarkCard, StarPrimaryVariant.copy(alpha = 0.4f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(StarPrimary.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play Media",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = typeLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Audio Track Pill (if sound attached)
            if (post.audioTrack != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = "Audio track",
                            tint = ReachCyanGlow,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${post.audioTrack.title} • ${post.audioTrack.artist}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${post.audioTrack.durationSeconds}s",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Caption
            Text(
                text = post.caption,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // Hashtags
            if (post.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    post.hashtags.forEach { tag ->
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarPrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transparent Reach & Performance Bar
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = ReachCyanGlow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${post.reachCount} Reach",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ReachCyanGlow
                        )
                    }

                    Text(
                        text = "${post.viewsCount} Views",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${post.sharesCount} Shares",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${post.savesCount} Saves",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Like, Comment, Share, Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like Button
                    IconButton(
                        onClick = onToggleLike,
                        modifier = Modifier.testTag("like_post_${post.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = post.likesCount.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Comment Button
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier.testTag("comment_post_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.commentsCount.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Share Button
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.testTag("share_post_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Repost / Retweet Button
                    IconButton(
                        onClick = onRepostClick,
                        modifier = Modifier.testTag("repost_post_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Repeat,
                            contentDescription = "Repost",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.repostsCount.toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Save / Bookmark Button
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier.testTag("save_post_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) VipGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: Post,
    onAddComment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var commentInput by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("comments_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments (${post.comments.size})",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (post.comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Be the first Fan to comment!",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(post.comments) { comment ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(StarPrimary.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = comment.authorName.take(1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = comment.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (comment.hasVipStar) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VipStarBadge(size = 12.dp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = comment.timestampText,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = comment.text,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add Comment Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("Leave thoughtful creator feedback...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_comment_input"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (commentInput.isNotBlank()) {
                            onAddComment(commentInput)
                            commentInput = ""
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                    modifier = Modifier.testTag("submit_comment_button")
                ) {
                    Text("Send")
                }
            }
        }
    }
}
