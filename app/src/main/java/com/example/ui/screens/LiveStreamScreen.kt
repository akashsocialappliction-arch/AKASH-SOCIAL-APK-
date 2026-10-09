package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LiveChatMessage
import com.example.model.LiveStream
import com.example.model.User
import com.example.ui.components.CreatorBadge
import com.example.ui.components.FanFriendButton
import com.example.ui.components.UserAvatar
import com.example.ui.components.VipStarBadge
import com.example.ui.theme.*

@Composable
fun LiveStreamScreen(
    stream: LiveStream,
    currentUser: User,
    onSendComment: (String) -> Unit,
    onSendSpark: () -> Unit,
    onToggleFan: () -> Unit,
    onClose: () -> Unit,
    onEndLive: () -> Unit = onClose,
    onDeleteMessage: ((String) -> Unit)? = null,
    onSetPinnedAnnouncement: ((String?) -> Unit)? = null,
    onToggleMuteChat: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var commentInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val isHost = stream.creator.id == currentUser.id || stream.isHost
    var showEndLiveConfirm by remember { mutableStateOf(false) }
    var showModerationSheet by remember { mutableStateOf(false) }
    var streamSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            streamSeconds++
        }
    }

    val streamDurationText = remember(streamSeconds) {
        val mins = streamSeconds / 60
        val secs = streamSeconds % 60
        String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
    }

    LaunchedEffect(stream.chatMessages.size) {
        if (stream.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(stream.chatMessages.size - 1)
        }
    }

    val quickLanguagePresets = listOf(
        "नमस्ते 🙏",
        "बहुत खूब! 🌟",
        "Super stream 🔥",
        "Kaise ho? 😃",
        "Awesome! 🚀",
        "Like & Share ❤️",
        "Boht badhiya 👏",
        "✨",
        "🔥",
        "👏",
        "❤️",
        "🚀",
        "🤝",
        "💯"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDarkBg)
            .testTag("live_stream_screen")
    ) {
        // Stream Background Canvas / Broadcast Viewfinder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), CosmicDarkBg)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                UserAvatar(user = stream.creator, size = 96.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stream.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHost) "🔴 Broadcasting as Host ($streamDurationText)" else "Broadcasting Live ($streamDurationText)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ReachCyanGlow
                    )
                }

                if (isHost) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CreatorBadge(size = 12.dp, showLabel = true, isOwner = true)
                }
            }
        }

        // Top Overlay Header: Host info, Live badge, Viewer count, Moderate/End/Close
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(user = stream.creator, size = 40.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stream.creator.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        if (stream.creator.isDeveloper || stream.creator.isCreator) {
                            Spacer(modifier = Modifier.width(4.dp))
                            CreatorBadge(size = 11.dp, showLabel = false)
                        } else if (stream.creator.vipStarActive) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VipStarBadge(size = 12.dp)
                        }
                    }
                    Text(
                        text = "${stream.creator.fansCount} Fans • ${stream.creator.friendsCount} Friends",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                if (!isHost) {
                    Spacer(modifier = Modifier.width(10.dp))
                    FanFriendButton(
                        user = stream.creator,
                        onToggleFan = onToggleFan,
                        compact = true
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Red.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = if (isHost) "HOST" else "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Real-time Dynamic Viewer Count with Eye Icon (Requirement 19)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("live_viewer_count_badge"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = "Live Viewers",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${stream.viewerCount}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Host Controls: Moderate & End Live
                if (isHost) {
                    IconButton(
                        onClick = { showModerationSheet = true },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("host_moderate_chat_button")
                    ) {
                        Icon(Icons.Filled.Shield, contentDescription = "Moderate", tint = ReachCyanGlow, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = { showEndLiveConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("host_end_live_button")
                    ) {
                        Text("End", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("live_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }
        }

        // Pinned Announcement Banner
        if (stream.pinnedAnnouncement != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = VipGold.copy(alpha = 0.85f),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 76.dp, start = 14.dp, end = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📌", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stream.pinnedAnnouncement!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Live Chat Messages (Bottom Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.85f)
                .padding(bottom = 128.dp, start = 14.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(stream.chatMessages) { chat ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${chat.username}: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = when {
                                    chat.isCreator -> Color(0xFFC084FC)
                                    chat.isVip -> VipGold
                                    else -> ReachCyanGlow
                                }
                            )
                            Text(
                                text = chat.message,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            if (isHost && onDeleteMessage != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "✕",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.clickable { onDeleteMessage(chat.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Multi-Language Chips (Hindi / English / Hinglish / Emojis)
        LazyRow(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 74.dp, start = 14.dp, end = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickLanguagePresets) { preset ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.clickable {
                        onSendComment(preset)
                    }
                ) {
                    Text(
                        text = preset,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Bottom Interaction Bar: Comment input + Spark reaction button
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentInput,
                onValueChange = { commentInput = it },
                placeholder = {
                    Text(
                        text = if (stream.isChatMuted) "Chat muted by host" else "Chat (Hindi, English, Hinglish)...",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                enabled = !stream.isChatMuted || isHost,
                modifier = Modifier
                    .weight(1f)
                    .testTag("live_comment_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = StarPrimaryLight,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send Comment
            IconButton(
                onClick = {
                    if (commentInput.isNotBlank()) {
                        val text = commentInput
                        commentInput = ""
                        onSendComment(text)
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(StarPrimary)
                    .testTag("live_send_comment_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send Starlight Spark Reaction
            IconButton(
                onClick = onSendSpark,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(VipGold, StreakFlame))
                    )
                    .testTag("live_send_spark_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = "Send Starlight Spark",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Host End Live Confirmation Dialog
    if (showEndLiveConfirm) {
        AlertDialog(
            onDismissRequest = { showEndLiveConfirm = false },
            title = { Text("End Live Broadcast?", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to end this live session? All viewers will disconnect safely.") },
            confirmButton = {
                Button(
                    onClick = {
                        showEndLiveConfirm = false
                        onEndLive()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("End Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndLiveConfirm = false }) {
                    Text("Stay Live")
                }
            }
        )
    }

    // Host Moderation Modal
    if (showModerationSheet) {
        var announcementInput by remember { mutableStateOf(stream.pinnedAnnouncement ?: "") }

        AlertDialog(
            onDismissRequest = { showModerationSheet = false },
            title = { Text("Live Host Moderation", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = announcementInput,
                        onValueChange = { announcementInput = it },
                        label = { Text("Pinned Announcement") },
                        placeholder = { Text("Welcome to my stream!") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onSetPinnedAnnouncement?.invoke(if (announcementInput.isBlank()) null else announcementInput.trim())
                            showModerationSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Announcement")
                    }

                    if (onToggleMuteChat != null) {
                        OutlinedButton(
                            onClick = {
                                onToggleMuteChat()
                                showModerationSheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (stream.isChatMuted) "Unmute Chat" else "Mute Chat for Viewers")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModerationSheet = false }) {
                    Text("Close")
                }
            }
        )
    }
}
