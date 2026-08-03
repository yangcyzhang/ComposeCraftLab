package com.craftlab.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGooeyScreen(onBack: () -> Unit) {
    // 两个可拖拽圆的中心坐标状态
    var circle1Center by remember { mutableStateOf(Offset(200f, 400f)) }
    var circle2Center by remember { mutableStateOf(Offset(500f, 600f)) }

    // 两圆半径
    val r1 = 80f
    val r2 = 60f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("粘性流体实验舱", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
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
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val pos = change.position
                            // 距离哪一个圆较近，就拖拽哪一个圆
                            val d1 = (pos - circle1Center).getDistance()
                            val d2 = (pos - circle2Center).getDistance()
                            if (d1 < d2) {
                                circle1Center += dragAmount
                            } else {
                                circle2Center += dragAmount
                            }
                        }
                    }
            ) {
                val p1 = circle1Center
                val p2 = circle2Center
                val dist = (p2 - p1).getDistance()

                // 只有当距离小于两圆半径加上拉丝最大阈值 (220f) 时，才绘制粘性拉丝
                val maxDist = r1 + r2 + 220f
                
                if (dist < maxDist && dist > abs(r1 - r2)) {
                    val path = Path()
                    
                    // 1. 计算两圆连线的夹角 theta
                    val theta = atan2(p2.y - p1.y, p2.x - p1.x)
                    
                    // 2. 动态计算粘性桥梁张角 alpha，拉得越远，张角越窄，直至拉断
                    val progress = (dist - (r1 + r2)) / 220f
                    val alphaFactor = (1.0f - progress.coerceIn(0f, 1f)).pow(2)
                    
                    val alpha1 = 0.8f * alphaFactor // 初始弧度
                    val alpha2 = 0.6f * alphaFactor

                    // 3. 圆 A 的两个切点坐标
                    val ax1 = p1.x + r1 * cos(theta + alpha1)
                    val ay1 = p1.y + r1 * sin(theta + alpha1)
                    val ax2 = p1.x + r1 * cos(theta - alpha1)
                    val ay2 = p1.y + r1 * sin(theta - alpha1)

                    // 4. 圆 B 的两个切点坐标
                    val bx1 = p2.x + r2 * cos(theta + PI.toFloat() - alpha2)
                    val by1 = p2.y + r2 * sin(theta + PI.toFloat() - alpha2)
                    val bx2 = p2.x + r2 * cos(theta + PI.toFloat() + alpha2)
                    val by2 = p2.y + r2 * sin(theta + PI.toFloat() + alpha2)

                    // 5. 粘性融合路径控制点：位于两个圆心连线中点，并向内侧微缩
                    val mx = (p1.x + p2.x) / 2
                    val my = (p1.y + p2.y) / 2
                    
                    // 加入流体拉伸阻力偏移
                    val stretchControlFactor = 0.5f * (1.0f - progress.coerceIn(0f, 1f))
                    val cx1 = mx + stretchControlFactor * (ax1 - mx)
                    val cy1 = my + stretchControlFactor * (ay1 - my)
                    val cx2 = mx + stretchControlFactor * (ax2 - mx)
                    val cy2 = my + stretchControlFactor * (ay2 - my)

                    // 6. 构造闭合的贝塞尔粘性连接体路径
                    path.moveTo(ax1, ay1)
                    path.quadraticTo(cx1, cy1, bx2, by2)
                    path.lineTo(bx1, by1)
                    path.quadraticTo(cx2, cy2, ax2, ay2)
                    path.close()

                    // 绘制粘性拉丝体（渐变流光）
                    drawPath(
                        path = path,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF00ADB5), Color(0xFF9C27B0)),
                            start = p1,
                            end = p2
                        )
                    )
                }

                // 绘制第一个圆球 (带立体光泽)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF00ADB5)),
                        center = p1 - Offset(r1 * 0.2f, r1 * 0.2f),
                        radius = r1 * 1.2f
                    ),
                    radius = r1,
                    center = p1
                )

                // 绘制第二个圆球
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE040FB), Color(0xFF9C27B0)),
                        center = p2 - Offset(r2 * 0.2f, r2 * 0.2f),
                        radius = r2 * 1.2f
                    ),
                    radius = r2,
                    center = p2
                )
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
                    text = "💡 操作提示：在屏幕上拖拽或者滑动任意圆球。当它们靠近时，Skia 引擎将自动计算正切切点与张角，使用贝塞尔曲线实时渲染出液态拉丝的粘性流体融合动画。",
                    fontSize = 12.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
