package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TalkLoopColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = TextPrimary,
    primaryContainer = MidnightCardBg,
    onPrimaryContainer = IndigoPrimaryLight,
    secondary = ElectricCyan,
    onSecondary = MidnightDarkBg,
    secondaryContainer = MidnightDarkSurface,
    onSecondaryContainer = ElectricCyan,
    tertiary = NeonPurple,
    onTertiary = TextPrimary,
    background = MidnightDarkBg,
    onBackground = TextPrimary,
    surface = MidnightDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightCardBg,
    onSurfaceVariant = TextSecondary,
    outline = MidnightCardBorder,
    error = ErrorRose,
    onError = TextPrimary
)

@Composable
fun TalkLoopTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = TalkLoopColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MidnightDarkBg.toArgb()
            window.navigationBarColor = MidnightDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

