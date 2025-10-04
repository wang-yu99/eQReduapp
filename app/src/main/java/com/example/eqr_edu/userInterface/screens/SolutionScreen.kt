package com.example.eqr_edu.userInterface.screens

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eqr_edu.IntermediateLanguage
import com.example.eqr_edu.userInterface.components.AppTopBar
import com.example.eqr_edu.userInterface.components.GrassDecoration
import com.example.eqr_edu.viewmodel.LanguageViewModel
import com.example.eqr_edu.viewmodel.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionScreen(
    solutionId: String,
    solutions: List<IntermediateLanguage.Solution>,
    labels: Map<String, String>,
    onNavigateBack: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToNewExercise: () -> Unit = {},
    languageViewModel: LanguageViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val verticalPadding = if (isLandscape) 8.dp else 16.dp
    val titleFontSize = if (isLandscape) 20.sp else 22.sp

    val solution = solutions.getOrNull((solutionId.toIntOrNull() ?: 1) - 1)

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
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                title = "💡 ${languageViewModel.stringResource("solution")} $solutionId",
                onNavigateBack = onNavigateBack,
                onNavigateHome = onNavigateHome,
                languageViewModel = languageViewModel
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 20.dp))

                solution?.let {
                    SolutionContentCard(
                        solution = it,
                        solutionIndex = solutionId.toIntOrNull() ?: 1,
                        languageViewModel = languageViewModel,
                        isLandscape = isLandscape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (isLandscape) 16.dp else 32.dp)
                    )
                } ?: run {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (isLandscape) 16.dp else 32.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Box(
                            modifier = Modifier.padding(if (isLandscape) 24.dp else 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🤔 ${languageViewModel.stringResource("solution_not_found")}",
                                fontSize = if (isLandscape) 16.sp else 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (isLandscape) 12.dp else 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    )
                ) {
                    Text(
                        text = "🌟 ${languageViewModel.stringResource("want_to_try_similar")}",
                        fontSize = if (isLandscape) 16.sp else 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(if (isLandscape) 12.dp else 16.dp)
                    )
                }

                NewExerciseButton(
                    onClick = onNavigateToNewExercise,
                    languageViewModel = languageViewModel,
                    isLandscape = isLandscape,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 32.dp))
            }
        }

        if (!isLandscape) {
            GrassDecoration(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SolutionContentCard(
    solution: IntermediateLanguage.Solution,
    solutionIndex: Int,
    languageViewModel: LanguageViewModel,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val (cardColor, accentColor, emoji) = when (solutionIndex % 4) {
        1 -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), "🌊")
        2 -> Triple(Color(0xFFFCE4EC), Color(0xFFE91E63), "🌸")
        3 -> Triple(Color(0xFFE8F5E8), Color(0xFF4CAF50), "🌿")
        else -> Triple(Color(0xFFFFF3E0), Color(0xFFFF9800), "🔥")
    }

    val cardPadding = if (isLandscape) 20.dp else 24.dp
    val iconSize = if (isLandscape) 60.dp else 72.dp
    val titleFontSize = if (isLandscape) 18.sp else 20.sp
    val emojiSize = if (isLandscape) 28.sp else 32.sp

    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = accentColor.copy(alpha = 0.3f)
            )
            .border(
                width = 2.dp,
                color = accentColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(
                            accentColor,
                            RoundedCornerShape(iconSize / 2)
                        )
                        .border(
                            width = 3.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(iconSize / 2)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = emojiSize
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = solution.question,
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (solution.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📝 ${languageViewModel.stringResource("solution_steps")}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = solution.steps.replace("\\n", "\n"),
                            fontSize = if (isLandscape) 14.sp else 16.sp,
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Start,
                            lineHeight = if (isLandscape) 20.sp else 24.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewExerciseButton(
    onClick: () -> Unit,
    languageViewModel: LanguageViewModel,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonHeight = if (isLandscape) 56.dp else 64.dp

    Card(
        modifier = modifier
            .height(buttonHeight)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(buttonHeight / 2),
                spotColor = Color(0xFFFFB74D).copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(buttonHeight / 2),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFD54F)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎯",
                    fontSize = if (isLandscape) 20.sp else 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = languageViewModel.stringResource("new_exercise"),
                    fontSize = if (isLandscape) 16.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}