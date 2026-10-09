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

private val DarkColorScheme = darkColorScheme(
    primary = PsBluePrimary,
    onPrimary = PsTextPrimary,
    primaryContainer = PsSurfaceHighlight,
    onPrimaryContainer = PsAccentCyan,
    secondary = PsBlueSecondary,
    onSecondary = PsTextPrimary,
    secondaryContainer = PsSurfaceVariantDark,
    onSecondaryContainer = PsTextPrimary,
    tertiary = PsAccentCyan,
    onTertiary = PsBackgroundDark,
    background = PsBackgroundDark,
    onBackground = PsTextPrimary,
    surface = PsSurfaceDark,
    onSurface = PsTextPrimary,
    surfaceVariant = PsSurfaceVariantDark,
    onSurfaceVariant = PsTextSecondary,
    outline = PsSurfaceHighlight
)

@Composable
fun KytyPS5Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = PsBackgroundDark.toArgb()
                window.navigationBarColor = PsBackgroundDark.toArgb()
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
