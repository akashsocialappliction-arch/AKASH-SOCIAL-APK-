package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    isCurrentUser: Boolean,
    userPosts: List<Post>,
    onToggleFan: (String) -> Unit,
    onStartCall: (User, CallType) -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenHelpSupport: () -> Unit,
    onOpenAdminCenter: () -> Unit,
    onOpenAiHelp: () -> Unit,
    onBlockUser: (String) -> Unit,
    onReportUser: (String) -> Unit,
    onUpdateProfile: (String, String, String, String?) -> Result<Unit> = { _, _, _, _ -> Result.success(Unit) },
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Posts") } // "Posts", "Clips", "Analytics"
    var isEditingProfile by remember { mutableStateOf(false) }

    if (isEditingProfile) {
        EditProfileDialog(
            user = user,
            onSave = onUpdateProfile,
            onDismiss = { isEditingProfile = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button if secondary screen
        if (onBack != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = user.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (!isCurrentUser) {
                        IconButton(onClick = { onReportUser(user.id) }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                    }
                }
            }
        }

        // Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Avatar & Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        UserAvatar(user = user, size = 68.dp)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(start = 8.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            if (user.isOwner || user.id == "user_akash_dev") {
                                SpecialPremiumBadge(showLabel = true)
                                OwnerBadge(showLabel = true)
                                CreatorBadge(showLabel = true)
                                DeveloperBadge(showLabel = true)
                            } else {
                                if (user.isCreator) {
                                    CreatorBadge(showLabel = true)
                                }
                                if (user.isDeveloper) {
                                    DeveloperBadge(showLabel = true)
                                }
                            }
                            StreakChip(streakDays = user.streakDays)
                            if (user.vipStarActive) {
                                VipStarBadge(size = 18.dp, showLabel = true)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name and Username
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (user.isOwner || user.id == "user_akash_dev") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("👑", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge(size = 16.dp)
                        } else if (user.isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedBadge(size = 16.dp)
                        } else if (user.vipStarActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🌟", fontSize = 16.sp)
                        }
                    }
                    Text(
                        text = user.username,
                        fontSize = 13.sp,
                        color = StarPrimaryLight,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bio
                    Text(
                        text = user.bio,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    // Special Creator & Owner Identity Card (Akash Singh Parihar)
                    if (user.isOwner || user.id == "user_akash_dev") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CosmicDarkBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Brush.linearGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFF8B5CF6)))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owner_creator_identity_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PremiumStarBadgeImage(size = 32.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Official Platform Owner",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFDE047)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        VerifiedBadge(size = 13.dp)
                                    }
                                    Text(
                                        text = "Owner • Creator • Developer of AKASH SOCIAL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // PRIMARY STATS: Fans | Friends | Posts | Reach
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // FANS STAT
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = user.fansCount.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = StarPrimaryLight
                                )
                                Text(
                                    text = "Fans",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )

                            // FRIENDS STAT
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = user.friendsCount.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = FriendEmerald
                                )
                                Text(
                                    text = "Friends 🤝",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )

                            // POSTS STAT
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = user.postsCount.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Posts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.outline)
                            )

                            // TOTAL REACH STAT
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${user.totalReach}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = ReachCyanGlow
                                )
                                Text(
                                    text = "Total Reach",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Profile Actions
                    if (isCurrentUser) {
                        // Current User Options
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { isEditingProfile = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_edit_button")
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Profile", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = onOpenAiHelp,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_ai_help_button")
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Help", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = onOpenHelpSupport,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_help_support_button")
                                ) {
                                    Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Support", fontSize = 12.sp)
                                }
                            }

                            if (user.isOwner || user.id == "user_akash_dev") {
                                Button(
                                    onClick = onOpenAdminCenter,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD97706)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_admin_center_button")
                                ) {
                                    Icon(Icons.Filled.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Secure Owner Control Panel 👑", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    } else {
                        // Other Creator Profile Actions: "Be a Fan", Message, Voice/Video Call
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FanFriendButton(
                                user = user,
                                onToggleFan = { onToggleFan(user.id) },
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = { onOpenChat(user.id) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.testTag("message_user_${user.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChatBubbleOutline,
                                    contentDescription = "Message",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onStartCall(user, CallType.VOICE) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .testTag("call_voice_${user.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Voice Call",
                                    tint = ReachCyanGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onStartCall(user, CallType.VIDEO) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .testTag("call_video_${user.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Videocam,
                                    contentDescription = "Video Call",
                                    tint = StreakFlameLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Creator Analytics Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creator_analytics_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📊 Creator Activity & Growth",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.streakDays} Days", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = StreakFlameLight)
                        }
                        Column {
                            Text("Missions Done", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.completedTasksCount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = VipGold)
                        }
                        Column {
                            Text("Top Reach", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.topTrendingPostReach}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ReachCyanGlow)
                        }
                        Column {
                            Text("Engagement", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(user.engagementRate, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FriendEmerald)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Likes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.likesCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text("Comments", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.commentsCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text("Shares", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.sharesCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text("Reposts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.repostsCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        // Content Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                listOf("Posts", "Clips", "Analytics").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StarPrimary else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Published Items
        if (userPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✨", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No content published yet",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(userPosts) { post ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = post.type.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarPrimaryLight
                            )
                            ReachBadge(reach = post.reachCount)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = post.caption,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "❤️ ${post.likesCount} Likes • 💬 ${post.commentsCount} Comments • ${post.timestampText}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onSave: (String, String, String, String?) -> Result<Unit>,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.bio) }
    var avatarUri by remember { mutableStateOf(user.avatarUri) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            avatarUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile Picture Preview & Change
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (!avatarUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        UserAvatar(user = user, size = 76.dp)
                    }

                    Surface(
                        shape = CircleShape,
                        color = StarPrimary,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                TextButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Choose Profile Picture", fontSize = 12.sp)
                }

                // Display Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )

                // Username Field
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = { Text("Username (@handle)") },
                    supportingText = { Text("Unique handle across AKASH SOCIAL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_username_input")
                )

                // Bio Field
                OutlinedTextField(
                    value = bio,
                    onValueChange = {
                        bio = it
                        errorMessage = null
                    },
                    label = { Text("Bio") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio_input")
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 Security: Platform ownership and verified status are cryptographically protected and cannot be altered via profile editing.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSaving = true
                    val result = onSave(name, username, bio, avatarUri)
                    isSaving = false
                    if (result.isSuccess) {
                        onDismiss()
                    } else {
                        errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to update profile."
                    }
                },
                enabled = !isSaving && name.isNotBlank() && username.isNotBlank(),
                modifier = Modifier.testTag("edit_profile_save_button")
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("edit_profile_cancel_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
