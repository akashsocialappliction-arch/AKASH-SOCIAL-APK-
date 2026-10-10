package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Post
import com.example.model.PostType
import com.example.model.User
import com.example.ui.theme.*

const val OFFICIAL_APK_DOWNLOAD_URL = "https://akash-social-nagod.app/download/akash_social_nagod_v1.0.0.apk"
const val OFFICIAL_APP_INVITE_URL = "https://akash-social-nagod.app/join"

/**
 * Premium App Download & Friends Invite Card.
 * Prominently presents the official APK release of AKASH SOCIAL NAGOD with a social "Share" button
 * to allow users to invite friends across WhatsApp, Telegram, Instagram, SMS, and other platforms.
 */
@Composable
fun AppDownloadCard(
    onShareApp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var copiedToClipboard by remember { mutableStateOf(false) }
    var downloadSimulated by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF131127),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(
                    StarPrimary,
                    Color(0xFF8B5CF6),
                    ReachCyan,
                    VipGold
                )
            )
        ),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .testTag("app_download_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: App Emblem & Version Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(VipGold, StarPrimary, CosmicDarkCard)
                                )
                            )
                            .border(1.5.dp, Color(0xFFFEF08A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Android,
                            contentDescription = "AKASH SOCIAL APK",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AKASH SOCIAL NAGOD",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "👑", fontSize = 12.sp)
                        }
                        Text(
                            text = "Official APK Release • v1.0.0-PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ReachCyanGlow
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Text(
                        text = "LIVE RELEASE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF34D399),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline & Description
            Text(
                text = "Your World. Your People. Your Moments.",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFDE047)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Connect with friends, watch vertical Reels, stream live, and share Stories with the creator community in Nagod and worldwide.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Feature Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChipFeature(icon = Icons.Filled.Verified, label = "Signed APK")
                ChipFeature(icon = Icons.Filled.Speed, label = "Ultra-Fast")
                ChipFeature(icon = Icons.Filled.LockOpen, label = "Free Forever")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary Social Share & Invite Friends Button
                Button(
                    onClick = {
                        if (onShareApp != null) {
                            onShareApp()
                        } else {
                            shareAppInvite(context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StarPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("download_card_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share & Invite",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                // Copy Link Button
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(OFFICIAL_APK_DOWNLOAD_URL))
                        copiedToClipboard = true
                        Toast.makeText(context, "Official APK download link copied!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("download_card_copy_link_button")
                ) {
                    Icon(
                        imageVector = if (copiedToClipboard) Icons.Filled.Check else Icons.Filled.ContentCopy,
                        contentDescription = "Copy Link",
                        tint = if (copiedToClipboard) Color(0xFF34D399) else Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copiedToClipboard) "Copied!" else "Copy Link",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                // Download Direct Action Button
                FilledTonalButton(
                    onClick = {
                        downloadSimulated = true
                        Toast.makeText(context, "Downloading AKASH SOCIAL NAGOD APK v1.0.0...", Toast.LENGTH_LONG).show()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ReachCyan.copy(alpha = 0.25f),
                        contentColor = ReachCyanGlow
                    ),
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("download_card_download_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = "Download APK",
                        tint = ReachCyanGlow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (downloadSimulated) "Ready" else "Download",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = downloadSimulated) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "APK Download Ready! You can install or share with friends.",
                            fontSize = 11.sp,
                            color = Color(0xFFD1FAE5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFeature(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ReachCyanGlow,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Launches the native Android Share sheet for inviting friends to download AKASH SOCIAL NAGOD.
 */
fun shareAppInvite(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        val inviteText = "🚀 Join me on AKASH SOCIAL NAGOD! 👑\n" +
                "\"Your World. Your People. Your Moments.\"\n\n" +
                "Connect with creators, record high-definition Reels, broadcast live, share daily Stories, and get exclusive badges!\n\n" +
                "📱 Download the Official Android APK:\n" +
                "$OFFICIAL_APK_DOWNLOAD_URL\n\n" +
                "✨ Built with pride by Akash Singh Parihar (@akash_dev)"
        putExtra(Intent.EXTRA_TEXT, inviteText)
        putExtra(Intent.EXTRA_SUBJECT, "Invite to AKASH SOCIAL NAGOD")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Invite friends to AKASH SOCIAL NAGOD via")
    context.startActivity(shareIntent)
}

/**
 * Interactive Share Media Modal Bottom Sheet.
 * Allows a user to:
 * 1. Directly share a video or photo to another person/friend inside AKASH SOCIAL NAGOD Chat with 1-tap.
 * 2. Share via Android System Share sheet (WhatsApp, Telegram, Instagram, Messages, etc.).
 * 3. Copy direct link to the post.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareMediaBottomSheet(
    post: Post,
    friends: List<User>,
    onSendDirectMessage: (partnerId: String, postId: String) -> Unit,
    onNativeShareComplete: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var sentUsers by remember { mutableStateOf(setOf<String>()) }
    var linkCopied by remember { mutableStateOf(false) }

    val postTypeLabel = when (post.type) {
        PostType.PHOTO -> "Photo 📸"
        PostType.VIDEO -> "Video 🎬"
        PostType.REEL -> "Reel 🎬"
        PostType.STORY -> "Story ✨"
        PostType.STAR_CLIP -> "StarClip 🌟"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.testTag("share_post_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Header: Post Summary Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(listOf(StarPrimary, ReachCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (post.type) {
                                PostType.PHOTO -> Icons.Filled.Photo
                                PostType.VIDEO, PostType.REEL, PostType.STAR_CLIP -> Icons.Filled.PlayCircle
                                PostType.STORY -> Icons.Filled.AutoAwesome
                            },
                            contentDescription = postTypeLabel,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Share $postTypeLabel",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StarPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = post.type.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StarPrimaryLight,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "By ${post.authorName} • \"${post.caption.take(50)}\"",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: In-App Direct Share to Friends
            Text(
                text = "Send directly to Friends on AKASH SOCIAL",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (friends.isEmpty()) {
                Text(
                    text = "Connect with creators to send instant DMs",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(friends) { friend ->
                        val isSent = sentUsers.contains(friend.id)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(68.dp)
                        ) {
                            UserAvatar(user = friend, size = 48.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = friend.name.split(" ").firstOrNull() ?: friend.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    if (!isSent) {
                                        onSendDirectMessage(friend.id, post.id)
                                        sentUsers = sentUsers + friend.id
                                        Toast.makeText(context, "Sent to ${friend.name}!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSent) FriendEmerald else StarPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("send_post_to_${friend.id}")
                            ) {
                                if (isSent) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Sent",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Sent", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                } else {
                                    Text("Send", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: External Sharing Actions
            Text(
                text = "Share to External Apps",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Native System Share Sheet (WhatsApp, Telegram, Messages, Instagram)
                Button(
                    onClick = {
                        sharePostExternally(context, post)
                        onNativeShareComplete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StarPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_post_system_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share $postTypeLabel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                // Copy Link to Post
                OutlinedButton(
                    onClick = {
                        val postUrl = "https://akash-social-nagod.app/post/${post.id}"
                        clipboardManager.setText(AnnotatedString(postUrl))
                        linkCopied = true
                        Toast.makeText(context, "Post link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("copy_post_link_button")
                ) {
                    Icon(
                        imageVector = if (linkCopied) Icons.Filled.Check else Icons.Filled.Link,
                        contentDescription = "Copy Link",
                        tint = if (linkCopied) FriendEmerald else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (linkCopied) "Copied!" else "Copy Link",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section 3: Invite Friends to Download the App Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicDarkSurface.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { shareAppInvite(context) }
                    .testTag("share_invite_app_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Invite Friends to AKASH SOCIAL NAGOD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = VipGold
                            )
                            Text(
                                text = "Share APK download link so they can watch your Reels",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = VipGold,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Triggers the native Android Intent chooser to share a specific post (video, photo, Reel, Story).
 */
fun sharePostExternally(context: Context, post: Post) {
    val postTypeLabel = when (post.type) {
        PostType.PHOTO -> "photo 📸"
        PostType.VIDEO -> "video 🎬"
        PostType.REEL -> "Reel 🎬"
        PostType.STORY -> "Story ✨"
        PostType.STAR_CLIP -> "StarClip 🌟"
    }

    val postUrl = "https://akash-social-nagod.app/post/${post.id}"
    val shareBody = "🔥 Check out this $postTypeLabel by ${post.authorName} on AKASH SOCIAL NAGOD!\n\n" +
            "\"${post.caption}\"\n\n" +
            "👀 Watch & interact here:\n" +
            "$postUrl\n\n" +
            "📱 Download AKASH SOCIAL NAGOD App:\n" +
            OFFICIAL_APK_DOWNLOAD_URL

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareBody)
        putExtra(Intent.EXTRA_SUBJECT, "Post by ${post.authorName} on AKASH SOCIAL NAGOD")
        type = "text/plain"
    }
    val chooser = Intent.createChooser(sendIntent, "Share $postTypeLabel with friends")
    context.startActivity(chooser)
}
