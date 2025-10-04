package com.example.eqr_edu.userInterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable grass decoration component
 * Displays grass and flower decorations at the bottom of various application pages
 */
@Composable
fun GrassDecoration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(140.dp) // Increased height to accommodate larger flowers
    ) {
        // Base grass gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF4CAF50).copy(alpha = 0.6f),
                            Color(0xFF4CAF50).copy(alpha = 0.9f),
                            Color(0xFF2E7D32)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // First layer: Background small flowers
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 45.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val backgroundFlowers = listOf("🌸", "🌿", "🍀", "🌱", "🌾", "🌸", "🌿")
            backgroundFlowers.forEachIndexed { index, flower ->
                Text(
                    text = flower,
                    fontSize = if (index % 2 == 0) 16.sp else 18.sp,
                    modifier = Modifier.offset(
                        x = if (index % 3 == 0) (-10).dp else if (index % 3 == 1) 0.dp else 10.dp,
                        y = if (index % 2 == 0) (-5).dp else 5.dp
                    )
                )
            }
        }

        // Second layer: Main large flower decorations
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 25.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val mainFlowers = listOf("🌺", "🌻", "🌷", "🌹", "🌼")
            mainFlowers.forEachIndexed { index, flower ->
                Text(
                    text = flower,
                    fontSize = if (index % 2 == 0) 32.sp else 28.sp, // Larger flowers
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(
                        x = if (index % 3 == 0) (-5).dp else if (index % 3 == 1) 0.dp else 5.dp,
                        y = if (index % 2 == 0) (-3).dp else 3.dp
                    )
                )
            }
        }

        // Third layer: Decorative small elements
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val decorativeElements = listOf("🦋", "✨", "🌟")
            decorativeElements.forEachIndexed { index, element ->
                Text(
                    text = element,
                    fontSize = if (index == 1) 20.sp else 18.sp,
                    modifier = Modifier.offset(
                        x = if (index % 2 == 0) (-15).dp else 15.dp,
                        y = if (index % 2 == 0) (-8).dp else 8.dp
                    )
                )
            }
        }

        // Fourth layer: Enhanced grass effect
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(20) { index ->
                val grassHeight = if (index % 3 == 0) 18.dp else if (index % 3 == 1) 14.dp else 12.dp
                val grassWidth = if (index % 2 == 0) 2.5.dp else 2.dp
                val grassColor = if (index % 3 == 0) Color(0xFF388E3C) else Color(0xFF4CAF50)

                Box(
                    modifier = Modifier
                        .width(grassWidth)
                        .height(grassHeight)
                        .offset(y = if (index % 2 == 0) (-1).dp else 1.dp)
                        .background(
                            color = grassColor,
                            shape = RoundedCornerShape(
                                topStart = 1.5.dp,
                                topEnd = 1.5.dp
                            )
                        )
                )
            }
        }

        // Fifth layer: Bottom soil effect
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E7D32),
                            Color(0xFF1B5E20)
                        )
                    )
                )
        )
    }
}