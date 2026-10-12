package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.theme.*

@Composable
fun StarPulseTopBar(
    streakDays: Int,
    unreadMessages: Int,
    unreadNotifications: Int,
    hasLiveStreams: Boolean,
    onStreakClick: () -> Unit,
    onLiveClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSupportClick: () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("starpulse_top_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onStreakClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(StarPrimary, ReachCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "StarPulse",
                        tint = VipGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "AKASH SOCIAL NAGOD",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Quick Actions: Streak, Live, Notifications, Messages, Menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Streak Chip
                StreakChip(
                    streakDays = streakDays,
                    onClick = onStreakClick
                )

                // StarLive Portal Button
                IconButton(
                    onClick = onLiveClick,
                    modifier = Modifier.testTag("top_bar_live_button")
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Filled.Podcasts,
                            contentDescription = "StarLive Streams",
                            tint = if (hasLiveStreams) StreakFlameLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (hasLiveStreams) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    }
                }

                // Notifications
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("top_bar_notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge(
                                    containerColor = StarPrimary,
                                    contentColor = Color.White
                                ) {
                                    Text(unreadNotifications.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Direct Messages
                IconButton(
                    onClick = onMessagesClick,
                    modifier = Modifier.testTag("top_bar_messages_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadMessages > 0) {
                                Badge(
                                    containerColor = StreakFlame,
                                    contentColor = Color.White
                                ) {
                                    Text(unreadMessages.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Direct Messages",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Support & Admin Quick Dropdown Trigger
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier.testTag("top_bar_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Admin & Continuous Improvement Center",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onSettingsClick != null) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings & Updates",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StarPulseBottomBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier
            .navigationBarsPadding()
            .testTag("starpulse_bottom_navigation")
    ) {
        // HOME
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { onTabSelected(MainTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home Feed"
                )
            },
            label = { Text("Feed", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StarPrimaryLight,
                indicatorColor = StarPrimary.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_home_tab")
        )

        // DISCOVER
        NavigationBarItem(
            selected = currentTab == MainTab.DISCOVER,
            onClick = { onTabSelected(MainTab.DISCOVER) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.DISCOVER) Icons.Filled.Explore else Icons.Outlined.Explore,
                    contentDescription = "Discover"
                )
            },
            label = { Text("Discover", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ReachCyanGlow,
                indicatorColor = ReachCyan.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_discover_tab")
        )

        // CREATE (Prominent Creator Button)
        NavigationBarItem(
            selected = currentTab == MainTab.CREATE,
            onClick = { onTabSelected(MainTab.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(StarPrimary, ReachCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Post or StarClip",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            label = { Text("Create", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("nav_create_tab")
        )

        // TASKS & STREAKS
        NavigationBarItem(
            selected = currentTab == MainTab.TASKS,
            onClick = { onTabSelected(MainTab.TASKS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.TASKS) Icons.Filled.TaskAlt else Icons.Outlined.TaskAlt,
                    contentDescription = "Tasks & Streaks"
                )
            },
            label = { Text("Tasks", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VipGold,
                indicatorColor = VipGold.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_tasks_tab")
        )

        // PROFILE
        NavigationBarItem(
            selected = currentTab == MainTab.PROFILE,
            onClick = { onTabSelected(MainTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Creator Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StarPrimaryLight,
                indicatorColor = StarPrimary.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_profile_tab")
        )
    }
}
