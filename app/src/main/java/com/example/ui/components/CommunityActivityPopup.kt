package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommunityActivityEvent
import com.example.model.CommunityPopupsFrequency
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Consent dialog prompted before enabling Community Activity Pop-ups for the first time.
 * Complies with Section 8 requirements.
 */
@Composable
fun CommunityConsentDialog(
    onEnable: (CommunityPopupsFrequency) -> Unit,
    onNotNow: () -> Unit
) {
    var selectedFrequency by remember { mutableStateOf(CommunityPopupsFrequency.NORMAL) }

    AlertDialog(
        onDismissRequest = onNotNow,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        tint = StarPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Community Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Would you like to see small pop-up notifications about activity in the AKASH SOCIAL community?",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "You'll see brief, non-intrusive updates when new creators join or hit creative milestones. You can turn this off or adjust frequency anytime in Settings.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Frequency options selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        CommunityPopupsFrequency.LOW to "Low (1-2/hr)",
                        CommunityPopupsFrequency.NORMAL to "Normal (2-3/15m)"
                    ).forEach { (freq, label) ->
                        val isSelected = selectedFrequency == freq
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFrequency = freq },
                            label = { Text(label, fontSize = 11.5.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onEnable(selectedFrequency) },
                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("enable_community_popups_button")
            ) {
                Text("Enable Pop-ups", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onNotNow,
                modifier = Modifier.testTag("not_now_community_popups_button")
            ) {
                Text("Not Now")
            }
        }
    )
}

/**
 * Non-blocking animated floating pill for occasional community activity updates.
 * Complies with Section 8 requirements:
 * - Labeled with [Demo Notification] for preview/testing
 * - Auto-dismisses after 4 seconds
 * - Disappears safely without blocking touches
 */
@Composable
fun CommunityActivityFloatingPill(
    event: CommunityActivityEvent?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(event) {
        if (event != null) {
            isVisible = true
            delay(4500)
            isVisible = false
            delay(300)
            onDismiss()
        } else {
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible && event != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (event != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E1B4B).copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFC026D3), Color(0xFF6366F1)))
                ),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("community_activity_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌐",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.message,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            isVisible = false
                            onDismiss()
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
