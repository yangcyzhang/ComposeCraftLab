package com.craftlab.compose.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Grain

@Composable
fun DashboardScreen(onSelectDemo: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B15)) // 极暗夜幕色
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // 极具科技感的流光标题
        Text(
            text = "Compose Craft Lab",
            fontSize = 30.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF00ADB5)
        )
        Text(
            text = "Compose 视觉与交互高阶实验室",
            fontSize = 13.sp,
            color = Color.LightGray.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // 实验室舱室卡片
        LabCard(
            title = "粘性流体实验舱",
            subtitle = "Liquid Gooey Physics Effect",
            description = "利用 Blur 模糊渲染与色彩阈值算法，在 Canvas 上呈现两个水滴状小球靠近时拉丝融合的液态质感。",
            icon = Icons.Default.Opacity,
            gradientColors = listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)),
            onClick = { onSelectDemo(1) }
        )

        LabCard(
            title = "AGSL 炫彩着色器",
            subtitle = "AGSL RuntimeShader Sandbox",
            description = "编写 GPU 级的 AGSL 代码动态生成流沙、液态金属等数学艺术纹理，实时响应交互时间与指针触控。",
            icon = Icons.Default.GraphicEq,
            gradientColors = listOf(Color(0xFFF9D423), Color(0xFFFF4E50)),
            onClick = { onSelectDemo(2) }
        )

        LabCard(
            title = "果冻物理弹性球",
            subtitle = "Physics Spring Deformations",
            description = "引入质量-弹簧-阻尼系统，拖拽拉扯圆球使其像果冻般延展形变，松手后产生自然的物理高频回弹振动。",
            icon = Icons.Default.Animation,
            gradientColors = listOf(Color(0xFFB188FF), Color(0xFFE854FF)),
            onClick = { onSelectDemo(3) }
        )

        LabCard(
            title = "3D 交互流光卡片",
            subtitle = "3D Parallax Tilt & Shine Card",
            description = "应用三维透视投影，拖拽时卡片沿轴线倾斜，顶部覆以反向线性渐变遮罩，模拟全息箔片的流光效果。",
            icon = Icons.Default.Layers,
            gradientColors = listOf(Color(0xFF38F9D7), Color(0xFF43E97B)),
            onClick = { onSelectDemo(4) }
        )

        LabCard(
            title = "粒子引力星系",
            subtitle = "Physics Particle Swarm Gravity",
            description = "构建 500 个粒子的重力场和空气阻力模型。支持手指按压时产生引力波斥力排阻粒子，炫酷震撼。",
            icon = Icons.Default.Grain,
            gradientColors = listOf(Color(0xFFFF9A9E), Color(0xFFFECFEF)),
            onClick = { onSelectDemo(5) }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun LabCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0x0FFFFFFF), Color(0x03FFFFFF))
                )
            )
            .border(
                border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标流光背景
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = gradientColors
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00ADB5),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
