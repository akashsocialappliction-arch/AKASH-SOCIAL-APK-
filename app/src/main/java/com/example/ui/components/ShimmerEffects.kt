package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates an animated linear shimmer brush that smoothly sweeps across the component.
 * Uses MaterialTheme dynamic colors to gracefully match both Dark and Light themes.
 */
@Composable
fun shimmerBrush(
    targetValue: Float = 1200f,
    durationMillis: Int = 1100
): Brush {
    val transition = rememberInfiniteTransition(label = "ShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    val isDark = MaterialTheme.colorScheme.surface.red < 0.5f
    val baseColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val highlightColor = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)

    val shimmerColors = listOf(
        baseColor.copy(alpha = 0.85f),
        highlightColor.copy(alpha = 0.95f),
        baseColor.copy(alpha = 0.85f)
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 350f, y = translateAnim - 350f),
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

/**
 * Reusable Shimmer placeholder box with customizable size, shape, and brush.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    brush: Brush = shimmerBrush(),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * Skeleton placeholder for the entire Home Feed (Stories bar + Post cards).
 */
@Composable
fun FeedSkeletonLoading(
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .testTag("feed_skeleton_loading")
    ) {
        // 1. Stories Bar Skeleton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            repeat(5) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(brush)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .width(48.dp)
                            .height(10.dp),
                        brush = brush,
                        shape = RoundedCornerShape(5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Quick Action Skeleton (Go Live card placeholder)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(38.dp),
                    brush = brush,
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerBox(
                        modifier = Modifier
                            .width(140.dp)
                            .height(14.dp),
                        brush = brush
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .width(200.dp)
                            .height(10.dp),
                        brush = brush
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                ShimmerBox(
                    modifier = Modifier
                        .width(72.dp)
                        .height(32.dp),
                    brush = brush,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Post Cards Skeleton
        repeat(2) {
            PostCardSkeleton(brush = brush)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Individual Post Card Skeleton showing realistic layout hierarchy.
 */
@Composable
fun PostCardSkeleton(
    brush: Brush = shimmerBrush(),
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("post_card_skeleton")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar, Name, Handle, Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    modifier = Modifier.size(44.dp),
                    brush = brush,
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShimmerBox(
                            modifier = Modifier
                                .width(120.dp)
                                .height(14.dp),
                            brush = brush
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        ShimmerBox(
                            modifier = Modifier.size(14.dp),
                            brush = brush,
                            shape = CircleShape
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .width(85.dp)
                            .height(11.dp),
                        brush = brush
                    )
                }
                ShimmerBox(
                    modifier = Modifier
                        .width(45.dp)
                        .height(12.dp),
                    brush = brush
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Caption lines
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(13.dp),
                brush = brush
            )
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.70f)
                    .height(13.dp),
                brush = brush
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Media Rectangle Container Placeholder
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                brush = brush,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Bar (Like, Comment, Share, Reach)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ShimmerBox(modifier = Modifier.size(26.dp), brush = brush, shape = CircleShape)
                    ShimmerBox(modifier = Modifier.size(26.dp), brush = brush, shape = CircleShape)
                    ShimmerBox(modifier = Modifier.size(26.dp), brush = brush, shape = CircleShape)
                }
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(22.dp),
                    brush = brush,
                    shape = RoundedCornerShape(11.dp)
                )
            }
        }
    }
}

/**
 * Skeleton placeholder for the Profile Screen (Header, Stats, Analytics, Tabs, Posts).
 */
@Composable
fun ProfileSkeletonLoading(
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Header Card Skeleton
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_skeleton_header")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Avatar & Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShimmerBox(
                        modifier = Modifier.size(68.dp),
                        brush = brush,
                        shape = CircleShape
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(3) {
                            ShimmerBox(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(24.dp),
                                brush = brush,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name & Handle
                ShimmerBox(
                    modifier = Modifier
                        .width(170.dp)
                        .height(20.dp),
                    brush = brush
                )
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerBox(
                    modifier = Modifier
                        .width(100.dp)
                        .height(13.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bio
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(12.dp),
                    brush = brush
                )
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(12.dp),
                    brush = brush
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Primary Stats Row (Fans, Friends, Posts, Reach)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(4) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(16.dp),
                                    brush = brush
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ShimmerBox(
                                    modifier = Modifier
                                        .width(44.dp)
                                        .height(10.dp),
                                    brush = brush
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        ShimmerBox(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            brush = brush,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // 2. Creator Analytics Card Skeleton
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                ShimmerBox(
                    modifier = Modifier
                        .width(160.dp)
                        .height(16.dp),
                    brush = brush
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(4) {
                        Column {
                            ShimmerBox(modifier = Modifier.width(42.dp).height(10.dp), brush = brush)
                            Spacer(modifier = Modifier.height(4.dp))
                            ShimmerBox(modifier = Modifier.width(32.dp).height(14.dp), brush = brush)
                        }
                    }
                }
            }
        }

        // 3. Tabs Skeleton (Posts, Clips, Analytics)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    brush = brush,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // 4. Content Items Skeleton
        repeat(2) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ShimmerBox(modifier = Modifier.width(60.dp).height(12.dp), brush = brush)
                        ShimmerBox(modifier = Modifier.width(50.dp).height(14.dp), brush = brush, shape = RoundedCornerShape(7.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp), brush = brush)
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f).height(11.dp), brush = brush)
                }
            }
        }
    }
}
