package com.example.ui.screens

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
import com.example.model.AudioTrack
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicAudioSheet(
    tracks: List<AudioTrack>,
    playingTrackId: String?,
    onPlayToggle: (String) -> Unit,
    onSelectTrack: (AudioTrack) -> Unit,
    onSearch: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Trending", "Synthwave", "Electronic", "Lo-Fi Chill", "Acoustic", "Ambient")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("music_audio_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StarPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = null,
                            tint = StarPrimaryLight
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Music & Creator Audio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Royalty-free soundtracks & creator sounds",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearch(it, selectedCategory)
                },
                placeholder = { Text("Search songs, artists, moods...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search audio")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onSearch("", selectedCategory)
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_audio_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = cat
                            onSearch(searchQuery, cat)
                        },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StarPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Copyright & Licensing Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = FriendEmerald
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Cleared for AKASH SOCIAL NAGOD Creator Sharing • Copyright Compliant",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Audio Tracks List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
            ) {
                items(tracks) { track ->
                    val isPlaying = track.id == playingTrackId
                    AudioTrackItem(
                        track = track,
                        isPlaying = isPlaying,
                        onPlayToggle = { onPlayToggle(track.id) },
                        onSelect = { onSelectTrack(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun AudioTrackItem(
    track: AudioTrack,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isPlaying) StarPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isPlaying) androidx.compose.foundation.BorderStroke(1.dp, StarPrimaryLight) else null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audio_track_${track.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play / Pause Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) StarPrimary else MaterialTheme.colorScheme.surface)
                    .clickable { onPlayToggle() }
                    .testTag("play_audio_${track.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = if (isPlaying) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Track Details & Simulated Waveform
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${track.artist} • ${track.durationSeconds}s • ${track.category}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Waveform visualization
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    track.waveformPreview.forEach { heightRatio ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(fraction = heightRatio)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isPlaying) ReachCyanGlow else MaterialTheme.colorScheme.outline)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Use Sound Button
            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("use_sound_button_${track.id}")
            ) {
                Text(text = "Use", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
