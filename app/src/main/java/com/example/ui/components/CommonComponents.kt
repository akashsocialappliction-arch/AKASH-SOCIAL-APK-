package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.model.User
import com.example.ui.theme.*
import coil.compose.AsyncImage

@Composable
fun PremiumStarBadgeImage(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_premium_star_badge),
        contentDescription = "Official Premium Star Badge",
        modifier = modifier
            .size(size)
            .testTag("official_premium_star_badge_image"),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun VipStarBadge(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    showLabel: Boolean = false,
    labelModifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .testTag("vip_star_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(VipGoldDark.copy(alpha = 0.25f), VipGold.copy(alpha = 0.35f))
                )
            )
            .border(
                1.dp,
                brush = Brush.linearGradient(listOf(VipGold, VipGoldLight)),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (showLabel) 6.dp else 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        PremiumStarBadgeImage(size = size)
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Premium Star",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VipGold,
                modifier = labelModifier
            )
        }
    }
}

@Composable
fun DeveloperBadge(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    showLabel: Boolean = true
) {
    Row(
        modifier = modifier
            .testTag("developer_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(StarPrimary.copy(alpha = 0.25f), ReachCyan.copy(alpha = 0.35f))
                )
            )
            .border(
                1.dp,
                brush = Brush.linearGradient(listOf(ReachCyanGlow, StarPrimaryLight)),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (showLabel) 8.dp else 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Code,
            contentDescription = "Developer of AKASH SOCIAL",
            tint = ReachCyanGlow,
            modifier = Modifier.size(size)
        )
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Developer of AKASH SOCIAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ReachCyanGlow
            )
        }
    }
}

@Composable
fun OwnerBadge(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    showLabel: Boolean = true
) {
    Row(
        modifier = modifier
            .testTag("owner_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFB45309), Color(0xFFD97706), Color(0xFFF59E0B))
                )
            )
            .border(
                1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFDE047), Color(0xFFFCD34D), Color(0xFFFFFFFF))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (showLabel) 8.dp else 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Diamond,
            contentDescription = "Owner",
            tint = Color(0xFFFEF08A),
            modifier = Modifier.size(size)
        )
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Owner 👑",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun SpecialPremiumBadge(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    showLabel: Boolean = true
) {
    Row(
        modifier = modifier
            .testTag("special_premium_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF7C3AED), Color(0xFFC026D3), Color(0xFFE11D48), Color(0xFFF59E0B))
                )
            )
            .border(
                1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFDE047), Color(0xFFFFFFFF), Color(0xFFF472B6))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (showLabel) 8.dp else 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Special Premium Badge",
            tint = Color(0xFFFEF08A),
            modifier = Modifier.size(size)
        )
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Special Premium ⭐",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp
) {
    Surface(
        shape = CircleShape,
        color = ReachCyanGlow,
        modifier = modifier.size(size).testTag("verified_badge")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Verified",
                tint = Color.Black,
                modifier = Modifier.size((size.value * 0.7f).dp)
            )
        }
    }
}

@Composable
fun OwnerPremiumBadge(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    showLabel: Boolean = true
) {
    Row(
        modifier = modifier
            .testTag("owner_premium_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFB45309), Color(0xFFD97706), Color(0xFFF59E0B), Color(0xFF8B5CF6))
                )
            )
            .border(
                1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFFDE047), Color(0xFFFCD34D), Color(0xFFFFFFFF))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (showLabel) 8.dp else 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Diamond,
            contentDescription = "Owner • Creator",
            tint = Color(0xFFFEF08A),
            modifier = Modifier.size(size)
        )
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Owner • Creator 👑",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun CreatorBadge(
    modifier: Modifier = Modifier,
    size: Dp = 14.dp,
    showLabel: Boolean = true,
    isOwner: Boolean = false
) {
    Row(
        modifier = modifier
            .testTag("creator_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFFDB2777))
                )
            )
            .border(
                1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xFFC7D2FE), Color(0xFFF472B6), Color(0xFFFDE047))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = if (showLabel) 8.dp else 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = "Creator",
            tint = Color(0xFFFEF08A),
            modifier = Modifier.size(size)
        )
        if (showLabel) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isOwner) "Owner • Creator 👑" else "Creator",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun StreakChip(
    streakDays: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else modifier

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StreakFlame.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, StreakFlame.copy(alpha = 0.6f)),
        modifier = clickableModifier.testTag("streak_chip")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$streakDays Days",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StreakFlameLight
            )
        }
    }
}

@Composable
fun FanFriendButton(
    user: User,
    onToggleFan: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    // Unique Fans and Friends system:
    // User is Fan of creator -> target gains fan
    // If target also fans user -> "Friends 🤝"
    val isFriend = user.isFriend
    val isFan = user.isCurrentUserFan

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFriend -> FriendEmerald
            isFan -> MaterialTheme.colorScheme.surfaceVariant
            else -> StarPrimary
        },
        label = "FanBtnColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isFriend -> Color.White
            isFan -> MaterialTheme.colorScheme.onSurface
            else -> Color.White
        },
        label = "FanBtnTextColor"
    )

    val buttonText = when {
        isFriend -> "Friends 🤝"
        isFan -> "Fan ✓"
        else -> "Be a Fan"
    }

    Button(
        onClick = onToggleFan,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(
            horizontal = if (compact) 10.dp else 16.dp,
            vertical = if (compact) 6.dp else 10.dp
        ),
        modifier = modifier
            .heightIn(min = 40.dp)
            .testTag("fan_friend_button_${user.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isFriend) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
            } else if (!isFan) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = buttonText,
                fontSize = if (compact) 12.sp else 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UserAvatar(
    user: User,
    size: Dp = 44.dp,
    showOnline: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else modifier

    Box(
        modifier = clickableModifier
            .size(size)
            .testTag("user_avatar_${user.id}")
    ) {
        if (!user.avatarUri.isNullOrEmpty()) {
            AsyncImage(
                model = user.avatarUri,
                contentDescription = user.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(StarPrimaryVariant, ReachCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.42f).sp
                )
            }
        }

        if (user.isOwner) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size((size.value * 0.40f).dp)
                    .clip(CircleShape)
                    .background(CosmicDarkBg)
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Diamond,
                    contentDescription = "Owner • Creator",
                    tint = Color(0xFFFEF08A),
                    modifier = Modifier.size((size.value * 0.26f).dp)
                )
            }
        } else if (user.isDeveloper) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size((size.value * 0.38f).dp)
                    .clip(CircleShape)
                    .background(CosmicDarkBg)
                    .border(1.dp, ReachCyanGlow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Code,
                    contentDescription = "Developer of AKASH SOCIAL",
                    tint = ReachCyanGlow,
                    modifier = Modifier.size((size.value * 0.26f).dp)
                )
            }
        } else if (user.vipStarActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size((size.value * 0.42f).dp)
                    .clip(CircleShape)
                    .background(CosmicDarkBg)
                    .border(1.dp, VipGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                PremiumStarBadgeImage(size = (size.value * 0.38f).dp)
            }
        }
    }
}

@Composable
fun ReachBadge(
    reach: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = ReachCyan.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ReachCyan.copy(alpha = 0.4f)),
        modifier = modifier.testTag("reach_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                contentDescription = "Reach",
                tint = ReachCyanGlow,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$reach Reach",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ReachCyanGlow
            )
        }
    }
}
