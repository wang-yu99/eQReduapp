package com.example.eqr_edu.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eqr_edu.ExecutionResult
import com.example.eqr_edu.ExecutionEvaluation
import com.example.eqr_edu.userInterface.components.AppTopBar
import com.example.eqr_edu.viewmodel.LanguageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    executionResults: List<ExecutionResult>,
    variables: Map<String, Int>,
    labels: Map<String, String>,
    onNavigateBack: () -> Unit,
    languageViewModel: LanguageViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top App Bar
        AppTopBar(
            title = "Stack VM Execution",
            titleColor = Color.Black,
            onNavigateBack = onNavigateBack,
            backIconColor = Color.Black,
            showHomeButton = false,  // 不显示主页按钮
            backgroundColor = Color.White
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Variables Header
            item {
                VariablesCard(variables = variables)
            }

            // VM Execution Summary
            item {
                VMSummaryCard(executionResults = executionResults)
            }

            // Exercise Results Header
            item {
                Text(
                    text = "VM Exercise Results (${executionResults.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Exercise Results
            itemsIndexed(executionResults) { index, result ->
                VMExerciseResultCard(
                    result = result,
                    exerciseNumber = index + 1
                )
            }
        }
    }
}

@Composable
private fun VariablesCard(
    variables: Map<String, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Variables",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stack VM Variables",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (variables.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(variables.entries.toList()) { (name, value) ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Text(
                                text = "$name = $value",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No variables defined",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun VMSummaryCard(
    executionResults: List<ExecutionResult>
) {
    val validResults = executionResults.count { it.isValid }
    val totalResults = executionResults.size
    val totalVMSteps = executionResults.sumOf { it.vmSteps }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Stack VM Execution Summary",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Valid Executions",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$validResults/$totalResults",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total VM Steps",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = totalVMSteps.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                }
            }
        }
    }
}

@Composable
private fun VMExerciseResultCard(
    result: ExecutionResult,
    exerciseNumber: Int
) {
    val cardColor = if (result.isValid) Color.White else Color(0xFFFFF3E0)
    val statusColor = if (result.isValid) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with exercise number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exercise $exerciseNumber",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (result.isValid) Icons.Default.CheckCircle else Icons.Default.ArrowBack,
                        contentDescription = if (result.isValid) "Success" else "Error",
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (result.isValid) "Valid" else "Failed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // VM execution info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "VM Steps: ${result.vmSteps}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                if (result.isValid) {
                    Text(
                        text = "Compiled & Executed",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expressions
            ExpressionDisplay(
                label = "Original:",
                expression = result.originalExpression,
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExpressionDisplay(
                label = "Substituted:",
                expression = result.substitutedExpression,
                textColor = Color(0xFF1976D2)
            )

            // VM Instructions (collapsible)
            if (result.compiledInstructions != null) {
                Spacer(modifier = Modifier.height(8.dp))
                var showInstructions by remember { mutableStateOf(false) }

                TextButton(
                    onClick = { showInstructions = !showInstructions }
                ) {
                    Text(
                        text = if (showInstructions) "Hide VM Instructions" else "Show VM Instructions",
                        fontSize = 12.sp
                    )
                }

                if (showInstructions) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = "VM Instructions:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = result.compiledInstructions,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }
            }

            // Result or Error
            if (result.isValid && result.result != null) {
                Spacer(modifier = Modifier.height(8.dp))
                VMResultDisplay(evaluation = result.result)
            } else if (!result.isValid && result.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorDisplay(errorMessage = result.errorMessage)
            }
        }
    }
}

@Composable
private fun ExpressionDisplay(
    label: String,
    expression: String,
    textColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = expression,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = textColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun VMResultDisplay(evaluation: ExecutionEvaluation) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Stack VM Result:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            when (evaluation) {
                is ExecutionEvaluation.EquationResult -> {
                    Text(
                        text = if (evaluation.isEqual) "âœ“ Equation is TRUE" else "âœ— Equation is FALSE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (evaluation.isEqual) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    evaluation.debugInfo?.let { debug ->
                        Text(
                            text = "Debug: $debug",
                            fontSize = 11.sp,
                            color = Color(0xFF666666),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
                is ExecutionEvaluation.ExpressionResult -> {
                    Text(
                        text = "Value: ${formatNumber(evaluation.result)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    evaluation.debugInfo?.let { debug ->
                        Text(
                            text = "Debug: $debug",
                            fontSize = 11.sp,
                            color = Color(0xFF666666),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun ErrorDisplay(errorMessage: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "VM Error:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = Color(0xFFC62828)
            )
        }
    }
}

// Helper function to format numbers
private fun formatNumber(number: Double): String {
    return if (number == number.toLong().toDouble()) {
        number.toLong().toString()
    } else {
        String.format("%.3f", number)
    }
}