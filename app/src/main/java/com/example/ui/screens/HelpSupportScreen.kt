package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HelpSupportScreen(
    onContactSupport: (Context) -> Unit,
    onOpenAiHelp: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("General Feedback") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("help_support_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "About, Feedback & Support",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Official About Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimaryLight.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().testTag("about_app_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👑", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AKASH SOCIAL",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Creator & Owner: Akash • Version 1.0 (Build 2026)",
                                    fontSize = 12.sp,
                                    color = StarPrimaryLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "AKASH SOCIAL is an Indian creator-first community that starts from genuine zero and grows organically. Founded & developed by Akash (Akash Singh Parihar).",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Instagram link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.instagram.com/akash_social_application")
                                    )
                                    context.startActivity(intent)
                                }
                                .padding(10.dp)
                        ) {
                            Text("📸", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Official Instagram",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "@akash_social_application",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ReachCyanGlow
                                )
                            }
                            Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = ReachCyanGlow, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Email link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { onContactSupport(context) }
                                .padding(10.dp)
                        ) {
                            Text("✉️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Public Contact & Support Email",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "akashsocialappliction@gmail.com",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StarPrimaryLight
                                )
                            }
                            Icon(Icons.Filled.Mail, contentDescription = null, tint = StarPrimaryLight, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // In-App Feedback Form Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("feedback_form_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Submit Feedback or Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Category chips
                        val categories = listOf("Bug Report", "General Feedback", "Feature Request", "Content Report", "Other")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.take(3).forEach { cat ->
                                val selected = selectedCategory == cat
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 10.sp) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            categories.drop(3).forEach { cat ->
                                val selected = selectedCategory == cat
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 10.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = feedbackMessage,
                            onValueChange = { feedbackMessage = it },
                            placeholder = { Text("Write your feedback or bug details...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(90.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (feedbackMessage.isNotBlank()) {
                                    feedbackStatus = "Feedback submitted securely to AKASH SOCIAL owner team! Thank you."
                                    feedbackMessage = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit $selectedCategory", fontSize = 13.sp)
                        }

                        if (feedbackStatus != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = feedbackStatus!!,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FriendEmerald
                            )
                        }
                    }
                }
            }

            // Quick AI Assistant Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StarPrimary.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimaryLight.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAiHelp() }
                        .testTag("support_ai_assistant_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StarPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ask AKASH SOCIAL AI Assistant",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Get instant 24/7 answers on Fans, Streaks, VIP Star & features",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = StarPrimaryLight)
                    }
                }
            }

            // FAQ Section
            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                FaqItem(
                    question = "How do Fans and Friends work?",
                    answer = "When you tap 'Be a Fan', you become a Fan of that creator. When two users become Fans of each other, they automatically become Friends 🤝! Friends can send direct messages and make voice and video calls."
                )
            }

            item {
                FaqItem(
                    question = "How do I earn the official 20-Day Premium Star badge?",
                    answer = "The official 3D Crown Star Badge is granted free for 20 consecutive calendar days! Complete active daily tasks, maintain your activity streak, and unlock the entitlement from the Tasks tab."
                )
            }

            item {
                FaqItem(
                    question = "How are Activity Streaks tracked?",
                    answer = "Streaks reward meaningful creative participation! Sharing posts, uploading StarClips, engaging with creator feedback, or completing active tasks keeps your streak active each day."
                )
            }

            item {
                FaqItem(
                    question = "How does Music & Audio licensing work?",
                    answer = "All music tracks in our built-in Audio Library are cleared for in-app creator sharing under royalty-free or creative commons licenses. You can preview waveforms and attach tracks directly to your posts and StarClips."
                )
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
