package com.example.xdownloader.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1DA1F2),
    secondary = Color(0xFF14171A),
    tertiary = Color(0xFF657786),
    background = Color(0xFF000000),
    surface = Color(0xFF16181C),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1DA1F2),
    secondary = Color(0xFF14171A),
    tertiary = Color(0xFF657786),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F9F9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF14171A),
    onSurface = Color(0xFF14171A)
)

@Composable
fun XDownloaderTheme(
    darkTheme: Boolean = true, // 强制使用深色模式
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb() // 透明状态栏
            window.navigationBarColor = Color.Transparent.toArgb() // 透明导航栏
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
