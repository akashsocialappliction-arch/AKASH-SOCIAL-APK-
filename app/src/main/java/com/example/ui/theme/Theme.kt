package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Refined Material 3 Dark Color Scheme (Professional Slate / Deep Blue)
private val DarkColorScheme = darkColorScheme(
    primary = StarPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = StarPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = VipGold,
    onSecondary = Color(0xFF1E1402),
    secondaryContainer = VipGoldDark,
    onSecondaryContainer = VipGoldLight,
    tertiary = ReachCyanGlow,
    onTertiary = Color(0xFF021B24),
    tertiaryContainer = ReachCyan,
    onTertiaryContainer = Color.White,
    background = CosmicDarkBg,
    onBackground = CosmicDarkText,
    surface = CosmicDarkSurface,
    onSurface = CosmicDarkText,
    surfaceVariant = CosmicDarkCard,
    onSurfaceVariant = CosmicDarkTextMuted,
    outline = CosmicDarkBorder,
    outlineVariant = Color(0xFF1E283D)
)

// Refined Material 3 Light Color Scheme (Crisp Minimalist Studio)
private val LightColorScheme = lightColorScheme(
    primary = StarPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = StarPrimaryVariant,
    secondary = VipGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = ReachCyan,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF0369A1),
    background = LightBg,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextMuted,
    outline = LightBorder,
    outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun StarPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve AKASH SOCIAL distinct branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
