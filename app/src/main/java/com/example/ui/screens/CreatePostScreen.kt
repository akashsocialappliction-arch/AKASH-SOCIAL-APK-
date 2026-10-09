package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.R
import com.example.model.AudioTrack
import com.example.model.PostType
import com.example.model.User
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*

@Composable
fun CreatePostScreen(
    currentUser: User,
    selectedAudio: AudioTrack?,
    onOpenAudioPicker: () -> Unit,
    onRemoveAudio: () -> Unit,
    onPublish: (PostType, String, List<String>, List<String>, Int?, Int) -> Unit,
    onStartLive: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(PostType.PHOTO) }
    var captionText by remember { mutableStateOf("") }
    var selectedHashtags by remember { mutableStateOf(listOf("#AkashSocial", "#DailyStreak")) }
    var selectedMentions by remember { mutableStateOf(emptyList<String>()) }
    var attachSampleArtwork by remember { mutableStateOf(false) }
    var isCameraOpen by remember { mutableStateOf(false) }
    var recordedVideoDuration by remember { mutableStateOf<Int?>(null) }

    if (isCameraOpen) {
        CameraCaptureScreen(
            onVideoCaptured = { duration ->
                recordedVideoDuration = duration
                isCameraOpen = false
            },
            onClose = { isCameraOpen = false }
        )
        return
    }

    val hashtagSuggestions = listOf(
        "#AkashSocial", "#CreatorReach", "#DailyStreak", "#StarClip", "#Design", "#Art", "#Friends"
    )

    val mentionSuggestions = listOf(
        "@aarav_creates", "@ananya_art", "@aditya_v", "@yash_cinema", "@kavya_sound"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("create_post_screen")
    ) {
        // Author Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            UserAvatar(user = currentUser, size = 48.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentUser.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Creating for ${currentUser.fansCount} Fans & Friends",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onStartLive != null) {
                Button(
                    onClick = onStartLive,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("create_screen_go_live_button")
                ) {
                    Icon(Icons.Filled.Podcasts, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GO LIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Post Type Tabs: Photo, Video, StarClip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(
                PostType.PHOTO to "📷 Photo Post",
                PostType.VIDEO to "🎥 Video Post",
                PostType.STAR_CLIP to "⚡ StarClip"
            ).forEach { (type, label) ->
                val isSelected = selectedType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) StarPrimary else Color.Transparent)
                        .clickable { selectedType = type }
                        .padding(vertical = 10.dp)
                        .testTag("type_tab_${type.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Camera Capture Action for Video & StarClip
        if (selectedType == PostType.VIDEO || selectedType == PostType.STAR_CLIP) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (recordedVideoDuration != null) FriendEmerald.copy(alpha = 0.12f) else ReachCyan.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (recordedVideoDuration != null) FriendEmerald else ReachCyanGlow.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (recordedVideoDuration != null) FriendEmerald else ReachCyanGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (recordedVideoDuration != null) Icons.Filled.Check else Icons.Filled.Videocam,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (recordedVideoDuration != null) "Camera Video Captured (${recordedVideoDuration}s)" else "Capture with CameraX",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (recordedVideoDuration != null) "Ready to share with your Fans & Friends ✓" else "Record native camera video in real-time",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { isCameraOpen = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (recordedVideoDuration != null) MaterialTheme.colorScheme.surfaceVariant else ReachCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("open_camera_capture_button")
                    ) {
                        Text(
                            text = if (recordedVideoDuration != null) "Retake" else "Record",
                            color = if (recordedVideoDuration != null) MaterialTheme.colorScheme.onSurface else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input
        OutlinedTextField(
            value = captionText,
            onValueChange = { captionText = it },
            placeholder = { Text("What are you creating today? Share your thoughts, techniques, or milestones...") },
            minLines = 4,
            maxLines = 8,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("caption_input_field")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // MUSIC / AUDIO LIBRARY SECTION
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (selectedAudio != null) ReachCyanGlow else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(StarPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = ReachCyanGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Soundtrack & Audio",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (selectedAudio != null) {
                        IconButton(
                            onClick = onRemoveAudio,
                            modifier = Modifier.testTag("remove_selected_audio")
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove audio", tint = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedAudio != null) {
                    // Display selected audio track
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Headphones,
                                contentDescription = null,
                                tint = StarPrimaryLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedAudio.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${selectedAudio.artist} • ${selectedAudio.durationSeconds}s",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Attached ✓",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FriendEmerald
                            )
                        }
                    }
                } else {
                    // Button to open Audio Library
                    OutlinedButton(
                        onClick = onOpenAudioPicker,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_audio_library_button")
                    ) {
                        Icon(Icons.Filled.LibraryMusic, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Music / Search Audio Tracks")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sample Artwork toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = attachSampleArtwork,
                onCheckedChange = { attachSampleArtwork = it },
                modifier = Modifier.testTag("attach_artwork_checkbox")
            )
            Text(
                text = "Attach Hi-Res Visual Artwork (${if (selectedType == PostType.STAR_CLIP) "Vertical Clip Render" else "Starlight City Skyline"})",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hashtags Row
        Text(
            text = "Add Hashtags:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(hashtagSuggestions) { tag ->
                val isSelected = selectedHashtags.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedHashtags = if (isSelected) selectedHashtags - tag else selectedHashtags + tag
                    },
                    label = { Text(tag, fontSize = 11.sp) },
                    modifier = Modifier.testTag("chip_tag_$tag")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mentions Row
        Text(
            text = "Mention Creators:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(mentionSuggestions) { mention ->
                val isSelected = selectedMentions.contains(mention)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedMentions = if (isSelected) selectedMentions - mention else selectedMentions + mention
                    },
                    label = { Text(mention, fontSize = 11.sp) },
                    modifier = Modifier.testTag("chip_mention_$mention")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reach Forecast & Task Reward Indicator
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = ReachCyan.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ReachCyan.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = ReachCyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Projected Reach: 1,200 – 2,200 creators",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReachCyanGlow
                    )
                    Text(
                        text = "Publishing will advance your active Tasks & streak score!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Publish Button
        Button(
            onClick = {
                val finalCaption = if (captionText.isBlank()) {
                    when (selectedType) {
                        PostType.PHOTO -> "Sharing new creator concept art on StarPulse! 🚀"
                        PostType.VIDEO -> "New studio workflow video. Check out the creative process!"
                        PostType.STAR_CLIP -> "Daily StarClip: Building momentum and keeping our streak alive! 🔥"
                    }
                } else captionText

                val mediaRes = if (attachSampleArtwork) R.drawable.post_artwork else null
                val duration = recordedVideoDuration ?: (if (selectedType == PostType.STAR_CLIP) 38 else if (selectedType == PostType.VIDEO) 65 else 0)

                onPublish(selectedType, finalCaption, selectedHashtags, selectedMentions, mediaRes, duration)
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_post_button")
        ) {
            Text(
                text = "Publish to AKASH SOCIAL NAGOD",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
