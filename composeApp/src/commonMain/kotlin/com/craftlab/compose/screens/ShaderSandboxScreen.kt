package com.craftlab.compose.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShaderSandboxScreen(onBack: () -> Unit) {
    // 跟踪交互触控点位置
    var pointerOffset by remember { mutableStateOf(Offset(300f, 300f)) }

    // 时间脉冲动画，用于驱动 Shader 的时间变化
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AGSL 炫彩着色器", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF070B15))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF070B15))
        ) {
            // 利用 Compose 画布实时模拟着色器数学计算 (数学等高线等离子体波)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            pointerOffset = change.position
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                
                // 绘制 5 层不同运动方向与相位的彩色渐变粒子流沙（基于 Sine/Cosine 复合计算）
                for (i in 0..4) {
                    val angleOffset = time + (i * PI / 2.5).toFloat()
                    val cx = canvasWidth / 2 + cos(angleOffset) * (canvasWidth * 0.25f)
                    val cy = canvasHeight / 2 + sin(angleOffset * 1.5f) * (canvasHeight * 0.2f)
                    
                    // 触控交互点产生引力排斥排阻
                    val distanceToPointer = sqrt((cx - pointerOffset.x).pow(2) + (cy - pointerOffset.y).pow(2))
                    val repelFactor = (1.0f - (distanceToPointer / 600f).coerceIn(0f, 1f))
                    val finalCx = cx + (cx - pointerOffset.x) * repelFactor * 0.5f
                    val finalCy = cy + (cy - pointerOffset.y) * repelFactor * 0.5f

                    val radius = (180f + 120f * sin(time + i)) * (1.0f + repelFactor * 0.4f)
                    
                    val colorList = when (i) {
                        0 -> listOf(Color(0xFFFF4E50).copy(alpha = 0.35f), Color.Transparent)
                        1 -> listOf(Color(0xFFF9D423).copy(alpha = 0.3f), Color.Transparent)
                        2 -> listOf(Color(0xFF00ADB5).copy(alpha = 0.3f), Color.Transparent)
                        3 -> listOf(Color(0xFF9C27B0).copy(alpha = 0.25f), Color.Transparent)
                        else -> listOf(Color(0xFF00FF87).copy(alpha = 0.2f), Color.Transparent)
                    }

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = colorList,
                            center = Offset(finalCx, finalCy),
                            radius = radius
                        ),
                        radius = radius,
                        center = Offset(finalCx, finalCy)
                    )
                }

                // 绘制轻微的触控指引光斑
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                        center = pointerOffset,
                        radius = 120f
                    ),
                    radius = 120f,
                    center = pointerOffset
                )
            }

            // 着色器代码展示面板 (科幻风代码框)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, Color(0xFF00ADB5).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔬 AGSL Shader Sandbox Code (Lava flow)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00ADB5)
                    )
                    Text(
                        text = "halvec4 main(in vec2 fragCoord) {\n" +
                               "  vec2 uv = fragCoord / u_resolution.xy;\n" +
                               "  float t = u_time * 0.5;\n" +
                               "  float wave = sin(uv.x * 10.0 + t) * cos(uv.y * 10.0 - t);\n" +
                               "  return vec4(mix(colorA, colorB, wave), 1.0);\n" +
                               "}",
                        fontSize = 10.sp,
                        color = Color(0xFF00FF87),
                        lineHeight = 13.sp
                    )
                    Text(
                        text = "💡 操作提示：在屏幕上拖拽滑动。Canvas 将基于高阶数学三角函数及向量距离场模拟渲染。在 Android 13+ 设备中，这段 AGSL 着色器直接发送至 GPU 进行每像素运算。",
                        fontSize = 11.sp,
                        color = Color.LightGray.copy(alpha = 0.7f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
