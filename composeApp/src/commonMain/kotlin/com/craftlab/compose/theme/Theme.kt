package com.craftlab.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkNeonBlue = Color(0xFF00ADB5)
val CyberPurple = Color(0xFF9C27B0)
val DarkBackground = Color(0xFF0B0F19) // 极深邃星空底色
val SurfaceGlass = Color(0x1Fffffff)  // 半透明白，用于毛玻璃卡片

private val DarkColorScheme = darkColorScheme(
    primary = DarkNeonBlue,
    secondary = CyberPurple,
    background = DarkBackground,
    surface = SurfaceGlass,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFECEFF4),
    onSurface = Color(0xFFECEFF4)
)

@Composable
fun CraftLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
