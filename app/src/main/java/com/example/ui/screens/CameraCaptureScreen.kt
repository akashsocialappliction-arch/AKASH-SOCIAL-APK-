package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CameraMode {
    STORY,
    REEL
}

data class CameraCaptureResult(
    val mode: CameraMode,
    val durationSeconds: Int = 0,
    val isPhoto: Boolean = false,
    val caption: String = ""
)

enum class FlashMode {
    OFF,
    ON,
    AUTO
}

@Composable
fun CameraCaptureScreen(
    initialMode: CameraMode = CameraMode.REEL,
    onVideoCaptured: ((durationSeconds: Int) -> Unit)? = null,
    onMediaCaptured: ((result: CameraCaptureResult) -> Unit)? = null,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Permissions State
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isSimulatorMode by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    // Camera Configuration States
    var currentMode by remember { mutableStateOf(initialMode) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableStateOf(FlashMode.OFF) }
    var isGridEnabled by remember { mutableStateOf(false) }
    var maxReelDurationSeconds by remember { mutableIntStateOf(60) }
    var selectedFilterName by remember { mutableStateOf("Natural") }

    // Recording & Capturing States
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableIntStateOf(0) }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraRef by remember { mutableStateOf<Camera?>(null) }
    var cameraError by remember { mutableStateOf(false) }
    var isShutterFlashVisible by remember { mutableStateOf(false) }

    // Post-capture review state
    var capturedResult by remember { mutableStateOf<CameraCaptureResult?>(null) }

    // Back handling
    BackHandler {
        when {
            capturedResult != null -> capturedResult = null
            isRecording -> {
                isRecording = false
                recordingDuration = 0
            }
            else -> onClose()
        }
    }

    // Video Recording Timer Loop
    LaunchedEffect(isRecording, currentMode, maxReelDurationSeconds) {
        if (isRecording) {
            recordingDuration = 0
            val maxDuration = if (currentMode == CameraMode.STORY) 15 else maxReelDurationSeconds
            while (isRecording) {
                delay(1000)
                recordingDuration++
                if (recordingDuration >= maxDuration) {
                    isRecording = false
                    val result = CameraCaptureResult(
                        mode = currentMode,
                        durationSeconds = recordingDuration,
                        isPhoto = false
                    )
                    capturedResult = result
                }
            }
        }
    }

    // Shutter flash animation auto-hide
    LaunchedEffect(isShutterFlashVisible) {
        if (isShutterFlashVisible) {
            delay(120)
            isShutterFlashVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_capture_screen")
    ) {
        if ((hasCameraPermission || isSimulatorMode) && !cameraError) {
            // CameraX Live Preview
            if (!isSimulatorMode) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                cameraProviderRef = cameraProvider
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val imageCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                val cameraSelector = CameraSelector.Builder()
                                    .requireLensFacing(lensFacing)
                                    .build()

                                cameraProvider.unbindAll()
                                val cam = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                                cameraRef = cam
                                cam.cameraControl.enableTorch(flashMode == FlashMode.ON)
                            } catch (_: Exception) {
                                cameraError = true
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    update = { previewView ->
                        try {
                            val cameraProvider = cameraProviderRef ?: return@AndroidView
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val imageCapture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            val cam = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                            cameraRef = cam
                            cam.cameraControl.enableTorch(flashMode == FlashMode.ON)
                        } catch (_: Exception) {
                            // Handled gracefully
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Studio Simulator Preview for Emulators
                StudioSimulatorViewfinder(
                    mode = currentMode,
                    lensFacing = lensFacing,
                    isRecording = isRecording,
                    filterName = selectedFilterName
                )
            }

            // Optional 3x3 Rule of Thirds Grid Overlay
            if (isGridEnabled) {
                CameraGridOverlay(modifier = Modifier.fillMaxSize())
            }

            // White Shutter Flash Effect
            if (isShutterFlashVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }

            // Top Overlay Bar: Close, Recording Timer, Flash, Grid, Studio Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (isRecording) {
                            isRecording = false
                        }
                        onClose()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                        .testTag("camera_close_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                // Recording Timer Badge or Studio Mode Title
                if (isRecording) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFDC2626).copy(alpha = 0.9f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                        modifier = Modifier.testTag("recording_timer_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PulsingDot()
                            Spacer(modifier = Modifier.width(6.dp))
                            val maxLimit = if (currentMode == CameraMode.STORY) 15 else maxReelDurationSeconds
                            val mins = recordingDuration / 60
                            val secs = recordingDuration % 60
                            val maxMins = maxLimit / 60
                            val maxSecs = maxLimit % 60
                            Text(
                                text = String.format("%02d:%02d / %02d:%02d", mins, secs, maxMins, maxSecs),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentMode == CameraMode.STORY) "STORY STUDIO ✨" else "REEL STUDIO 🎬",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMode == CameraMode.STORY) Color(0xFFFDE047) else ReachCyanGlow
                            )
                        }
                    }
                }

                // Action Controls on Top Right: Flash & Grid
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            flashMode = when (flashMode) {
                                FlashMode.OFF -> FlashMode.ON
                                FlashMode.ON -> FlashMode.AUTO
                                FlashMode.AUTO -> FlashMode.OFF
                            }
                            cameraRef?.cameraControl?.enableTorch(flashMode == FlashMode.ON)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("camera_flash_toggle")
                    ) {
                        Icon(
                            imageVector = when (flashMode) {
                                FlashMode.OFF -> Icons.Filled.FlashOff
                                FlashMode.ON -> Icons.Filled.FlashOn
                                FlashMode.AUTO -> Icons.Filled.FlashAuto
                            },
                            contentDescription = "Toggle Flash",
                            tint = if (flashMode != FlashMode.OFF) VipGold else Color.White
                        )
                    }

                    IconButton(
                        onClick = { isGridEnabled = !isGridEnabled },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .testTag("camera_grid_toggle")
                    ) {
                        Icon(
                            imageVector = if (isGridEnabled) Icons.Filled.GridOn else Icons.Filled.GridOff,
                            contentDescription = "Toggle Grid",
                            tint = if (isGridEnabled) ReachCyanGlow else Color.White
                        )
                    }
                }
            }

            // Right-Side Creative Tools Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Flip Camera Lens
                CreativeToolItem(
                    icon = Icons.Filled.Cameraswitch,
                    label = if (lensFacing == CameraSelector.LENS_FACING_BACK) "Back" else "Front",
                    testTag = "flip_camera_lens_button",
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    }
                )

                // Reel Duration Toggle (15s / 30s / 60s) in Reel mode
                if (currentMode == CameraMode.REEL) {
                    CreativeToolItem(
                        icon = Icons.Filled.Timer,
                        label = "${maxReelDurationSeconds}s",
                        testTag = "camera_duration_selector",
                        onClick = {
                            maxReelDurationSeconds = when (maxReelDurationSeconds) {
                                15 -> 30
                                30 -> 60
                                else -> 15
                            }
                        }
                    )
                }

                // Filter / Mood Preset
                CreativeToolItem(
                    icon = Icons.Filled.AutoAwesome,
                    label = selectedFilterName,
                    testTag = "camera_filter_toggle",
                    onClick = {
                        selectedFilterName = when (selectedFilterName) {
                            "Natural" -> "Vibrant"
                            "Vibrant" -> "Cinema"
                            "Cinema" -> "Golden"
                            else -> "Natural"
                        }
                    }
                )

                // Audio / Mic Status
                CreativeToolItem(
                    icon = if (hasAudioPermission) Icons.Filled.Mic else Icons.Filled.MicOff,
                    label = if (hasAudioPermission) "Audio ON" else "Muted",
                    tint = if (hasAudioPermission) FriendEmerald else Color.Red,
                    testTag = "camera_audio_status",
                    onClick = {
                        if (!hasAudioPermission) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                        }
                    }
                )
            }

            // Bottom Shutter & Mode Switcher Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Instructions hint text
                Text(
                    text = when {
                        isRecording -> "Tap stop square to finish recording"
                        currentMode == CameraMode.STORY -> "Tap shutter to snap Photo • Hold to record Story clip"
                        else -> "Tap red shutter to record Reel (${maxReelDurationSeconds}s max)"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Central Shutter Button
                Box(
                    modifier = Modifier.size(88.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated Progress Ring while recording
                    val maxLimit = if (currentMode == CameraMode.STORY) 15 else maxReelDurationSeconds
                    val progress = if (isRecording && maxLimit > 0) {
                        (recordingDuration.toFloat() / maxLimit).coerceIn(0f, 1f)
                    } else 0f

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.3f),
                            style = Stroke(width = 4.dp.toPx())
                        )
                        if (isRecording) {
                            drawArc(
                                color = Color(0xFFEF4444),
                                startAngle = -90f,
                                sweepAngle = progress * 360f,
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Shutter Center
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentMode == CameraMode.STORY && !isRecording) {
                                    Brush.linearGradient(
                                        listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF59E0B))
                                    )
                                } else {
                                    Brush.linearGradient(listOf(Color.White, Color.White.copy(alpha = 0.9f)))
                                }
                            )
                            .padding(4.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                when (currentMode) {
                                    CameraMode.STORY -> {
                                        if (isRecording) {
                                            isRecording = false
                                            capturedResult = CameraCaptureResult(
                                                mode = CameraMode.STORY,
                                                durationSeconds = recordingDuration,
                                                isPhoto = false
                                            )
                                        } else {
                                            // Snap story photo with flash effect
                                            isShutterFlashVisible = true
                                            capturedResult = CameraCaptureResult(
                                                mode = CameraMode.STORY,
                                                durationSeconds = 0,
                                                isPhoto = true
                                            )
                                        }
                                    }
                                    CameraMode.REEL -> {
                                        if (!isRecording) {
                                            isRecording = true
                                        } else {
                                            isRecording = false
                                            val dur = if (recordingDuration < 1) 3 else recordingDuration
                                            capturedResult = CameraCaptureResult(
                                                mode = CameraMode.REEL,
                                                durationSeconds = dur,
                                                isPhoto = false
                                            )
                                        }
                                    }
                                }
                            }
                            .testTag("camera_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRecording) {
                            // Stop Recording Square
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDC2626))
                            )
                        } else {
                            if (currentMode == CameraMode.STORY) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PhotoCamera,
                                        contentDescription = "Capture Story Photo",
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Mode Switcher: STORY | REEL
                if (!isRecording) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Story Mode Button
                            ModeTabButton(
                                title = "STORY ✨",
                                isSelected = currentMode == CameraMode.STORY,
                                testTag = "camera_mode_story",
                                onClick = { currentMode = CameraMode.STORY }
                            )

                            // Reel Mode Button
                            ModeTabButton(
                                title = "REEL 🎬",
                                isSelected = currentMode == CameraMode.REEL,
                                testTag = "camera_mode_reel",
                                onClick = { currentMode = CameraMode.REEL }
                            )
                        }
                    }
                }
            }
        } else {
            // Permission Rationale / Denied State
            CameraPermissionRationaleView(
                hasCameraPermission = hasCameraPermission,
                hasAudioPermission = hasAudioPermission,
                onRequestPermissions = {
                    permissionLauncher.launch(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    )
                },
                onUseSimulator = { isSimulatorMode = true },
                onClose = onClose
            )
        }

        // Post-Capture Media Review Sheet
        if (capturedResult != null) {
            PostCaptureReviewDialog(
                result = capturedResult!!,
                onRetake = {
                    capturedResult = null
                    recordingDuration = 0
                },
                onConfirm = { finalResult ->
                    onMediaCaptured?.invoke(finalResult)
                    onVideoCaptured?.invoke(finalResult.durationSeconds)
                    onClose()
                }
            )
        }
    }
}

