package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AdminUpdateCenterScreen(
    updates: List<AdminUpdateItem>,
    users: List<User>,
    currentUser: User,
    posts: List<Post>,
    liveStreams: List<LiveStream>,
    reports: List<ReportItem>,
    isAuthenticated: Boolean,
    onAuthenticate: (String) -> Boolean,
    onLockSession: () -> Unit,
    onUpdateUserStats: (String, Int?, Int?, Long?, Int?, Int?, Int?, Int?, Int?) -> Unit,
    onToggleUserVipStar: (String) -> Unit,
    onToggleUserCreator: ((String) -> Unit)? = null,
    onToggleUserModerator: ((String) -> Unit)? = null,
    onToggleUserVerification: ((String) -> Unit)? = null,
    onToggleUserBlock: (String) -> Unit,
    onResetUserData: (String) -> Unit,
    onDeletePost: (String) -> Unit,
    onTogglePinPost: (String) -> Unit,
    onUpdatePostStats: (String, Int, Int, Int, Int) -> Unit,
    onModerateLiveChat: (String, String, String?) -> Unit,
    onEndLiveStream: (String) -> Unit,
    onDismissReport: (String) -> Unit,
    onResolveReport: (String, String) -> Unit,
    onToggleApprove: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passcodeInput by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf("Users") } // Users, Growth, Posts, Live, Reports, Updates
    var editingUser by remember { mutableStateOf<User?>(null) }
    var editingPost by remember { mutableStateOf<Post?>(null) }

    val tabs = listOf(
        "Users" to "👥 Users",
        "Growth" to "📈 Growth",
        "Posts" to "🎬 Posts",
        "Live" to "🔴 Live",
        "Reports" to "🛡️ Reports",
        "Updates" to "⚙️ Updates"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_update_center_screen")
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Owner Control Panel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OwnerPremiumBadge(size = 12.dp, showLabel = false)
                    }
                    Text(
                        text = "AKASH SOCIAL • Official Owner: Akash Singh Parihar",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isAuthenticated) {
                    IconButton(
                        onClick = onLockSession,
                        modifier = Modifier.testTag("admin_lock_session_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Lock Session",
                            tint = StarPrimaryLight
                        )
                    }
                }
            }
        }

        // Security Authentication Gate
        if (!isAuthenticated) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(listOf(StarPrimary, ReachCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Official Owner Authentication",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Exclusive access for Akash Singh Parihar (Owner • Creator • Developer). Enter your secure Owner PIN or passkey to unlock the Owner Control Panel.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = passcodeInput,
                            onValueChange = {
                                passcodeInput = it
                                authError = false
                            },
                            placeholder = { Text("Enter Owner PIN (e.g. 7869)", fontSize = 13.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = authError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_pin_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        if (authError) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Access Denied: Only the authenticated Owner account with valid PIN can access this panel.",
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val success = onAuthenticate(passcodeInput)
                                if (success) {
                                    authError = false
                                } else {
                                    authError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("admin_unlock_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock Owner Control Panel", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            return
        }

        // Authenticated Dashboard Content
        // Tab Navigation Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { (key, label) ->
                val isSelected = selectedTab == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedTab = key },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StarPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("admin_tab_$key")
                )
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("admin_dashboard_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                "Users" -> {
                    item {
                        Text(
                            text = "Community Profiles & Moderation (${users.size + 1} Accounts)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Owner Profile Item
                    item {
                        UserModeratorCard(
                            user = currentUser,
                            isOwner = true,
                            onEditStats = { editingUser = currentUser },
                            onToggleVipStar = { onToggleUserVipStar(currentUser.id) },
                            onToggleBlock = {},
                            onReset = { onResetUserData(currentUser.id) }
                        )
                    }

                    // Community Users
                    items(users) { communityUser ->
                        UserModeratorCard(
                            user = communityUser,
                            isOwner = false,
                            onEditStats = { editingUser = communityUser },
                            onToggleVipStar = { onToggleUserVipStar(communityUser.id) },
                            onToggleBlock = { onToggleUserBlock(communityUser.id) },
                            onReset = { onResetUserData(communityUser.id) }
                        )
                    }
                }

                "Growth" -> {
                    item {
                        GrowthTelemetryOverviewCard(
                            currentUser = currentUser,
                            users = users,
                            posts = posts
                        )
                    }
                }

                "Posts" -> {
                    item {
                        Text(
                            text = "Platform Posts, Reels & Videos (${posts.size} Published)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    items(posts) { post ->
                        PostModeratorCard(
                            post = post,
                            onTogglePin = { onTogglePinPost(post.id) },
                            onDelete = { onDeletePost(post.id) },
                            onEditStats = { editingPost = post }
                        )
                    }
                }

                "Live" -> {
                    item {
                        LiveModeratorSection(
                            liveStreams = liveStreams,
                            onEndStream = onEndLiveStream,
                            onModerateChat = onModerateLiveChat
                        )
                    }
                }

                "Reports" -> {
                    item {
                        ReportsModeratorSection(
                            reports = reports,
                            onDismiss = onDismissReport,
                            onResolve = onResolveReport
                        )
                    }
                }

                "Updates" -> {
                    item {
                        Text(
                            text = "Continuous Improvement & Modular Updates Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    items(updates) { update ->
                        AdminUpdateCard(
                            item = update,
                            onToggleApprove = { onToggleApprove(update.id) }
                        )
                    }
                }
            }
        }
    }

    // Edit User Stats Dialog
    if (editingUser != null) {
        EditUserStatsDialog(
            user = editingUser!!,
            onSave = { fans, friends, reach, streak, missions, likes, shares, comments ->
                onUpdateUserStats(editingUser!!.id, fans, friends, reach, streak, missions, likes, shares, comments)
                editingUser = null
            },
            onToggleVip = {
                onToggleUserVipStar(editingUser!!.id)
                editingUser = null
            },
            onToggleVerified = {
                onToggleUserVerification?.invoke(editingUser!!.id)
                editingUser = null
            },
            onToggleCreator = {
                onToggleUserCreator?.invoke(editingUser!!.id)
                editingUser = null
            },
            onToggleModerator = {
                onToggleUserModerator?.invoke(editingUser!!.id)
                editingUser = null
            },
            onDismiss = { editingUser = null }
        )
    }

    // Edit Post Stats Dialog
    if (editingPost != null) {
        EditPostStatsDialog(
            post = editingPost!!,
            onSave = { likes, comments, shares, reposts ->
                onUpdatePostStats(editingPost!!.id, likes, comments, shares, reposts)
                editingPost = null
            },
            onDismiss = { editingPost = null }
        )
    }
}

@Composable
fun UserModeratorCard(
    user: User,
    isOwner: Boolean,
    onEditStats: () -> Unit,
    onToggleVipStar: () -> Unit,
    onToggleBlock: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOwner) StarPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("moderator_user_${user.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(user = user, size = 44.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isOwner) {
                            Spacer(modifier = Modifier.width(4.dp))
                            SpecialPremiumBadge(size = 11.dp, showLabel = false)
                            Spacer(modifier = Modifier.width(4.dp))
                            OwnerBadge(size = 11.dp, showLabel = false)
                            Spacer(modifier = Modifier.width(4.dp))
                            CreatorBadge(size = 11.dp, showLabel = false)
                            Spacer(modifier = Modifier.width(4.dp))
                            DeveloperBadge(size = 11.dp, showLabel = false)
                        } else {
                            if (user.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VerifiedBadge(size = 11.dp)
                            }
                            if (user.isCreator) {
                                Spacer(modifier = Modifier.width(4.dp))
                                CreatorBadge(size = 11.dp, showLabel = false)
                            }
                            if (user.isModerator) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = ReachCyan.copy(alpha = 0.2f)) {
                                    Text("MOD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ReachCyanGlow, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            if (user.vipStarActive) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VipStarBadge(size = 12.dp)
                            }
                        }
                    }
                    Text(
                        text = "${user.username} • Fans: ${user.fansCount} • Friends: ${user.friendsCount} • Reach: ${user.totalReach}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (user.isBlocked) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Red.copy(alpha = 0.15f)
                    ) {
                        Text("BLOCKED", fontSize = 9.sp, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onEditStats,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tuning", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onToggleVipStar,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (user.vipStarActive) "Revoke Star" else "Grant Star", fontSize = 11.sp)
                }

                if (!isOwner) {
                    OutlinedButton(
                        onClick = onToggleBlock,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (user.isBlocked) FriendEmerald else Color.Red),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (user.isBlocked) "Unblock" else "Block", fontSize = 11.sp)
                    }
                }

                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Zero", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun GrowthTelemetryOverviewCard(
    currentUser: User,
    users: List<User>,
    posts: List<Post>
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimaryLight.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Natural Organic Growth Telemetry",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "All accounts start genuinely at zero. Values grow gradually through authentic community usage and creation.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TelemetryMetricBox(label = "Platform Reach", value = "${currentUser.totalReach + users.sumOf { it.totalReach }}", color = ReachCyanGlow, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                TelemetryMetricBox(label = "Published Posts", value = "${posts.size}", color = StarPrimaryLight, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                TelemetryMetricBox(label = "Active Streak", value = "${currentUser.streakDays}d", color = StreakFlame, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TelemetryMetricBox(label = "Your Fans", value = "${currentUser.fansCount}", color = StarPrimaryLight, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                TelemetryMetricBox(label = "Your Friends", value = "${currentUser.friendsCount}", color = FriendEmerald, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                TelemetryMetricBox(label = "Missions Done", value = "${currentUser.completedTasksCount}", color = VipGold, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TelemetryMetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PostModeratorCard(
    post: Post,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onEditStats: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${post.authorName} (${post.authorUsername})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (post.isPinned) {
                    Surface(shape = RoundedCornerShape(6.dp), color = VipGold.copy(alpha = 0.2f)) {
                        Text("PINNED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VipGold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.caption,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "❤️ ${post.likesCount} • 💬 ${post.commentsCount} • 🔁 ${post.sharesCount} • 🚀 ${post.reachCount} Reach",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ReachCyanGlow
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onTogglePin,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (post.isPinned) "Unpin" else "Pin to Top", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onEditStats,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tuning Stats", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun LiveModeratorSection(
    liveStreams: List<LiveStream>,
    onEndStream: (String) -> Unit,
    onModerateChat: (String, String, String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Live Broadcast Telemetry & Chat Moderation",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (liveStreams.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No active live broadcasts at this moment. Tap 'GO LIVE' to start a session.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            liveStreams.forEach { stream ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stream.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("👁️ ${stream.viewerCount} Viewers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ReachCyanGlow)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Host: ${stream.creator.name} (${stream.creator.username}) • Messages: ${stream.chatMessages.size}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onModerateChat(stream.id, "CLEAR_ALL", null) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clear Chat", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onModerateChat(stream.id, "TOGGLE_MUTE", null) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (stream.isChatMuted) "Unmute Chat" else "Mute Chat", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onEndStream(stream.id) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("End Stream", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportsModeratorSection(
    reports: List<ReportItem>,
    onDismiss: (String) -> Unit,
    onResolve: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Community Safety & Reports Queue (${reports.size} Reports)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (reports.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No pending reports! Community environment is healthy and respectful.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            reports.forEach { report ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(shape = RoundedCornerShape(6.dp), color = Color.Red.copy(alpha = 0.15f)) {
                                Text(report.targetType, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Red, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Text(report.timestampText, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Target: ${report.targetTitle} (ID: ${report.targetId})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Reason: ${report.reason}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Status: ${report.statusText}", fontSize = 11.sp, color = StarPrimaryLight)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onDismiss(report.id) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Dismiss", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onResolve(report.id, "Action Taken") },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FriendEmerald),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Resolve", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditUserStatsDialog(
    user: User,
    onSave: (Int?, Int?, Long?, Int?, Int?, Int?, Int?, Int?) -> Unit,
    onToggleVip: () -> Unit,
    onToggleVerified: () -> Unit,
    onToggleCreator: () -> Unit,
    onToggleModerator: () -> Unit,
    onDismiss: () -> Unit
) {
    var fans by remember { mutableStateOf(user.fansCount.toString()) }
    var friends by remember { mutableStateOf(user.friendsCount.toString()) }
    var reach by remember { mutableStateOf(user.totalReach.toString()) }
    var streak by remember { mutableStateOf(user.streakDays.toString()) }
    var missions by remember { mutableStateOf(user.completedTasksCount.toString()) }
    var likes by remember { mutableStateOf(user.likesCount.toString()) }
    var shares by remember { mutableStateOf(user.sharesCount.toString()) }
    var comments by remember { mutableStateOf(user.commentsCount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tune Metrics & Badges for ${user.name}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Numerical Metrics", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StarPrimaryLight)
                OutlinedTextField(value = fans, onValueChange = { fans = it }, label = { Text("Fans") }, singleLine = true)
                OutlinedTextField(value = friends, onValueChange = { friends = it }, label = { Text("Friends") }, singleLine = true)
                OutlinedTextField(value = reach, onValueChange = { reach = it }, label = { Text("Total Reach") }, singleLine = true)
                OutlinedTextField(value = streak, onValueChange = { streak = it }, label = { Text("Streak Days") }, singleLine = true)
                OutlinedTextField(value = missions, onValueChange = { missions = it }, label = { Text("Completed Missions") }, singleLine = true)
                OutlinedTextField(value = likes, onValueChange = { likes = it }, label = { Text("Likes Count") }, singleLine = true)
                OutlinedTextField(value = shares, onValueChange = { shares = it }, label = { Text("Shares Count") }, singleLine = true)
                OutlinedTextField(value = comments, onValueChange = { comments = it }, label = { Text("Comments Count") }, singleLine = true)

                Spacer(modifier = Modifier.height(4.dp))
                Text("Badges & Permissions", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VipGold)

                OutlinedButton(
                    onClick = onToggleVip,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (user.vipStarActive) "Revoke VIP Star Badge ⭐" else "Grant VIP Star Badge ⭐", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onToggleVerified,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (user.isVerified) "Remove Verification Badge ✓" else "Grant Verification Badge ✓", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onToggleCreator,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (user.isCreator) "Toggle Creator: Active ✨" else "Toggle Creator: Standard", fontSize = 12.sp)
                }

                if (!user.isOwner) {
                    OutlinedButton(
                        onClick = onToggleModerator,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (user.isModerator) "Revoke Moderator Permissions 🛡️" else "Grant Moderator Permissions 🛡️", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    fans.toIntOrNull(),
                    friends.toIntOrNull(),
                    reach.toLongOrNull(),
                    streak.toIntOrNull(),
                    missions.toIntOrNull(),
                    likes.toIntOrNull(),
                    shares.toIntOrNull(),
                    comments.toIntOrNull()
                )
            }) {
                Text("Save Tuning")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditPostStatsDialog(
    post: Post,
    onSave: (Int, Int, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var likes by remember { mutableStateOf(post.likesCount.toString()) }
    var comments by remember { mutableStateOf(post.commentsCount.toString()) }
    var shares by remember { mutableStateOf(post.sharesCount.toString()) }
    var reposts by remember { mutableStateOf(post.repostsCount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tune Post Engagement", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = likes, onValueChange = { likes = it }, label = { Text("Likes") }, singleLine = true)
                OutlinedTextField(value = comments, onValueChange = { comments = it }, label = { Text("Comments") }, singleLine = true)
                OutlinedTextField(value = shares, onValueChange = { shares = it }, label = { Text("Shares") }, singleLine = true)
                OutlinedTextField(value = reposts, onValueChange = { reposts = it }, label = { Text("Reposts") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    likes.toIntOrNull() ?: post.likesCount,
                    comments.toIntOrNull() ?: post.commentsCount,
                    shares.toIntOrNull() ?: post.sharesCount,
                    reposts.toIntOrNull() ?: post.repostsCount
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminUpdateCard(
    item: AdminUpdateItem,
    onToggleApprove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isApproved) FriendEmerald.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_update_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (item.category) {
                        "Suggested Improvement" -> StarPrimary.copy(alpha = 0.15f)
                        "Feature Idea" -> ReachCyan.copy(alpha = 0.15f)
                        "Bug Report" -> Color.Red.copy(alpha = 0.15f)
                        else -> VipGold.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = item.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.category) {
                            "Suggested Improvement" -> StarPrimaryLight
                            "Feature Idea" -> ReachCyanGlow
                            "Bug Report" -> Color.Red
                            else -> VipGold
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                if (item.isMajorChange) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VipGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "MAJOR CHANGE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = VipGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Impact Summary
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Impact Summary: ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.impactSummary,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (item.isApproved) FriendEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onToggleApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isApproved) FriendEmerald else StarPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("toggle_update_button_${item.id}")
                ) {
                    Text(
                        text = if (item.isApproved) "Approved ✓" else "Review & Approve",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
