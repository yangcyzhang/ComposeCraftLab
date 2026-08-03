package com.craftlab.compose.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicsSpringScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    
    // 果冻球的弹性偏移状态使用 Animatable
    val elasticOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    
    // 基础固定中心点
    val baseCenter = Offset(0f, 0f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("果冻物理弹性球", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
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
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // 拖拽时直接更新坐标偏移量
                                scope.launch {
                                    elasticOffset.snapTo(elasticOffset.value + dragAmount)
                                }
                            },
                            onDragEnd = {
                                // 释放拖拽：触发高频物理弹性回弹动画 (Damping = 0.35f, Stiffness = 150f)
                                scope.launch {
                                    elasticOffset.animateTo(
                                        targetValue = Offset.Zero,
                                        animationSpec = spring(
                                            dampingRatio = 0.35f, // 极低阻尼，产生多次往复摆动
                                            stiffness = 150f      // 中等刚度，果冻般Q弹
                                        )
                                    )
                                }
                            }
                        )
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2

                val currentOffset = elasticOffset.value
                val dragLen = currentOffset.getDistance()
                
                // 计算当前拖拽方向角度 (弧度转化为角度)
                val angleRad = atan2(currentOffset.y, currentOffset.x)
                val angleDeg = angleRad * 180f / PI.toFloat()

                // 基于拉伸长度动态计算拉伸因子与压缩因子 (面积守恒原理)
                val maxStretch = 1.6f
                val stretchFactor = 1.0f + (dragLen / 250f).coerceAtMost(maxStretch - 1.0f)
                val squeezeFactor = 1.0f / stretchFactor

                val radius = 100f

                // 在画布中心坐标系进行变换绘制
                rotate(degrees = angleDeg, pivot = Offset(centerX, centerY)) {
                    scale(
                        scaleX = stretchFactor,
                        scaleY = squeezeFactor,
                        pivot = Offset(centerX, centerY)
                    ) {
                        // 绘制拉伸后的果冻形变阴影 (扁平椭圆)
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.25f),
                            radius = radius * 1.05f,
                            center = Offset(centerX + dragLen - 8f, centerY + 8f)
                        )

                        // 绘制Q弹果冻球 (炫彩流光渐变)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFE854FF), Color(0xFFB188FF)),
                                center = Offset(centerX + dragLen - radius * 0.2f, centerY - radius * 0.2f),
                                radius = radius * 1.2f
                            ),
                            radius = radius,
                            center = Offset(centerX + dragLen, centerY)
                        )
                    }
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
                    text = "💡 操作提示：按住中间的紫色圆球并任意方向拖拽拉扯。随着拖拽幅度增大，圆球会由于拉力拉伸形变，松手后球体会根据弹簧振子阻尼系统（Spring System）产生高频抖动和晃动，直至复原。",
                    fontSize = 12.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
