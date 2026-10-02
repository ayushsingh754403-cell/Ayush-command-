package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = RedDarkPrimary,
        onPrimary = androidx.compose.ui.graphics.Color.White,
        primaryContainer = RedDarkContainer,
        onPrimaryContainer = OnRedDarkContainer,
        background = BackgroundDark,
        surface = SurfaceDark,
        onBackground = OnSurfaceDark,
        onSurface = OnSurfaceDark
    )

private val LightColorScheme =
    lightColorScheme(
        primary = RedPrimary,
        onPrimary = androidx.compose.ui.graphics.Color.White,
        primaryContainer = RedContainer,
        onPrimaryContainer = OnRedContainer,
        background = BackgroundLight,
        surface = SurfaceLight,
        onBackground = OnSurfaceLight,
        onSurface = OnSurfaceLight
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default to false so our brand red and white theme shines through consistently
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
