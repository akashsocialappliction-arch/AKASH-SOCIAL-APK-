package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UpdateManifest
import com.example.ui.theme.*

/**
 * Dialog shown when a verified newer version of AKASH SOCIAL is available.
 * Complies with Section 6 of requirements.
 */
@Composable
fun UpdateAvailableDialog(
    manifest: UpdateManifest,
    onDownload: (String) -> Unit,
    onLater: () -> Unit,
    onDismiss: () -> Unit
) {
    var showInstructionsDialog by remember { mutableStateOf(false) }

    if (showInstructionsDialog) {
        InstallationInstructionsDialog(
            apkUrl = manifest.apkDownloadUrl,
            onDismiss = {
                showInstructionsDialog = false
                onDismiss()
            }
        )
    }

    AlertDialog(
        onDismissRequest = {
            if (!manifest.isMandatory) onLater()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Update Available 🚀",
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "A new version of AKASH SOCIAL is ready! Update now to enjoy the latest improvements, features, and bug fixes.",
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Version Badge Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Latest Version",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "v${manifest.latestVersionName} (Build ${manifest.latestVersionCode})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarPrimaryLight
                        )
                    }
                }

                // Release Notes
                if (manifest.releaseNotes.isNotBlank()) {
                    Text(
                        text = "What's New:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = manifest.releaseNotes,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (manifest.isMandatory) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ This is a required security update to ensure compatibility with modern Android features.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDownload(manifest.apkDownloadUrl)
                    showInstructionsDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("download_update_button")
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Download Update", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (!manifest.isMandatory) {
                TextButton(
                    onClick = onLater,
                    modifier = Modifier.testTag("update_later_button")
                ) {
                    Text("Later", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    )
}

/**
 * Step-by-step Android APK Installation Guidance Dialog
 * Complies with Section 3 and 6 of update requirements.
 */
@Composable
fun InstallationInstructionsDialog(
    apkUrl: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Security, contentDescription = null, tint = ReachCyanGlow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Installation Instructions", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Follow these simple steps to install the new update without losing your profile, posts, or account progress:",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val steps = listOf(
                    "1. Tap Download Update to download the latest AKASH SOCIAL APK.",
                    "2. Wait until the download finishes.",
                    "3. Open the downloaded APK from the phone’s Downloads folder or File Manager.",
                    "4. If Android asks for permission to install apps from that source, allow it only for the trusted browser or file manager you are using.",
                    "5. Tap Install and wait for the installation to finish.",
                    "6. Open AKASH SOCIAL again."
                )

                steps.forEach { step ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = step,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("🛡️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Android Security Notice: Android may display a standard security prompt for APK files. You can safely install only APKs downloaded from our official AKASH SOCIAL verified link. Do NOT uninstall your current app—installing over it automatically preserves all your data.",
                            fontSize = 11.sp,
                            color = Color(0xFFF59E0B),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("I Understand")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openApkDownloadInBrowser(context, apkUrl)
                }
            ) {
                Text("Re-open Link")
            }
        }
    )
}

/**
 * Dialog shown when manual update check confirms the app is on the latest version.
 */
@Composable
fun UpToDateDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = FriendEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Up to Date", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = "You're using the latest version of AKASH SOCIAL.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("OK")
            }
        }
    )
}

/**
 * Dialog shown when update check fails due to network or endpoint error.
 */
@Composable
fun UpdateErrorDialog(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Check Failed", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = if (errorMessage.isNotBlank()) errorMessage
                else "Couldn't check for updates. Please check your internet connection and try again.",
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

/**
 * Helper to open APK download link in external browser or download manager.
 */
fun openApkDownloadInBrowser(context: Context, downloadUrl: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        Toast.makeText(context, "Opening verified download link...", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, "Download link: $downloadUrl", Toast.LENGTH_LONG).show()
    }
}
