package com.example.eqr_edu.viewmodel

import androidx.lifecycle.ViewModel
import com.example.eqr_edu.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExerciseViewModel : ViewModel() {

    private val _currentExercise = MutableStateFlow<IRExerciseInstance?>(null)
    val currentExercise: StateFlow<IRExerciseInstance?> = _currentExercise.asStateFlow()

    private val _showResult = MutableStateFlow(false)
    val showResult: StateFlow<Boolean> = _showResult.asStateFlow()

    private val _verificationResult = MutableStateFlow<IRVerificationResult?>(null)
    val verificationResult: StateFlow<IRVerificationResult?> = _verificationResult.asStateFlow()

    private var exerciseGenerator: ExerciseGenerator? = null

    fun initializeGenerator(il: IntermediateLanguage, vm: EducationalVM) {
        exerciseGenerator = ExerciseGenerator(il, vm)
    }

    fun generateExercise(tag: String) {
        exerciseGenerator?.let { generator ->
            try {
                val exercises = generator.getExercisesByTag(setOf(tag))

                if (exercises.isNotEmpty()) {
                    val randomIndex = kotlin.random.Random.nextInt(exercises.size)
                    _currentExercise.value = exercises[randomIndex]
                    _showResult.value = false
                    _verificationResult.value = null
                }
            } catch (e: Exception) {
                // Handle error silently or add proper error handling
            }
        }
    }

    fun submitAnswer(userAnswer: Double, exercise: IRExerciseInstance) {
        exerciseGenerator?.let { generator ->
            val result = generator.verifyAnswer(userAnswer, exercise)
            _verificationResult.value = result
            _showResult.value = true
        }
    }
}