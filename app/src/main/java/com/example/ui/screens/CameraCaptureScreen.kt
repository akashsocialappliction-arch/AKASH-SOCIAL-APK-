package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CameraCaptureScreen(
    onVideoCaptured: (durationSeconds: Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableIntStateOf(0) }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraError by remember { mutableStateOf(false) }

    // Recording timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDuration = 0
            while (isRecording) {
                delay(1000)
                recordingDuration++
                // Automatically stop at 60 seconds
                if (recordingDuration >= 60) {
                    isRecording = false
                    onVideoCaptured(recordingDuration)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_capture_screen")
    ) {
        if (hasCameraPermission && !cameraError) {
            // CameraX Live Preview View
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProviderRef = cameraProvider
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
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
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (_: Exception) {
                        // Handled gracefully
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Fallback / Permission rationale / Emulator simulated viewfinder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color.Black)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Videocam,
                        contentDescription = "Camera",
                        tint = ReachCyanGlow,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (!hasCameraPermission) "Camera Permission Required" else "Camera Viewfinder Active",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (!hasCameraPermission)
                            "Grant camera & audio access to capture native videos for AKASH SOCIAL."
                        else "Studio camera stream simulator ready for video capture.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    if (!hasCameraPermission) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarPrimary)
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            }
        }

        // Top Overlay Bar: Close button, duration timer, flash/status
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
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("camera_close_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            // Recording Timer Badge
            if (isRecording) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Red.copy(alpha = 0.85f),
                    modifier = Modifier.testTag("recording_timer_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val mins = recordingDuration / 60
                        val secs = recordingDuration % 60
                        Text(
                            text = String.format("%02d:%02d / 01:00", mins, secs),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "AKASH SOCIAL Studio",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Flip Camera Button
            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("flip_camera_lens_button")
            ) {
                Icon(Icons.Filled.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
            }
        }

        // Bottom Controls Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRecording) "Tap red square to stop & use video" else "Tap red button to record video clip",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Record / Stop Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(3.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clickable {
                            if (!isRecording) {
                                isRecording = true
                            } else {
                                isRecording = false
                                val duration = if (recordingDuration < 2) 5 else recordingDuration
                                onVideoCaptured(duration)
                            }
                        }
                        .testTag("camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        // Stop Recording Red Square
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Red)
                        )
                    } else {
                        // Start Recording Red Circle
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
            }
        }
    }
}
