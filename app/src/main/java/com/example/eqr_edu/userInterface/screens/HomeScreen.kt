package com.example.eqr_edu.userInterface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import com.example.eqr_edu.IntermediateLanguage
import com.example.eqr_edu.userInterface.components.GrassDecoration
import com.example.eqr_edu.R
import com.example.eqr_edu.userInterface.components.LanguageSelector
import com.example.eqr_edu.userInterface.components.ResponsiveConfig
import com.example.eqr_edu.userInterface.components.RoundedButtonCard
import com.example.eqr_edu.userInterface.components.SolutionButtonCard
import com.example.eqr_edu.userInterface.components.rememberResponsiveConfig
import com.example.eqr_edu.viewmodel.LanguageViewModel
import com.example.eqr_edu.viewmodel.stringResource

@Composable
fun HomePage(
    variables: Map<String, Int>,
    solutions: List<IntermediateLanguage.Solution>,
    exerciseCount: Int,
    onNavigateToSolution: (String) -> Unit = {},
    onNavigateToExercise: () -> Unit = {},
    onRegenerateVariables: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {},
    languageViewModel: LanguageViewModel
) {
    val config = rememberResponsiveConfig()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF9E6),
                        Color(0xFFFFFCDC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = config.screenPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = config.itemSpacing),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏠 ${languageViewModel.stringResource("home_page")} 🌟",
                    fontSize = config.titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )

                LanguageSelector(
                    languageViewModel = languageViewModel,
                    modifier = Modifier.padding(start = 8.dp)
                )

                IconButton(
                    onClick = onNavigateToScanner,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(config.iconSize)
                        .background(
                            color = Color(0xFF1976D2),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code_scanner_24),
                        contentDescription = "Scan New QR Code",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Educational Illustration Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = config.sectionSpacing)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color(0x40FF9800)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (config.isLandscape) 3f else 16f / 9f)  // 关键改动
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFFB74D),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edu),
                        contentDescription = "Educational QR Content",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(
                                color = Color(0xFFFF9800),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "✨ ${languageViewModel.stringResource("learn")}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Solutions Section
            if (solutions.isNotEmpty()) {
                Text(
                    text = "📚 ${languageViewModel.stringResource("solutions")}",
                    fontSize = config.headlineFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Start
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(config.itemSpacing)
                ) {
                    solutions.forEachIndexed { index, solution ->
                        SolutionCard(
                            solution = solution,
                            solutionIndex = index + 1,
                            onClick = { onNavigateToSolution((index + 1).toString()) },
                            config = config
                        )
                    }
                }
            } else {
                Text(
                    text = "🤔 No solutions available yet",
                    fontSize = 16.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(config.sectionSpacing))

            // Exercise Section
            Text(
                text = "💪 ${languageViewModel.stringResource("exercises")}",
                fontSize = config.headlineFontSize,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            ExerciseCard(
                exerciseCount = exerciseCount,
                onClick = onNavigateToExercise,
                languageViewModel = languageViewModel,
                config = config
            )

            Spacer(modifier = Modifier.height(config.sectionSpacing))
        }

        if (!config.isLandscape) {
            GrassDecoration(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SolutionCard(
    solution: IntermediateLanguage.Solution,
    solutionIndex: Int,
    onClick: () -> Unit,
    config: ResponsiveConfig
) {
    val (backgroundColor, emoji) = when (solutionIndex % 4) {
        1 -> Color(0xFF00BCD4) to "🌊"
        2 -> Color(0xFFE91E63) to "🌸"
        3 -> Color(0xFF4CAF50) to "🌿"
        else -> Color(0xFFFF9800) to "🔥"
    }

    SolutionButtonCard(
        text = solution.question,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        emoji = emoji,
        backgroundColor = backgroundColor,
        config = config
    )
}

@Composable
private fun ExerciseCard(
    exerciseCount: Int,
    onClick: () -> Unit,
    languageViewModel: LanguageViewModel,
    config: ResponsiveConfig
) {
    RoundedButtonCard(
        text = if (exerciseCount > 0)
            languageViewModel.stringResource("other_exercises")
        else
            "No exercises available",
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        emoji = if (exerciseCount > 0) "📝" else "😴",
        backgroundColor = Color(0xFFFFD54F),
        height = config.cardHeight,
        fontSize = config.bodyFontSize,
        showBorder = false,
        elevation = 6.dp
    )
}