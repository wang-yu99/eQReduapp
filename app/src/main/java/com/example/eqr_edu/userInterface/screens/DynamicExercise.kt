package com.example.eqr_edu.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eqr_edu.IRExerciseInstance
import com.example.eqr_edu.IRVerificationResult
import com.example.eqr_edu.userInterface.components.AppTopBar
import com.example.eqr_edu.userInterface.components.GrassDecoration
import com.example.eqr_edu.userInterface.components.RoundedButtonCard
import com.example.eqr_edu.userInterface.components.rememberResponsiveConfig
import com.example.eqr_edu.viewmodel.LanguageViewModel
import com.example.eqr_edu.viewmodel.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicExerciseScreen(
    exerciseType: String,
    exerciseInstance: IRExerciseInstance,
    showResult: Boolean,
    verificationResult: IRVerificationResult?,
    onAnswerSubmitted: (Double) -> Unit,
    onGenerateNew: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    languageViewModel: LanguageViewModel
) {
    val config = rememberResponsiveConfig()
    var userAnswer by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(exerciseInstance) {
        userAnswer = ""
    }
    val (exerciseEmoji, accentColor) = when (exerciseType.uppercase()) {
        "PLUS" -> "➕" to Color(0xFF4CAF50)
        "MINUS" -> "➖" to Color(0xFFE91E63)
        "STAR" -> "✨" to Color(0xFFFF9800)
        "MORE_OPERANDS" -> "🧮" to Color(0xFF2196F3)
        else -> "📝" to Color(0xFF9C27B0)
    }

    val localizedExerciseType = when (exerciseType.uppercase()) {
        "PLUS" -> languageViewModel.stringResource("operator_plus")
        "MINUS" -> languageViewModel.stringResource("operator_minus")
        "STAR" -> languageViewModel.stringResource("operator_multiply")
        "MORE_OPERANDS" -> languageViewModel.stringResource("more_operands")
        else -> exerciseType
    }
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
            // Top app bar - using encapsulated AppTopBar
            AppTopBar(
                title = "$exerciseEmoji ${languageViewModel.stringResource("exercise")} - $localizedExerciseType",
                onNavigateBack = onNavigateBack,
                backIconColor = accentColor,
                onNavigateHome = onNavigateHome,
                homeIconColor = accentColor,
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

                // Exercise display card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = config.itemSpacing * 2)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = accentColor.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = accentColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(config.exerciseCardPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${exerciseInstance.mathExpression} = ?",
                                fontSize = if (config.isLandscape) 22.sp else 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Small label
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(
                                    color = accentColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🎯 ${languageViewModel.stringResource("challenge")}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Answer input section
                Text(
                    text = "💭 ${languageViewModel.stringResource("please_enter_answer")}",
                    fontSize = config.bodyFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = config.itemSpacing),
                    textAlign = TextAlign.Start
                )

                // Input field
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = config.itemSpacing)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0x40FFD54F)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    OutlinedTextField(
                        value = userAnswer,
                        onValueChange = { userAnswer = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        enabled = !showResult,
                        placeholder = {
                            Text(
                                "${languageViewModel.stringResource("enter_number_answer")} ✏️",
                                color = Color(0xFF999999)
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Submit/Result section
                if (!showResult) {
                    // Submit button - using RoundedButtonCard
                    RoundedButtonCard(
                        text = languageViewModel.stringResource("submit_answer"),
                        onClick = {
                            val userAnswerDouble = userAnswer.toDoubleOrNull()
                            if (userAnswerDouble != null) {
                                onAnswerSubmitted(userAnswerDouble)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        emoji = "🚀",
                        backgroundColor = accentColor,
                        height = config.buttonHeight,
                        fontSize = config.bodyFontSize,
                        emojiSize = if (config.isLandscape) 18.sp else 20.sp,
                        showBorder = false
                    )
                } else {
                    // Result display
                    verificationResult?.let { result ->
                        ResultCard(
                            result = result,
                            accentColor = accentColor,
                            languageViewModel = languageViewModel,
                            config = config
                        )

                        Spacer(modifier = Modifier.height(config.itemSpacing))

                        // New exercise button
                        RoundedButtonCard(
                            text = languageViewModel.stringResource("try_again_exercise"),
                            onClick = onGenerateNew,
                            modifier = Modifier.fillMaxWidth(),
                            emoji = "🔄",
                            backgroundColor = Color(0xFFFFD54F),
                            height = config.buttonHeight,
                            fontSize = config.bodyFontSize,
                            emojiSize = if (config.isLandscape) 18.sp else 20.sp,
                            showBorder = false
                        )
                    }
                }
                Spacer(modifier = Modifier.height(if (config.isLandscape) 16.dp else 32.dp))
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
private fun ResultCard(
    result: IRVerificationResult,
    accentColor: Color,
    languageViewModel: LanguageViewModel,
    config: com.example.eqr_edu.userInterface.components.ResponsiveConfig
) {
    val (resultEmoji, backgroundColor) = if (result.isCorrect) {
        "🎉" to Color(0xFFE8F5E8)
    } else {
        "😢" to Color(0xFFFFEBEE)
    }

    val cardPadding = if (config.isLandscape) 16.dp else 20.dp
    val titleFontSize = if (config.isLandscape) 18.sp else 20.sp
    val answerFontSize = if (config.isLandscape) 16.sp else 18.sp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (result.isCorrect) Color(0x404CAF50) else Color(0x40F44336)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = if (result.isCorrect) Color(0xFF4CAF50).copy(alpha = 0.3f) else Color(0xFFF44336).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Result title
                Text(
                    text = "$resultEmoji ${if (result.isCorrect) languageViewModel.stringResource("great_job") else languageViewModel.stringResource("try_again")}",
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // User answer card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "👤 ${languageViewModel.stringResource("your_answer")}",
                                fontSize = 12.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${result.userAnswer}",
                                fontSize = answerFontSize,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        }
                    }

                    // Correct answer card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✅ ${languageViewModel.stringResource("correct_answer")}",
                                fontSize = 12.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${result.correctAnswer}",
                                fontSize = answerFontSize,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }

                // Error message display
                result.error?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "❌ ${languageViewModel.stringResource("error")}: $error",
                        fontSize = 12.sp,
                        color = Color(0xFFF44336)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(
                        color = accentColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (result.isCorrect) "🏆" else "💪",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}