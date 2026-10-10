package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

/**
 * Requirement 3: Welcome Screen and App Startup
 * Heading: Welcome to AKASH SOCIAL
 * Tagline: Your World. Your People. Your Moments.
 * Supporting text: Connect with people. Share your creativity. Create your own moments.
 * Provides working Get Started and Log In options.
 */
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLogin: (username: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        CosmicDarkBg,
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("welcome_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Emblem
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                StarPrimary,
                                StarPrimaryVariant,
                                CosmicDarkSurface
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(StarPrimaryLight, ReachCyanGlow)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_akash_social_logo),
                    contentDescription = "AKASH SOCIAL Logo",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Heading & Branding
            Text(
                text = "Welcome to AKASH SOCIAL",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 0.25.sp,
                modifier = Modifier.testTag("welcome_heading")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your World. Your People. Your Moments.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VipGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("welcome_tagline")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Supporting Text
            Text(
                text = "Connect with people. Share your creativity. Create your own moments.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("welcome_supporting_text")
            )

            Spacer(modifier = Modifier.height(36.dp))

            AnimatedVisibility(
                visible = isLoginMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicDarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Log In to Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )

                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = {
                                usernameInput = it
                                errorMessage = null
                            },
                            label = { Text("Username or Handle") },
                            placeholder = { Text("@akash_dev or username") },
                            leadingIcon = {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = StarPrimaryLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input")
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Passcode (Optional)") },
                            leadingIcon = {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = StarPrimaryLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (usernameInput.isBlank()) {
                                    errorMessage = "Please enter your username"
                                } else {
                                    onLogin(usernameInput.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_submit_button")
                        ) {
                            Text("Log In", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        TextButton(
                            onClick = { isLoginMode = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Back to Welcome", color = StarPrimaryLight, fontSize = 13.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !isLoginMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Get Started Button -> Opens registration/direct setup
                    Button(
                        onClick = onGetStarted,
                        colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("welcome_get_started_button")
                    ) {
                        Text(
                            text = "Get Started",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Log In Button -> Opens authentication form
                    OutlinedButton(
                        onClick = { isLoginMode = true },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimaryLight.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("welcome_login_button")
                    ) {
                        Text(
                            text = "Log In",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
