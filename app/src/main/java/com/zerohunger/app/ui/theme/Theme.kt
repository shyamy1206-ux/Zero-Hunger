package com.zerohunger.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LightGreen,
    secondary = CalmGreen,
    tertiary = LightGreen,
    background = NavyDark,
    surface = NavySurface,
    surfaceVariant = NavySurfaceVariant,
    onPrimary = NavyDark,
    onSecondary = NavyDark,
    onTertiary = NavyDark,
    onBackground = LightText,
    onSurface = LightText,
    error = ErrorRedLight,
    onError = NavyDark
)

private val LightColorScheme = lightColorScheme(
    primary = CalmGreen,
    secondary = DarkGreen,
    tertiary = CalmGreen,
    background = OffWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun ZeroHungerTheme(
    darkTheme: Boolean = false, // Forced Light Mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
