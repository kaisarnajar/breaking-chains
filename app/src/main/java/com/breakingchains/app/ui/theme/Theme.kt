package com.breakingchains.app.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,
    primaryContainer = MintSoftContainer,
    onPrimaryContainer = DeepTealDark,
    secondary = MintPrimary,
    onSecondary = DeepTealDark,
    secondaryContainer = MintLight,
    onSecondaryContainer = DeepTealDark,
    tertiary = AccentCoralRed,
    background = SoftSkyBackground,
    onBackground = TextPrimaryDark,
    surface = CardSurfaceWhite,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFEBF1F5),
    onSurfaceVariant = TextSecondaryMuted,
    outline = BorderSubtle
)

private val DarkColorScheme = darkColorScheme(
    primary = MintPrimary,
    onPrimary = DeepTealDark,
    primaryContainer = DeepTeal,
    onPrimaryContainer = MintLight,
    secondary = DeepTealLight,
    onSecondary = Color.White,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun BreakingChainsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to maintain consistent visual identity
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
