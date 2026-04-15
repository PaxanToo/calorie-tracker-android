package com.example.fitness_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppDarkColorScheme = darkColorScheme(
    primary = LimeAccent,
    onPrimary = PureBlack,

    secondary = LimeAccentDark,
    onSecondary = PureBlack,

    tertiary = LimeAccent,
    onTertiary = PureBlack,

    background = BackgroundBlack,
    onBackground = PureWhite,

    surface = SurfaceBlack,
    onSurface = PureWhite,

    surfaceVariant = CardBlack,
    onSurfaceVariant = GrayText,

    outline = GrayBorder,
    outlineVariant = GrayBorder,

    error = androidx.compose.ui.graphics.Color(0xFFFF5252),
    onError = PureWhite
)

private val AppLightColorScheme = lightColorScheme(
    primary = LimeAccent,
    onPrimary = PureBlack,

    secondary = LimeAccentDark,
    onSecondary = PureBlack,

    tertiary = LimeAccent,
    onTertiary = PureBlack,

    background = SoftWhite,
    onBackground = PureBlack,

    surface = PureWhite,
    onSurface = PureBlack,

    surfaceVariant = SoftWhite,
    onSurfaceVariant = GrayText,

    outline = GrayBorder,
    outlineVariant = GrayBorder,

    error = androidx.compose.ui.graphics.Color(0xFFD32F2F),
    onError = PureWhite
)

@Composable
fun Fitness_appTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}