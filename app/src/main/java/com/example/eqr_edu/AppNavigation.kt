package com.example.eqr_edu
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eqr_edu.userInterface.screens.*
import com.example.eqr_edu.viewmodel.AppViewModel
import com.example.eqr_edu.viewmodel.ExerciseViewModel
import com.example.eqr_edu.viewmodel.LanguageViewModel

object AppDestinations {
    const val QR_SCANNER = "qr_scanner"
    const val HOME = "home"
    const val SOLUTION = "solution"
    const val EXERCISE = "exercise"
    const val EXERCISE_SELECTION = "exercise_selection"
    const val DYNAMIC_EXERCISE = "dynamic_exercise"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onProcessQRResult: (ByteArray, Int) -> Unit
) {
    // // Activity level ViewModel
    val appViewModel: AppViewModel = viewModel()
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val languageViewModel: LanguageViewModel = viewModel()

    // Collect shared state
    val variables by appViewModel.variables.collectAsState()
    val labels by appViewModel.labels.collectAsState()
    val solutions by appViewModel.solutions.collectAsState()
    val isDataReady by appViewModel.isDataReady.collectAsState()
    val executionResults by appViewModel.executionResults.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.QR_SCANNER
    ) {
        // QR Scanner Screen
        composable(AppDestinations.QR_SCANNER) {
            QRScannerScreen(
                onScanResult = { bytes, byteSize ->
                    appViewModel.processQRData(bytes)

                    // Initialize exercise generator
                    appViewModel.getIL()?.let { il ->
                        appViewModel.getVM()?.let { vm ->
                            exerciseViewModel.initializeGenerator(il, vm)
                        }
                    }

                    onProcessQRResult(bytes, byteSize)
                    navController.navigate(AppDestinations.HOME) {
                        popUpTo(AppDestinations.QR_SCANNER) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(AppDestinations.HOME) {
            if (isDataReady) {
                HomePage(
                    variables = variables,
                    solutions = solutions,
                    exerciseCount = executionResults.size,
                    onNavigateToSolution = { solutionId ->
                        navController.navigate("${AppDestinations.SOLUTION}/$solutionId")
                    },
                    onNavigateToExercise = {
                        navController.navigate(AppDestinations.EXERCISE_SELECTION)
                    },
                    onRegenerateVariables = {
                        appViewModel.regenerateVariables()
                        // Reinitialize exercise generator
                        appViewModel.getIL()?.let { il ->
                            appViewModel.getVM()?.let { vm ->
                                exerciseViewModel.initializeGenerator(il, vm)
                            }
                        }
                    },
                    onNavigateToScanner = {
                        appViewModel.resetData()
                        navController.navigate(AppDestinations.QR_SCANNER) {
                            popUpTo(AppDestinations.QR_SCANNER) { inclusive = true }
                        }
                    },
                    languageViewModel = languageViewModel
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(AppDestinations.QR_SCANNER) {
                        popUpTo(AppDestinations.HOME) { inclusive = true }
                    }
                }
            }
        }

        // Exercise Selection Screen
        composable(AppDestinations.EXERCISE_SELECTION) {
            if (isDataReady) {
                val allTags = getAllAvailableTags(solutions, executionResults)
                val availableTags = allTags.filter { it != "STAR" }

                ExerciseSelection(
                    availableTags = availableTags,
                    labels = labels,
                    onTagSelected = { selectedTag ->
                        exerciseViewModel.generateExercise(selectedTag)
                        navController.navigate("${AppDestinations.DYNAMIC_EXERCISE}/$selectedTag")
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        navController.navigate(AppDestinations.HOME) {
                            popUpTo(AppDestinations.HOME) { inclusive = true }
                        }
                    },
                    languageViewModel = languageViewModel
                )
            }
        }

        // Dynamic Exercise Screen
        composable("${AppDestinations.DYNAMIC_EXERCISE}/{exerciseType}") { backStackEntry ->
            val exerciseType = backStackEntry.arguments?.getString("exerciseType") ?: "PLUS"
            val currentExercise by exerciseViewModel.currentExercise.collectAsState()
            val showResult by exerciseViewModel.showResult.collectAsState()
            val verificationResult by exerciseViewModel.verificationResult.collectAsState()

            // Generate exercise when first entering the app
            LaunchedEffect(exerciseType) {
                if (currentExercise == null) {
                    exerciseViewModel.generateExercise(exerciseType)
                }
            }

            currentExercise?.let { exercise ->
                DynamicExerciseScreen(
                    exerciseType = exerciseType,
                    exerciseInstance = exercise,
                    showResult = showResult,
                    verificationResult = verificationResult,
                    onAnswerSubmitted = { userAnswer ->
                        exerciseViewModel.submitAnswer(userAnswer, exercise)
                    },
                    onGenerateNew = {
                        exerciseViewModel.generateExercise(exerciseType)
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        navController.navigate(AppDestinations.HOME) {
                            popUpTo(AppDestinations.HOME) { inclusive = true }
                        }
                    },
                    languageViewModel = languageViewModel
                )
            }
        }

        // Solution Screen
        composable("${AppDestinations.SOLUTION}/{solutionId}") { backStackEntry ->
            val solutionId = backStackEntry.arguments?.getString("solutionId") ?: "1"

            if (isDataReady) {
                val currentSolution = solutions.getOrNull((solutionId.toIntOrNull() ?: 1) - 1)

                SolutionScreen(
                    solutionId = solutionId,
                    solutions = solutions,
                    labels = labels,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        navController.navigate(AppDestinations.HOME) {
                            popUpTo(AppDestinations.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToNewExercise = {
                        val solutionTag = currentSolution?.tags?.let { tags ->
                            // If multiple operator labels are present simultaneously, it indicates mixed operations.
                            val operatorTags = tags.filter { it in listOf("PLUS", "MINUS", "STAR") }

                            when {
                                // multiple operator → MORE_OPERANDS
                                operatorTags.size >= 2 -> "MORE_OPERANDS"
                                tags.contains("MORE_OPERANDS") -> "MORE_OPERANDS"
                                tags.contains("STAR") -> "STAR"
                                tags.contains("MINUS") -> "MINUS"
                                tags.contains("PLUS") -> "PLUS"

                                else -> "PLUS"
                            }
                        } ?: "PLUS"

                        exerciseViewModel.generateExercise(solutionTag)
                        navController.navigate("${AppDestinations.DYNAMIC_EXERCISE}/$solutionTag")
                    },
                    languageViewModel = languageViewModel
                )
            }
        }

        // VM Execution Results Screen
        composable(AppDestinations.EXERCISE) {
            if (isDataReady) {
                ExerciseScreen(
                    executionResults = executionResults,
                    variables = variables,
                    labels = labels,
                    onNavigateBack = { navController.popBackStack() },
                    languageViewModel = languageViewModel
                )
            }
        }
    }
}

private fun getAllAvailableTags(
    solutions: List<IntermediateLanguage.Solution>,
    executionResults: List<ExecutionResult>
): List<String> {
    val allTags = mutableSetOf<String>()
    solutions.forEach { solution ->
        allTags.addAll(solution.tags)
    }
    allTags.addAll(listOf("PLUS", "MINUS"))
    if (allTags.size > 2) {
        allTags.add("MORE_OPERANDS")
    }
    return allTags.toList().sorted()
}