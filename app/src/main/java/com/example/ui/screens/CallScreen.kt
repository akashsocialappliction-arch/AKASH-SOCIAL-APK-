package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveCall
import com.example.model.CallStatus
import com.example.model.CallType
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*

@Composable
fun CallOverlayScreen(
    call: ActiveCall,
    onAnswer: () -> Unit,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleCamera: () -> Unit,
    onFlipCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDarkBg)
            .testTag("call_overlay_screen")
    ) {
        // Video Preview Background (if Video call and Camera on)
        if (call.type == CallType.VIDEO && call.status == CallStatus.ONGOING && call.isCameraOn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
            ) {
                // Partner simulated video stream
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatar(user = call.partner, size = 110.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${call.partner.name} (Live Video)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // PiP local camera preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ReachCyanGlow),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 20.dp)
                        .size(width = 90.dp, height = 120.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("You", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        } else {
            // Voice Call Ambient Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            listOf(StarPrimaryVariant.copy(alpha = 0.35f), CosmicDarkBg)
                        )
                    )
            )
        }

        // Main Caller Info Column
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (call.type != CallType.VIDEO || call.status != CallStatus.ONGOING) {
                Box(
                    modifier = Modifier
                        .scale(if (call.status == CallStatus.OUTGOING || call.status == CallStatus.INCOMING) pulseScale else 1f),
                    contentAlignment = Alignment.Center
                ) {
                    UserAvatar(user = call.partner, size = 100.dp)
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = call.partner.name,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            val statusText = when (call.status) {
                CallStatus.OUTGOING -> "Calling..."
                CallStatus.INCOMING -> "Incoming ${if (call.type == CallType.VIDEO) "Video" else "Voice"} Call..."
                CallStatus.ONGOING -> {
                    val mins = call.durationSeconds / 60
                    val secs = call.durationSeconds % 60
                    String.format("%02d:%02d", mins, secs)
                }
                else -> "Call Ending..."
            }

            Text(
                text = statusText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = when (call.status) {
                    CallStatus.ONGOING -> FriendEmeraldGlow
                    CallStatus.INCOMING -> VipGold
                    else -> ReachCyanGlow
                }
            )

            if (call.partner.isFriend) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FriendEmerald.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Mutual Friend Connection 🤝",
                        fontSize = 11.sp,
                        color = FriendEmeraldGlow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (call.status == CallStatus.INCOMING) {
                // Incoming Call Actions: Accept / Decline
                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decline Red
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .testTag("decline_call_button")
                    ) {
                        Icon(Icons.Filled.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(30.dp))
                    }

                    // Accept Green
                    IconButton(
                        onClick = onAnswer,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(FriendEmerald)
                            .testTag("answer_call_button")
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = "Answer", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            } else {
                // Ongoing or Outgoing Call Controls: Mute, Speaker, Camera, Flip, End
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Toggle
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (call.isMuted) Color.White else Color.White.copy(alpha = 0.2f))
                            .testTag("toggle_mute_button")
                    ) {
                        Icon(
                            imageVector = if (call.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                            contentDescription = "Mute",
                            tint = if (call.isMuted) Color.Black else Color.White
                        )
                    }

                    // Speaker Toggle
                    IconButton(
                        onClick = onToggleSpeaker,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (call.isSpeaker) Color.White else Color.White.copy(alpha = 0.2f))
                            .testTag("toggle_speaker_button")
                    ) {
                        Icon(
                            imageVector = if (call.isSpeaker) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                            contentDescription = "Speaker",
                            tint = if (call.isSpeaker) Color.Black else Color.White
                        )
                    }

                    // Video Specific Controls
                    if (call.type == CallType.VIDEO) {
                        IconButton(
                            onClick = onToggleCamera,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(if (!call.isCameraOn) Color.White else Color.White.copy(alpha = 0.2f))
                                .testTag("toggle_camera_button")
                        ) {
                            Icon(
                                imageVector = if (call.isCameraOn) Icons.Filled.Videocam else Icons.Filled.VideocamOff,
                                contentDescription = "Camera",
                                tint = if (!call.isCameraOn) Color.Black else Color.White
                            )
                        }

                        IconButton(
                            onClick = onFlipCamera,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("flip_camera_button")
                        ) {
                            Icon(Icons.Filled.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White)
                        }
                    }

                    // End Call Button
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .testTag("end_call_button")
                    ) {
                        Icon(Icons.Filled.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                }
            }
        }
    }
}
