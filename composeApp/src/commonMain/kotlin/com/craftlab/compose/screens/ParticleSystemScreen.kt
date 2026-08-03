package com.craftlab.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.math.*
import kotlin.random.Random

// 粒子属性结构
private class LabParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticleSystemScreen(onBack: () -> Unit) {
    // 追踪触碰排斥力中心坐标
    var repelPoint by remember { mutableStateOf<Offset?>(null) }
    
    // 初始化 500 个物理粒子
    val particles = remember {
        val list = mutableListOf<LabParticle>()
        for (i in 0 until 500) {
            val angle = Random.nextFloat() * 2 * PI.toFloat()
            val speed = Random.nextFloat() * 4f + 1f
            val color = when (Random.nextInt(3)) {
                0 -> Color(0xFF00ADB5).copy(alpha = Random.nextFloat() * 0.5f + 0.3f)
                1 -> Color(0xFF9C27B0).copy(alpha = Random.nextFloat() * 0.5f + 0.3f)
                else -> Color(0xFF00FF87).copy(alpha = Random.nextFloat() * 0.5f + 0.3f)
            }
            list.add(
                LabParticle(
                    x = 500f + cos(angle) * (100f + Random.nextFloat() * 150f),
                    y = 800f + sin(angle) * (100f + Random.nextFloat() * 150f),
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    size = Random.nextFloat() * 4f + 2f
                )
            )
        }
        list
    }

    // 时间驱动帧循环，用于驱动物理方程更新
    var frameTrigger by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { time ->
                frameTrigger = time
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("粒子引力星系", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
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
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> repelPoint = offset },
                            onDrag = { change, _ ->
                                change.consume()
                                repelPoint = change.position
                            },
                            onDragEnd = { repelPoint = null },
                            onDragCancel = { repelPoint = null }
                        )
                    }
            ) {
                // 每帧的物理演算更新
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2

                // 强制触发物理重绘
                val t = frameTrigger 

                particles.forEach { p ->
                    // 1. 引力：计算粒子指向屏幕中心的引力向量
                    val dx = centerX - p.x
                    val dy = centerY - p.y
                    val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
                    
                    // 万有引力近似模型 (向心加速度)
                    val gravityStrength = 0.12f
                    p.vx += (dx / dist) * gravityStrength
                    p.vy += (dy / dist) * gravityStrength

                    // 2. 指针排斥：若存在手指拖拽点，计算排斥力
                    repelPoint?.let { repel ->
                        val rdx = p.x - repel.x
                        val rdy = p.y - repel.y
                        val rdist = sqrt(rdx * rdx + rdy * rdy).coerceAtLeast(1f)
                        if (rdist < 300f) {
                            val pushForce = (1.0f - rdist / 300f) * 1.8f
                            p.vx += (rdx / rdist) * pushForce
                            p.vy += (rdy / rdist) * pushForce
                        }
                    }

                    // 3. 物理阻力与平移更新
                    p.vx *= 0.97f // 空气阻力
                    p.vy *= 0.97f
                    
                    p.x += p.vx
                    p.y += p.vy

                    // 4. 绘制粒子
                    drawCircle(
                        color = p.color,
                        radius = p.size,
                        center = Offset(p.x, p.y)
                    )
                }
            }

            // 操作提示面板 (毛玻璃风格)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "💡 操作提示：在屏幕上拖拽或者滑动。500 个相互独立的物理粒子在受到向心重力作用的同时，会受到您拖拽触碰点产生的引力排斥波。通过 withFrameMillis 实现 GPU 同步级物理渲染演算。",
                    fontSize = 12.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
