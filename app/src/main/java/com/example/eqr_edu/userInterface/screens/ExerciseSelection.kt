package com.example.eqr_edu.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import com.example.eqr_edu.userInterface.components.AppTopBar
import com.example.eqr_edu.userInterface.components.ExerciseTypeButtonCard
import com.example.eqr_edu.userInterface.components.GrassDecoration
import com.example.eqr_edu.userInterface.components.ResponsiveConfig
import com.example.eqr_edu.userInterface.components.rememberResponsiveConfig
import com.example.eqr_edu.viewmodel.LanguageViewModel
import com.example.eqr_edu.viewmodel.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelection(
    availableTags: List<String>,
    labels: Map<String, String>,
    onTagSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
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
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                title = "🎯 ${languageViewModel.stringResource("other_exercises")}",
                onNavigateBack = onNavigateBack,
                onNavigateHome = onNavigateHome,
                languageViewModel = languageViewModel
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = config.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(config.itemSpacing))

                // Question prompt card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = config.sectionSpacing),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(config.contentPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🤔",
                            fontSize = if (config.isLandscape) 28.sp else 32.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = languageViewModel.stringResource("which_exercise"),
                            fontSize = config.headlineFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                // Exercise type buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(config.itemSpacing)
                ) {
                    availableTags.forEach { tag ->
                        ExerciseTypeButton(
                            tag = tag,
                            label = labels[tag] ?: tag,
                            languageViewModel = languageViewModel,
                            onClick = { onTagSelected(tag) },
                            config = config
                        )
                    }
                }

                Spacer(modifier = Modifier.height(config.sectionSpacing))
            }
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
private fun ExerciseTypeButton(
    tag: String,
    label: String,
    languageViewModel: LanguageViewModel,
    onClick: () -> Unit,
    config: ResponsiveConfig
) {
    val (backgroundColor, emoji) = when (tag) {
        "PLUS" -> Color(0xFFE91E63) to "➕"
        "MINUS" -> Color(0xFFFF9800) to "➖"
        "STAR" -> Color(0xFF9C27B0) to "✨"
        "MORE_OPERANDS" -> Color(0xFF2196F3) to "🧮"
        else -> Color(0xFF4CAF50) to "📝"
    }

    val localizedLabel = when (tag) {
        "PLUS" -> languageViewModel.stringResource("operator_plus")
        "MINUS" -> languageViewModel.stringResource("operator_minus")
        "STAR" -> languageViewModel.stringResource("operator_multiply")
        "MORE_OPERANDS" -> languageViewModel.stringResource("more_operands")
        else -> label
    }

    ExerciseTypeButtonCard(
        tag = tag,
        text = localizedLabel,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        emoji = emoji,
        backgroundColor = backgroundColor
    )
}