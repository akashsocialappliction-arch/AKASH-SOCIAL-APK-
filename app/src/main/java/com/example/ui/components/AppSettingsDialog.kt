package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommunityPopupsFrequency
import com.example.model.User
import com.example.ui.theme.*

/**
 * App Settings dialog supporting Manual Update Check, Community Activity Pop-up controls,
 * Account Security, and Multi-Profile management.
 */
@Composable
fun AppSettingsDialog(
    currentUser: User,
    isCommunityPopupsEnabled: Boolean,
    communityFrequency: CommunityPopupsFrequency,
    onSetCommunityPopupsConsent: (Boolean, CommunityPopupsFrequency) -> Unit,
    onCheckForUpdates: () -> Unit,
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    var popupsEnabled by remember { mutableStateOf(isCommunityPopupsEnabled) }
    var selectedFreq by remember { mutableStateOf(if (communityFrequency == CommunityPopupsFrequency.OFF) CommunityPopupsFrequency.NORMAL else communityFrequency) }
    var showOptionalSecurityInfo by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Settings, contentDescription = null, tint = StarPrimaryLight)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AKASH SOCIAL Settings",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Update Checker
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Version & Updates",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Current: v2.0 (Build 2)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = {
                                    onDismiss()
                                    onCheckForUpdates()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("settings_check_update_button")
                            ) {
                                Icon(Icons.Filled.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Check for Updates", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section 2: Community Activity Pop-ups
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Community Activity Pop-ups",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Show non-blocking notifications about new community creators and creative milestones",
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = popupsEnabled,
                                onCheckedChange = { checked ->
                                    popupsEnabled = checked
                                    onSetCommunityPopupsConsent(checked, selectedFreq)
                                },
                                modifier = Modifier.testTag("community_popups_switch")
                            )
                        }

                        if (popupsEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Frequency Control:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    CommunityPopupsFrequency.LOW to "Low (1-2/hr)",
                                    CommunityPopupsFrequency.NORMAL to "Normal (2-3/15m)"
                                ).forEach { (freq, label) ->
                                    val isSelected = selectedFreq == freq
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedFreq = freq
                                            onSetCommunityPopupsConsent(true, freq)
                                        },
                                        label = { Text(label, fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: Account & Security
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Active Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${currentUser.name} (${currentUser.username.removePrefix("@")})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StarPrimaryLight
                        )
                        Text(
                            text = "Auth Type: ${currentUser.authProvider.replaceFirstChar { it.uppercase() }}",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { showOptionalSecurityInfo = !showOptionalSecurityInfo },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (showOptionalSecurityInfo) "Hide Security Options ▲" else "Optional Extra Security & Recovery ▼",
                                fontSize = 12.sp,
                                color = ReachCyanGlow
                            )
                        }

                        if (showOptionalSecurityInfo) {
                            Text(
                                text = "Basic accounts use only Username & Password. For extra security, you can link an email or phone number in your Profile for instant self-service recovery.",
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Section 4: Account Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onSwitchAccount()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Switch Profile", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log Out", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close")
            }
        }
    )
}
