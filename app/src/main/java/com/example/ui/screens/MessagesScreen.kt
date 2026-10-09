package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.UserAvatar
import com.example.ui.components.VipStarBadge
import com.example.ui.theme.*

@Composable
fun ConversationsScreen(
    conversations: List<Conversation>,
    onSelectConversation: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("conversations_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Direct Messages",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(conversations) { conv ->
                val partner = conv.partner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectConversation(partner.id) }
                        .testTag("conversation_item_${partner.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            UserAvatar(user = partner, size = 48.dp)
                            if (conv.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(FriendEmerald)
                                        .border(2.dp, CosmicDarkBg, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = partner.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (partner.isFriend) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("🤝", fontSize = 11.sp)
                                }
                                if (partner.vipStarActive) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VipStarBadge(size = 12.dp)
                                }
                            }
                            Text(
                                text = conv.lastMessage.text,
                                fontSize = 12.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = conv.lastMessage.timestampText,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (conv.unreadCount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(StreakFlame),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = conv.unreadCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    currentUserId: String,
    partner: User,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onStartCall: (User, CallType) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chat_screen_${partner.id}")
    ) {
        // Chat Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                UserAvatar(user = partner, size = 38.dp)
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = partner.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (partner.isFriend) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🤝", fontSize = 12.sp)
                        }
                    }
                    Text(
                        text = if (partner.isFriend) "Friend • Online" else "Fan Connection",
                        fontSize = 11.sp,
                        color = if (partner.isFriend) FriendEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Voice Call Launcher
                IconButton(
                    onClick = { onStartCall(partner, CallType.VOICE) },
                    modifier = Modifier.testTag("chat_voice_call_button")
                ) {
                    Icon(Icons.Filled.Call, contentDescription = "Voice Call", tint = ReachCyanGlow)
                }

                // Video Call Launcher
                IconButton(
                    onClick = { onStartCall(partner, CallType.VIDEO) },
                    modifier = Modifier.testTag("chat_video_call_button")
                ) {
                    Icon(Icons.Filled.Videocam, contentDescription = "Video Call", tint = StreakFlameLight)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUserId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        color = if (isMe) StarPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (msg.sharedPostId != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CosmicDarkBg.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = VipGold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Shared Post Attachment", fontSize = 11.sp, color = VipGold)
                                    }
                                }
                            }
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.align(Alignment.End),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg.timestampText,
                                    fontSize = 9.sp,
                                    color = if (isMe) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isMe) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "✓✓",
                                        fontSize = 9.sp,
                                        color = ReachCyanGlow
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Write a message to your friend...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input"),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            val text = textInput
                            textInput = ""
                            onSendMessage(text)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(StarPrimary)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
