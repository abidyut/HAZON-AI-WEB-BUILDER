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

private val HazonDarkColorScheme = darkColorScheme(
    primary = HazonViolet,
    onPrimary = HazonText,
    primaryContainer = HazonPanel3,
    onPrimaryContainer = HazonPurple,
    secondary = HazonBlue,
    onSecondary = HazonText,
    secondaryContainer = HazonPanel2,
    onSecondaryContainer = HazonText,
    tertiary = HazonCyan,
    background = HazonBackground,
    onBackground = HazonText,
    surface = HazonPanel,
    onSurface = HazonText,
    surfaceVariant = HazonPanel2,
    onSurfaceVariant = HazonTextMuted,
    outline = HazonBorder,
    outlineVariant = HazonBorderHover
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Dark mode only by specification
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = HazonDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = HazonBackground.toArgb()
                window.navigationBarColor = HazonBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
