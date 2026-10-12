package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.ui.theme.*

/**
 * Premium Glassmorphism Login & Registration Screen for AKASH SOCIAL
 * Inspired by modern purple, blue, and magenta gradient aesthetics.
 * Supports basic registration requiring ONLY Username and Password (no mandatory personal info).
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onLoginWithUsernamePassword: (username: String, password: String, rememberMe: Boolean) -> Result<User>,
    onRegisterWithUsernamePassword: (username: String, password: String) -> Result<User>,
    isUsernameAvailable: (username: String) -> Boolean,
    onExploreAsGuest: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onContinueWithPhone: (phone: String, otp: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Auth Mode: 0 = Login, 1 = Create New Account
    var authMode by remember { mutableStateOf(0) }

    // Form inputs
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // State handling
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isForgotPasswordDialogOpen by remember { mutableStateOf(false) }

    // Real-time username validation & availability check
    val cleanUsername = usernameInput.trim().removePrefix("@").replace(" ", "_").lowercase()
    val isUsernameValid = cleanUsername.length >= 3 && cleanUsername.matches("^[a-z0-9_]+$".toRegex())
    val usernameAvailability = remember(cleanUsername, authMode) {
        if (authMode == 1 && cleanUsername.length >= 3) {
            isUsernameAvailable(cleanUsername)
        } else null
    }

    // Professional purple, blue, and magenta gradient background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E0854), // Deep Purple
            Color(0xFF1E1B4B), // Midnight Indigo
            Color(0xFF0F172A), // Dark Slate
            Color(0xFF3B0764), // Rich Magenta
            Color(0xFF111827)  // Deep Charcoal
        )
    )

    // Glassmorphism border brush
    val glassBorderBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFC026D3).copy(alpha = 0.7f), // Bright Magenta
            Color(0xFF6366F1).copy(alpha = 0.8f), // Electric Indigo
            Color(0xFF06B6D4).copy(alpha = 0.7f)  // Cyan glow
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("premium_login_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Decorative glowing background circles for realistic glassmorphism depth
        Box(
            modifier = Modifier
                .offset(x = (-90).dp, y = (-180).dp)
                .size(240.dp)
                .clip(CircleShape)
                .background(Color(0xFF9333EA).copy(alpha = 0.22f))
        )
        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = 140.dp)
                .size(260.dp)
                .clip(CircleShape)
                .background(Color(0xFF2563EB).copy(alpha = 0.20f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Branding Header: Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFC026D3), Color(0xFF6366F1))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AKASH SOCIAL",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "👑",
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "NAGOD CREATOR PLATFORM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ReachCyanGlow,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modern Glassmorphism Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF1E1B4B).copy(alpha = 0.65f), // Translucent glass effect
                border = androidx.compose.foundation.BorderStroke(1.5.dp, glassBorderBrush),
                tonalElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("glassmorphism_login_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Auth Mode Switcher Tab (LOGIN vs CREATE ACCOUNT)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.8f))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (authMode == 0) Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)))
                                    else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable {
                                    authMode = 0
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LOG IN",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (authMode == 0) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (authMode == 1) Brush.horizontalGradient(listOf(Color(0xFFC026D3), Color(0xFF8B5CF6)))
                                    else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                )
                                .clickable {
                                    authMode = 1
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CREATE ACCOUNT",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (authMode == 1) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (authMode == 0) "Welcome Back" else "Create Your Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = if (authMode == 0)
                            "Enter your username and password to log in"
                        else
                            "No phone, email, or personal details required!",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                    )

                    // Error Message Banner
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Field 1: Username
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = {
                            usernameInput = it.removePrefix("@")
                            errorMessage = null
                        },
                        label = { Text("Username") },
                        placeholder = { Text("e.g. akashsingh") },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = StarPrimaryLight)
                        },
                        trailingIcon = {
                            if (authMode == 1 && cleanUsername.isNotEmpty()) {
                                if (usernameAvailability == true) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = "Available", tint = FriendEmerald)
                                } else if (usernameAvailability == false) {
                                    Icon(Icons.Filled.Cancel, contentDescription = "Taken", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StarPrimaryLight,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = StarPrimaryLight,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = StarPrimaryLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_username_field"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    // Username live feedback for registration
                    if (authMode == 1 && cleanUsername.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isUsernameValid) {
                                Text(
                                    text = "At least 3 characters (letters, numbers, underscores only, no @)",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFBBF24)
                                )
                            } else if (usernameAvailability == true) {
                                Text(
                                    text = "✓ Username is available",
                                    fontSize = 11.sp,
                                    color = FriendEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (usernameAvailability == false) {
                                Text(
                                    text = "✗ Username is already taken, choose another",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 2: Password
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = StarPrimaryLight)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StarPrimaryLight,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = StarPrimaryLight,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = StarPrimaryLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_field"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (authMode == 1) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    // Field 3: Confirm Password (Registration mode only)
                    if (authMode == 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = {
                                confirmPasswordInput = it
                                errorMessage = null
                            },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Repeat your password") },
                            leadingIcon = {
                                Icon(Icons.Filled.LockClock, contentDescription = null, tint = StarPrimaryLight)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = StarPrimaryLight,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = StarPrimaryLight,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                cursorColor = StarPrimaryLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_confirm_password_field"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Remember Me & Forgot Password (in Login mode)
                    if (authMode == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = StarPrimary,
                                        uncheckedColor = Color.White.copy(alpha = 0.6f)
                                    )
                                )
                                Text(
                                    text = "Remember Me",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            TextButton(onClick = { isForgotPasswordDialogOpen = true }) {
                                Text(
                                    text = "Forgot Password?",
                                    fontSize = 12.sp,
                                    color = ReachCyanGlow,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prominent LOGIN / CREATE ACCOUNT Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val user = cleanUsername
                            val pass = passwordInput

                            if (user.isBlank()) {
                                errorMessage = "Please enter a valid username."
                                return@Button
                            }
                            if (pass.isBlank()) {
                                errorMessage = "Please enter your password."
                                return@Button
                            }

                            if (authMode == 0) {
                                // Log In
                                isLoading = true
                                val result = onLoginWithUsernamePassword(user, pass, rememberMe)
                                isLoading = false
                                result.fold(
                                    onSuccess = { loggedInUser ->
                                        Toast.makeText(context, "Welcome back, ${loggedInUser.name}!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess(loggedInUser)
                                    },
                                    onFailure = { err ->
                                        errorMessage = err.message ?: "Authentication failed. Please check credentials."
                                    }
                                )
                            } else {
                                // Create New Account
                                if (pass.length < 6) {
                                    errorMessage = "Password must be at least 6 characters long."
                                    return@Button
                                }
                                if (pass != confirmPasswordInput) {
                                    errorMessage = "Passwords do not match. Please verify."
                                    return@Button
                                }
                                if (usernameAvailability == false) {
                                    errorMessage = "Username '$user' is already taken. Please choose another or log in."
                                    return@Button
                                }

                                isLoading = true
                                val result = onRegisterWithUsernamePassword(user, pass)
                                isLoading = false
                                result.fold(
                                    onSuccess = { createdUser ->
                                        Toast.makeText(context, "Account created! Welcome, ${createdUser.name}!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess(createdUser)
                                    },
                                    onFailure = { err ->
                                        errorMessage = err.message ?: "Registration failed. Try another username."
                                    }
                                )
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (authMode == 0) StarPrimary else Color(0xFFC026D3)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (authMode == 0) "LOGIN" else "CREATE ACCOUNT & LOGIN",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary switch option
                    TextButton(
                        onClick = {
                            authMode = if (authMode == 0) 1 else 0
                            errorMessage = null
                        },
                        modifier = Modifier.testTag("toggle_auth_mode_button")
                    ) {
                        Text(
                            text = if (authMode == 0)
                                "Don't have an account? Create New Account"
                            else
                                "Already have an account? Log In",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // Explore as Guest Quick Access
                    OutlinedButton(
                        onClick = onExploreAsGuest,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ReachCyanGlow.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ReachCyanGlow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("login_explore_guest_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TravelExplore,
                            contentDescription = null,
                            tint = ReachCyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explore as Guest",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer / Security note
            Text(
                text = "Protected with secure SHA-256 client & server hashing.\nPreserves all creator posts, Reels, and streaks.",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Forgot Password Guidance Dialog
    if (isForgotPasswordDialogOpen) {
        AlertDialog(
            onDismissRequest = { isForgotPasswordDialogOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LockReset, contentDescription = null, tint = StarPrimaryLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Password Recovery", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Because AKASH SOCIAL allows registration without mandatory personal details (like phone or email), password recovery is available through:\n\n" +
                                "1. Optional Linked Recovery: If you previously linked an optional email or phone number in your Profile settings.\n\n" +
                                "2. Multi-Account Switcher: You can switch to any other saved profile on this device or create a new creator account immediately.\n\n" +
                                "3. Owner Support: Contact verified support at support@akashsocial.com or reach out to @akash_social_application.",
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { isForgotPasswordDialogOpen = false }) {
                    Text("Understood")
                }
            }
        )
    }
}
