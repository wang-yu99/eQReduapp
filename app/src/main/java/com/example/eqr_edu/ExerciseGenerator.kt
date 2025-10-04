package com.example.eqr_edu

import android.util.Log

/**
 * IR-based exercise generator - generates exercises from definitions in QR code
 */
class ExerciseGenerator(
    private val il: IntermediateLanguage,
    private val vm: EducationalVM
) {
    companion object {
        const val TAG = "IRExerciseGenerator"
    }

    /**
     * Get exercises matching the specified tags
     * PLUS uses exact match, other tags use fuzzy match
     */
    fun getExercisesByTag(selectedTags: Set<String>): List<IRExerciseInstance> {
        val newVariables = vm.initializeVariables()

        // Only PLUS uses exact match
        val matchingExercises = if (selectedTags.contains("PLUS") && selectedTags.size == 1) {
            // PLUS exact match: tags must be exactly [PLUS]
            il.exercises.filter { exercise ->
                exercise.tags.toSet() == setOf("PLUS")
            }
        } else {
            // Other tags fuzzy match: contains any of the tags
            il.exercises.filter { exercise ->
                exercise.tags.any { tag -> selectedTags.contains(tag) }
            }
        }

        return matchingExercises.mapIndexed { index, exercise ->
            generateExerciseInstance(index + 1, exercise)
        }
    }

    /**
     * Generate new random exercises (regenerate variables)
     */
    fun generateNewExercises(selectedTags: Set<String>): List<IRExerciseInstance> {
        val newVariables = vm.initializeVariables()
        val matchingExercises = il.exercises.filter { exercise ->
            exercise.tags.any { tag -> selectedTags.contains(tag) }
        }
        return matchingExercises.mapIndexed { index, exercise ->
            generateExerciseInstance(index + 1, exercise)
        }
    }

    /**
     * Generate single exercise instance (correct replacement order: first labels then variables)
     */
    private fun generateExerciseInstance(id: Int, exercise: IntermediateLanguage.Exercise): IRExerciseInstance {
        val variables = vm.getVariables()
        val labels = vm.getLabels()

        // Step 1: Replace label placeholders (plus, minus, star, etc.)
        var processedExpression = exercise.expression
        labels.forEach { (labelKey, labelValue) ->
            when (labelKey) {
                "MINUS" -> {
                    processedExpression = processedExpression.replace("minus", labelValue)
                }
                "PLUS" -> {
                    processedExpression = processedExpression.replace("plus", labelValue)
                }
                "STAR" -> {
                    processedExpression = processedExpression.replace("star", labelValue)
                }
                "MORE_OPERANDS" -> {
                    processedExpression = processedExpression.replace("more_operands", labelValue)
                }
            }
        }

        // Step 2: Replace variables (a, b, x, y, etc.)
        variables.forEach { (varName, value) ->
            val oldExpression = processedExpression
            processedExpression = processedExpression.replace(varName, value.toString())
            if (oldExpression != processedExpression) {
              //  Log.d(TAG, "  Replaced variable '$varName' with '$value': $processedExpression")
            }
        }

        // Separate question description and mathematical expression
        val (questionText, mathExpression) = parseExerciseExpression(processedExpression)

        return IRExerciseInstance(
            id = id,
            originalExpression = exercise.expression,
            processedExpression = processedExpression,
            questionText = questionText,
            mathExpression = mathExpression,
            tags = exercise.tags,
            variables = variables
        )
    }

    /**
     * Parse exercise expression, separating question description and mathematical expression
     */
    private fun parseExerciseExpression(expression: String): Pair<String?, String> {
        val colonIndex = expression.indexOf(": ")
        if (colonIndex != -1) {
            val questionText = expression.substring(0, colonIndex)
            val remainingExpression = expression.substring(colonIndex + 2)
            if (remainingExpression.contains("=")) {
                val parts = remainingExpression.split("=").map { it.trim() }
                if (parts.size == 2) {
                    val leftExpression = parts[0].trim()
                    val questionDisplay = "$questionText: $leftExpression = ?"

                    return questionDisplay to leftExpression
                }
            }

            return questionText to remainingExpression
        } else {
            if (expression.contains("=")) {
                val parts = expression.split("=").map { it.trim() }
                if (parts.size == 2) {
                    val leftExpression = parts[0].trim()
                    val questionDisplay = "$leftExpression = ?"

                    return questionDisplay to leftExpression
                }
            }
            return null to expression
        }
    }

    /**
     * Verify user answer - completely relies on VM calculation
     */
    fun verifyAnswer(userAnswer: Double, exercise: IRExerciseInstance): IRVerificationResult {
        return try {
            // Use VM calculation
            val vmResult = vm.testExpression(exercise.mathExpression)

            if (vmResult.isValid && vmResult.result is ExecutionEvaluation.ExpressionResult) {
                val correctAnswer = vmResult.result.result
                val tolerance = 0.0001
                val isCorrect = Math.abs(userAnswer - correctAnswer) < tolerance

                return IRVerificationResult(
                    isCorrect = isCorrect,
                    correctAnswer = correctAnswer,
                    userAnswer = userAnswer,
                    vmSteps = vmResult.vmSteps,
                    debugInfo = "VM: ${exercise.mathExpression} = $correctAnswer"
                )
            }

            IRVerificationResult(
                isCorrect = false,
                correctAnswer = Double.NaN,
                userAnswer = userAnswer,
                error = "VM calculation failed: ${vmResult.errorMessage ?: "unknown error"}"
            )

        } catch (e: Exception) {
            IRVerificationResult(
                isCorrect = false,
                correctAnswer = Double.NaN,
                userAnswer = userAnswer,
                error = "Verification error: ${e.message}"
            )
        }
    }
}

/**
 * IR-based exercise instance
 */
data class IRExerciseInstance(
    val id: Int,
    val originalExpression: String,    // Original IR expression
    val processedExpression: String,   // Processed expression
    val questionText: String?,         // Question description part
    val mathExpression: String,        // Mathematical expression part
    val tags: List<String>,
    val variables: Map<String, Int>
)

/**
 * IR verification result
 */
data class IRVerificationResult(
    val isCorrect: Boolean,
    val correctAnswer: Double,
    val userAnswer: Double,
    val vmSteps: Int = 0,
    val debugInfo: String? = null,
    val error: String? = null
)