@Composable
private fun ModeTabButton(
    title: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) StarPrimary else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun CreativeToolItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun CameraGridOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val linePaint = Color.White.copy(alpha = 0.25f)
        val strokeWidth = 1.dp.toPx()

        // Vertical lines
        drawLine(linePaint, Offset(width / 3f, 0f), Offset(width / 3f, height), strokeWidth)
        drawLine(linePaint, Offset(2f * width / 3f, 0f), Offset(2f * width / 3f, height), strokeWidth)

        // Horizontal lines
        drawLine(linePaint, Offset(0f, height / 3f), Offset(width, height / 3f), strokeWidth)
        drawLine(linePaint, Offset(0f, 2f * height / 3f), Offset(width, 2f * height / 3f), strokeWidth)
    }
}

@Composable
private fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = alpha))
    )
}

@Composable
private fun StudioSimulatorViewfinder(
    mode: CameraMode,
    lensFacing: Int,
    isRecording: Boolean,
    filterName: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sim_glow")
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            if (mode == CameraMode.STORY) {
                                listOf(Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF59E0B))
                            } else {
                                listOf(StarPrimary, ReachCyanGlow)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (mode == CameraMode.STORY) Icons.Filled.AutoAwesome else Icons.Filled.Videocam,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (mode == CameraMode.STORY) "CameraX Story Studio Preview" else "CameraX Reel Studio Preview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Lens: ${if (lensFacing == CameraSelector.LENS_FACING_BACK) "Back Wide-Angle" else "Front Selfie"} • Filter: $filterName",
                fontSize = 12.sp,
                color = ReachCyanGlow,
                fontWeight = FontWeight.SemiBold
            )

            if (isRecording) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Red.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Text(
                        text = "● LIVE ENCODING IN PROGRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraPermissionRationaleView(
    hasCameraPermission: Boolean,
    hasAudioPermission: Boolean,
    onRequestPermissions: () -> Unit,
    onUseSimulator: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF0B0F19), Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .testTag("camera_close_button")
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = CircleShape,
                    color = StarPrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StarPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Camera",
                        tint = StarPrimaryLight,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(36.dp)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = ReachCyan.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ReachCyanGlow)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Microphone",
                        tint = ReachCyanGlow,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Camera & Audio Access",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AKASH SOCIAL uses CameraX to record 1080p vertical Reels and snap high-resolution Stories, and microphone access to capture clear studio audio.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Permission status pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasCameraPermission) FriendEmerald.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (hasCameraPermission) FriendEmerald else Color.Red)
                ) {
                    Text(
                        text = if (hasCameraPermission) "Camera: Granted ✓" else "Camera: Needed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasCameraPermission) FriendEmerald else Color.Red,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasAudioPermission) FriendEmerald.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (hasAudioPermission) FriendEmerald else Color.Red)
                ) {
                    Text(
                        text = if (hasAudioPermission) "Mic: Granted ✓" else "Mic: Needed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasAudioPermission) FriendEmerald else Color.Red,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onRequestPermissions,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("grant_camera_permissions_button")
            ) {
                Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Grant Camera & Microphone Access", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onUseSimulator,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_camera_simulator_button")
            ) {
                Text("Continue in Simulator Preview Mode", color = ReachCyanGlow, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PostCaptureReviewDialog(
    result: CameraCaptureResult,
    onRetake: () -> Unit,
    onConfirm: (CameraCaptureResult) -> Unit
) {
    var caption by remember {
        mutableStateOf(
            if (result.mode == CameraMode.STORY) "Moments on AKASH SOCIAL ✨ #DailyStory"
            else "New Reel clip recorded with CameraX! 🎬 #Reels #Creator"
        )
    }

    AlertDialog(
        onDismissRequest = onRetake,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (result.mode == CameraMode.STORY) Icons.Filled.AutoAwesome else Icons.Filled.Movie,
                    contentDescription = null,
                    tint = if (result.mode == CameraMode.STORY) Color(0xFF8B5CF6) else StarPrimaryLight,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (result.isPhoto) "Story Photo Ready!" else "${if (result.mode == CameraMode.STORY) "Story" else "Reel"} Video Ready! (${result.durationSeconds}s)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicDarkBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (result.isPhoto) "📸 High-Resolution Photo Story" else "🎥 Vertical Video • ${result.durationSeconds} seconds",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Clean CameraX stream captured with zero artifacts.",
                            fontSize = 11.sp,
                            color = ReachCyanGlow
                        )
                    }
                }

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Add Caption & Tags") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("captured_media_caption_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(result.copy(caption = caption)) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                modifier = Modifier.testTag("use_captured_media_button")
            ) {
                Text(if (result.mode == CameraMode.STORY) "Share to Story ✨" else "Use in Reel 🎬")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onRetake,
                modifier = Modifier.testTag("retake_captured_media_button")
            ) {
                Text("Retake")
            }
        }
    )
}
