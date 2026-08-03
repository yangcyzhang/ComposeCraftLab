package com.craftlab.compose

import androidx.compose.runtime.*
import com.craftlab.compose.theme.CraftLabTheme
import com.craftlab.compose.screens.*

@Composable
fun App() {
    // 0: Dashboard 主控台, 1: 粘性流体舱, 2: AGSL着色器沙盒, 3: 果冻物理弹性球, 4: 3D流光卡片, 5: 粒子引力星系
    var currentScreen by remember { mutableStateOf(0) }

    CraftLabTheme {
        when (currentScreen) {
            0 -> DashboardScreen(onSelectDemo = { currentScreen = it })
            1 -> LiquidGooeyScreen(onBack = { currentScreen = 0 })
            2 -> ShaderSandboxScreen(onBack = { currentScreen = 0 })
            3 -> PhysicsSpringScreen(onBack = { currentScreen = 0 })
            4 -> ParallaxCardScreen(onBack = { currentScreen = 0 })
            5 -> ParticleSystemScreen(onBack = { currentScreen = 0 })
        }
    }
}
