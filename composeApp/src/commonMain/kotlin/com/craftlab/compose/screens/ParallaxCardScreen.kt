package com.craftlab.compose.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParallaxCardScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    
    // 旋转倾角动画状态 (X 和 Y 轴)
    val rotateX = remember { Animatable(0f) }
    val rotateY = remember { Animatable(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("3D 交互流光卡片", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
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
                .background(Color(0xFF070B15)),
            contentAlignment = Alignment.Center
        ) {
            // 背景流光微光效果
            Box(
                modifier = Modifier
                    .size(340.dp, 480.dp)
                    .graphicsLayer {
                        // 应用 3D 透视投影 (X, Y 轴旋转与相机视角距离)
                        this.rotationX = rotateX.value
                        this.rotationY = rotateY.value
                        this.cameraDistance = 14f * density // 减小相机距离以增强 3D 纵深透视效果
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1E2638), Color(0xFF0F131C))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // 拖拽时根据移动方向反向计算倾斜角度 (限制最大旋转角度在 25 度内)
                                val targetRotX = (rotateX.value - dragAmount.y * 0.15f).coerceIn(-25f, 25f)
                                val targetRotY = (rotateY.value + dragAmount.x * 0.15f).coerceIn(-25f, 25f)
                                scope.launch {
                                    rotateX.snapTo(targetRotX)
                                    rotateY.snapTo(targetRotY)
                                }
                            },
                            onDragEnd = {
                                // 释放后，使用物理弹簧弹性还原到水平状态
                                scope.launch {
                                    rotateX.animateTo(0f, spring(stiffness = 180f, dampingRatio = 0.5f))
                                }
                                scope.launch {
                                    rotateY.animateTo(0f, spring(stiffness = 180f, dampingRatio = 0.5f))
                                }
                            }
                        )
                    }
            ) {
                // 卡片内部核心内容与全息流光层 (Holographic Overlay)
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. 流光反射层：根据旋转倾斜度实时计算反向光影倾角
                    val shineProgressX = rotateY.value / 25f  // -1.0 到 1.0
                    val shineProgressY = -rotateX.value / 25f // -1.0 到 1.0

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0x00FFFFFF),
                                        Color.White.copy(alpha = (0.2f + 0.15f * abs(shineProgressX)).coerceIn(0f, 0.4f)),
                                        Color(0xFF00ADB5).copy(alpha = (0.15f * abs(shineProgressY)).coerceIn(0f, 0.3f)),
                                        Color(0xFF9C27B0).copy(alpha = (0.15f * abs(shineProgressX)).coerceIn(0f, 0.3f)),
                                        Color(0x00FFFFFF)
                                    ),
                                    start = Offset(
                                        x = (0.3f + shineProgressX * 0.5f) * 1000f,
                                        y = (0.1f + shineProgressY * 0.5f) * 1000f
                                    ),
                                    end = Offset(
                                        x = (0.7f + shineProgressX * 0.5f) * 1000f,
                                        y = (0.9f + shineProgressY * 0.5f) * 1000f
                                    )
                                )
                            )
                    )

                    // 2. 卡片中的极客文字及立体发光线条
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LAB-04",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00ADB5)
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0xFF00FF87))
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "HOLOGRAPHIC",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "3D 立体全息视差卡片",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "运用矩阵透视投影变换与时间/位移差分混合渐变算法，实现模拟反向偏振全息箔片反光的 3D 视觉体验。",
                                fontSize = 11.sp,
                                color = Color.LightGray.copy(alpha = 0.6f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // 悬浮提示框 (毛玻璃质感)
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
                    text = "💡 操作提示：按住卡片并上下左右进行拖拽。卡片将使用带有 3D 空间纵深相机的 graphicsLayer 偏转，且卡片上覆的炫彩反光条将随着您倾斜角度的变化做反方向物理流光移动。",
                    fontSize = 12.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
