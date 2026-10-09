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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DiscoverScreen(
    creators: List<User>,
    posts: List<Post>,
    spotlights: List<CreatorSpotlight>,
    selectedSpotlightFilter: SpotlightMetric?,
    onSetSpotlightFilter: (SpotlightMetric?) -> Unit,
    onToggleFan: (String) -> Unit,
    onCreatorClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Creators", "StarClips", "High Reach", "Visual Art", "Tech & Tools")

    // Filter creators by search
    val filteredCreators = remember(searchQuery, creators) {
        if (searchQuery.isBlank()) creators
        else creators.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.bio.contains(searchQuery, ignoreCase = true)
        }
    }

    // Filter spotlights by selected criteria
    val filteredSpotlights = remember(selectedSpotlightFilter, spotlights) {
        if (selectedSpotlightFilter == null) spotlights
        else spotlights.filter { it.metric == selectedSpotlightFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("discover_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search creators, tags, #1KReach, StarClips...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = StarPrimaryLight)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discover_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        // Category Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ReachCyan,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("discover_chip_$cat")
                    )
                }
            }
        }

        // CREATOR SPOTLIGHT SECTION (Configurable Criteria)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌟", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Creator Spotlight",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Dynamic Selection",
                        fontSize = 11.sp,
                        color = VipGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Celebrating outstanding creators by reach, engagement, streaks & tasks",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Configurable Criteria Filter Bar
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedSpotlightFilter == null,
                            onClick = { onSetSpotlightFilter(null) },
                            label = { Text("All Criteria", fontSize = 11.sp) },
                            modifier = Modifier.testTag("spotlight_filter_all")
                        )
                    }
                    items(SpotlightMetric.values()) { metric ->
                        FilterChip(
                            selected = selectedSpotlightFilter == metric,
                            onClick = { onSetSpotlightFilter(metric) },
                            label = { Text(metric.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VipGoldDark,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("spotlight_filter_${metric.name}")
                        )
                    }
                }
            }
        }

        // Spotlighted Creators Horizontal Cards
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredSpotlights) { spot ->
                    SpotlightCreatorCard(
                        spotlight = spot,
                        onCreatorClick = { onCreatorClick(spot.creator.id) },
                        onToggleFan = { onToggleFan(spot.creator.id) }
                    )
                }
            }
        }

        // Trending Hashtags
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔥 Trending Creator Topics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            "#1KReach" to "28.4K posts",
                            "#DailyStreak" to "42.1K posts",
                            "#VisualArt" to "19.5K posts",
                            "#StarClip" to "33.8K clips"
                        ).forEach { (tag, count) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StarPrimaryLight
                                )
                                Text(
                                    text = count,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Rising Creators
        item {
            Text(
                text = "Rising Community Creators",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(filteredCreators) { creator ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreatorClick(creator.id) }
                    .testTag("discover_creator_${creator.id}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(user = creator, size = 46.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = creator.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (creator.vipStarActive) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VipStarBadge(size = 13.dp)
                            }
                        }
                        Text(
                            text = "${creator.fansCount} Fans • ${creator.totalReach} Reach",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = creator.bio,
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FanFriendButton(
                        user = creator,
                        onToggleFan = { onToggleFan(creator.id) },
                        compact = true
                    )
                }
            }
        }
    }
}

@Composable
fun SpotlightCreatorCard(
    spotlight: CreatorSpotlight,
    onCreatorClick: () -> Unit,
    onToggleFan: () -> Unit
) {
    val creator = spotlight.creator
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.4f)),
        modifier = Modifier
            .width(260.dp)
            .clickable { onCreatorClick() }
            .testTag("spotlight_card_${spotlight.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Metric Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ReachCyan.copy(alpha = 0.2f)
            ) {
                Text(
                    text = spotlight.metric.badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReachCyanGlow,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(user = creator, size = 44.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = creator.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = creator.username,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = spotlight.reasonWhyFeatured,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 ${creator.streakDays}d Streak",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StreakFlameLight
                )

                FanFriendButton(
                    user = creator,
                    onToggleFan = onToggleFan,
                    compact = true
                )
            }
        }
    }
}
