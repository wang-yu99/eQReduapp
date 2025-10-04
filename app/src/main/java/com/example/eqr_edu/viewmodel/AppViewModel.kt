package com.example.eqr_edu.viewmodel
import androidx.lifecycle.ViewModel
import com.example.eqr_edu.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _variables = MutableStateFlow<Map<String, Int>>(emptyMap())
    val variables: StateFlow<Map<String, Int>> = _variables.asStateFlow()

    private val _executionResults = MutableStateFlow<List<ExecutionResult>>(emptyList())
    val executionResults: StateFlow<List<ExecutionResult>> = _executionResults.asStateFlow()

    private val _labels = MutableStateFlow<Map<String, String>>(emptyMap())
    val labels: StateFlow<Map<String, String>> = _labels.asStateFlow()

    private val _solutions = MutableStateFlow<List<IntermediateLanguage.Solution>>(emptyList())
    val solutions: StateFlow<List<IntermediateLanguage.Solution>> = _solutions.asStateFlow()

    private val _isDataReady = MutableStateFlow(false)
    val isDataReady: StateFlow<Boolean> = _isDataReady.asStateFlow()

    // save VM and IL instance
    private var currentVM: EducationalVM? = null
    private var currentIL: IntermediateLanguage? = null

    fun processQRData(bytes: ByteArray) {
        try {
            val decoder = QRBinaryDecoder(bytes)
            val il = decoder.decode()

            val vm = EducationalVM(il)
            val vmVariables = vm.initializeVariables()
            val results = vm.executeExercises()

            // update all states
            currentVM = vm
            currentIL = il

            _variables.value = vmVariables
            _executionResults.value = results
            _labels.value = il.labels
            _solutions.value = il.solutions
            _isDataReady.value = true

        } catch (e: Exception) {
            _isDataReady.value = false
        }
    }

    fun regenerateVariables() {
        currentVM?.let { vm ->
            currentIL?.let { il ->
                val newVariables = vm.initializeVariables()
                val newResults = vm.executeExercises()

                _variables.value = newVariables
                _executionResults.value = newResults
            }
        }
    }

    fun getVM() = currentVM
    fun getIL() = currentIL

    fun resetData() {
        _variables.value = emptyMap()
        _executionResults.value = emptyList()
        _labels.value = emptyMap()
        _solutions.value = emptyList()
        _isDataReady.value = false
        currentVM = null
        currentIL = null
    }
}