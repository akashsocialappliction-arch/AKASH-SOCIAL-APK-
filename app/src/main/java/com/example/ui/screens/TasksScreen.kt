package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.model.AchievementItem
import com.example.model.TaskItem
import com.example.model.User
import com.example.ui.components.PremiumStarBadgeImage
import com.example.ui.components.VipStarBadge
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    currentUser: User,
    tasks: List<TaskItem>,
    achievements: List<AchievementItem>,
    onClaimTask: (String) -> Unit,
    onClaimAchievement: (String) -> Unit,
    onClaimLaunchVipStar: () -> Unit,
    onRenewVipStar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("Tasks") } // "Tasks" or "Achievements"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tasks_and_streaks_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Streak Hero Banner
        item {
            StreakHeroCard(streakDays = currentUser.streakDays)
        }

        // Live Daily Progress & Star Badge Milestone Tracker Card
        item {
            StarBadgeLiveTrackerCard(
                currentUser = currentUser,
                tasks = tasks,
                onClaimVip = onClaimLaunchVipStar
            )
        }

        // VIP Star Card
        item {
            VipStarStatusCard(
                currentUser = currentUser,
                onClaimLaunch = onClaimLaunchVipStar,
                onRenew = onRenewVipStar
            )
        }

        // Section Tabs: Active Tasks vs Achievements
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                listOf("Tasks", "Achievements").forEach { section ->
                    val isSelected = selectedSection == section
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) StarPrimary else Color.Transparent)
                            .clickable { selectedSection = section }
                            .padding(vertical = 10.dp)
                            .testTag("section_tab_$section"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (section == "Tasks") "Active Tasks (${tasks.count { !it.isClaimed }})" else "Achievements (${achievements.count { it.isUnlocked }})",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Section Content
        if (selectedSection == "Tasks") {
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onClaim = { onClaimTask(task.id) }
                )
            }
        } else {
            items(achievements) { ach ->
                AchievementCard(
                    achievement = ach,
                    onClaim = { onClaimAchievement(ach.id) }
                )
            }
        }
    }
}

@Composable
fun StreakHeroCard(streakDays: Int) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, StreakFlame.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("streak_hero_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "$streakDays Days Activity Streak",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = StreakFlameLight
                        )
                        Text(
                            text = "Rewards meaningful creative participation",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StreakFlame.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Active Pulse",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StreakFlameLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Streak Milestones Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    "1 Day" to (streakDays >= 1),
                    "7 Days" to (streakDays >= 7),
                    "15 Days" to (streakDays >= 15),
                    "30 Days" to (streakDays >= 30),
                    "60 Days" to (streakDays >= 60)
                ).forEach { (milestone, isReached) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isReached) StreakFlame else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isReached) StreakFlameLight else MaterialTheme.colorScheme.outline,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isReached) "✓" else "○",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isReached) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = milestone,
                            fontSize = 10.sp,
                            fontWeight = if (isReached) FontWeight.Bold else FontWeight.Normal,
                            color = if (isReached) StreakFlameLight else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StarBadgeLiveTrackerCard(
    currentUser: User,
    tasks: List<TaskItem>,
    onClaimVip: () -> Unit
) {
    val completedCount = tasks.count { it.isCompleted || it.isClaimed }
    val totalCount = tasks.size.coerceAtLeast(1)
    val taskProgressFloat = (completedCount.toFloat() / totalCount).coerceIn(0f, 1f)
    val streakTarget = 15
    val streakProgressFloat = (currentUser.streakDays.toFloat() / streakTarget).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("star_badge_live_tracker_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PremiumStarBadgeImage(size = 36.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Premium Star Badge",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (currentUser.vipStarActive) FriendEmerald.copy(alpha = 0.2f) else VipGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (currentUser.vipStarActive) "ACTIVE (20 Days)" else "TRACK PROGRESS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUser.vipStarActive) FriendEmerald else VipGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (currentUser.vipStarActive)
                                "Entitlement: ${currentUser.vipStarExpiresInDays.coerceAtMost(20)} Days Remaining"
                            else
                                "Complete daily milestones to unlock the official 20-Day Star Badge",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Milestone 1: Daily Tasks Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯 Daily Tasks:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$completedCount / $totalCount Completed", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${(taskProgressFloat * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = StarPrimaryLight)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { taskProgressFloat },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = StarPrimary,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Milestone 2: Activity Streak Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥 Activity Streak:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${currentUser.streakDays} / $streakTarget Days", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${(streakProgressFloat * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = StreakFlame)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { streakProgressFloat },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = StreakFlame,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!currentUser.vipStarActive) {
                Button(
                    onClick = onClaimVip,
                    colors = ButtonDefaults.buttonColors(containerColor = VipGoldDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("claim_20_day_star_badge_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PremiumStarBadgeImage(size = 18.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activate 20-Day Free Star Badge", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FriendEmerald.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FriendEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PremiumStarBadgeImage(size = 20.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Official Premium Star Badge active on your profile! 🌟",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FriendEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VipStarStatusCard(
    currentUser: User,
    onClaimLaunch: () -> Unit,
    onRenew: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, VipGold.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vip_star_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VipStarBadge(size = 22.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VIP Star Status",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VipGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (currentUser.vipStarActive) "ACTIVE" else "LOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VipGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Promotional Launch Reward • Free for First 2 Months",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Validity Countdown:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${currentUser.vipStarExpiresInDays} Days Remaining",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VipGold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "After the launch period, renew by completing requirements: 15-day streak (${currentUser.streakDays}/15 ✓), 1,000+ Reach (${currentUser.totalReach}/1000 ✓), and creative engagement.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!currentUser.vipStarActive) {
                Button(
                    onClick = onClaimLaunch,
                    colors = ButtonDefaults.buttonColors(containerColor = VipGoldDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("claim_launch_vip_star_button")
                ) {
                    Text("Claim Free Launch VIP Star 🌟", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = onRenew,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("renew_vip_star_button")
                ) {
                    Text("Renew VIP Star for +60 Days (Eligible ✓)", color = VipGold)
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onClaim: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isCompleted && !task.isClaimed) FriendEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StarPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = task.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarPrimaryLight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "⏳ ${task.timeRemaining}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = task.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress: ${task.progress} / ${task.target}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${((task.progress.toFloat() / task.target) * 100).toInt().coerceAtMost(100)}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) FriendEmerald else StarPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { (task.progress.toFloat() / task.target).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (task.isCompleted) FriendEmerald else StarPrimaryLight,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reward: ${task.rewardTitle}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = VipGold
                )

                if (task.isClaimed) {
                    Text(
                        text = "Claimed ✓",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FriendEmerald
                    )
                } else if (task.isCompleted) {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(containerColor = FriendEmerald),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("claim_task_button_${task.id}")
                    ) {
                        Text("Claim Reward ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "In Progress",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: AchievementItem,
    onClaim: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (achievement.isUnlocked) VipGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_card_${achievement.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (achievement.isUnlocked) VipGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = achievement.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = achievement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (achievement.isUnlocked) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🏆", fontSize = 12.sp)
                    }
                }

                Text(
                    text = achievement.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Bonus: ${achievement.reward}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VipGold
                )
            }

            if (achievement.isClaimed) {
                Text(
                    text = "Unlocked ✓",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FriendEmerald
                )
            } else if (achievement.isUnlocked) {
                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(containerColor = VipGoldDark),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("claim_achievement_button_${achievement.id}")
                ) {
                    Text("Claim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "${achievement.progress}/${achievement.target}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
