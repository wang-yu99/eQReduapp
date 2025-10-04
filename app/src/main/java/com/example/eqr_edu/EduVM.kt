package com.example.eqr_edu

import kotlin.random.Random

/**
 * Virtual Machine Instruction Set
 */
sealed class VMInstruction {
    // Stack operations
    data class Push(val value: Double) : VMInstruction()
    object Pop : VMInstruction()
    object Dup : VMInstruction()
    object Swap : VMInstruction()

    // Arithmetic operations
    object Add : VMInstruction()
    object Sub : VMInstruction()
    object Mul : VMInstruction()
    object Div : VMInstruction()
    object Pow : VMInstruction()
    object Neg : VMInstruction()

    // Comparison operations
    object Equal : VMInstruction()
    object NotEqual : VMInstruction()
    object Greater : VMInstruction()
    object Less : VMInstruction()

    // Variable operations
    data class LoadVar(val name: String) : VMInstruction()
    data class StoreVar(val name: String) : VMInstruction()

    // Control flow
    data class Jump(val address: Int) : VMInstruction()
    data class JumpIfFalse(val address: Int) : VMInstruction()
    data class JumpIfTrue(val address: Int) : VMInstruction()

    // Program control
    object Halt : VMInstruction()
    object Nop : VMInstruction()

    override fun toString(): String = when (this) {
        is Push -> "PUSH $value"
        Pop -> "POP"
        Dup -> "DUP"
        Swap -> "SWAP"
        Add -> "ADD"
        Sub -> "SUB"
        Mul -> "MUL"
        Div -> "DIV"
        Pow -> "POW"
        Neg -> "NEG"
        Equal -> "EQ"
        NotEqual -> "NEQ"
        Greater -> "GT"
        Less -> "LT"
        is LoadVar -> "LOAD $name"
        is StoreVar -> "STORE $name"
        is Jump -> "JMP $address"
        is JumpIfFalse -> "JF $address"
        is JumpIfTrue -> "JT $address"
        Halt -> "HALT"
        Nop -> "NOP"
    }
}

/**
 *  Virtual Machine Program
 */
data class VMProgram(
    val instructions: List<VMInstruction>,
    val metadata: Map<String, Any> = emptyMap()
) {
    fun disassemble(): String {
        return instructions.mapIndexed { index, instruction ->
            "${index.toString().padStart(3)}: $instruction"
        }.joinToString("\n")
    }
}

/**
 * Virtual Machine Execution Result
 */
data class VMExecutionResult(
    val success: Boolean,
    val result: Double? = null,
    val leftResult: Double? = null,
    val rightResult: Double? = null,
    val isEquation: Boolean = false,
    val isEqual: Boolean? = null,
    val error: String? = null,
    val executionSteps: Int = 0,
    val finalStack: List<Double> = emptyList()
)

/**
 * Execution Result Data Class
 */
data class ExecutionResult(
    val id: Int,
    val originalExpression: String,
    val substitutedExpression: String,
    val isValid: Boolean,
    val result: ExecutionEvaluation? = null,
    val errorMessage: String? = null,
    val vmSteps: Int = 0,
    val compiledInstructions: String? = null,
    val finalStack: List<Double>? = null
)

/**
 * Execution Evaluation Result
 */
sealed class ExecutionEvaluation {
    data class EquationResult(
        val isEqual: Boolean,
        val debugInfo: String? = null
    ) : ExecutionEvaluation()

    data class ExpressionResult(
        val result: Double,
        val debugInfo: String? = null
    ) : ExecutionEvaluation()
}

/**
 * Expression Compiler - Compiles mathematical expressions to VM instructions
 */
class ExpressionCompiler {

    companion object {
        const val TAG = "ExpressionCompiler"
    }

    /**
     * Compile expression to VM instruction sequence
     */
    fun compile(expression: String): VMProgram {
        val instructions = mutableListOf<VMInstruction>()

        //  Check if it's an equation
        val isEquation = expression.contains("=")

        if (isEquation) {
            compileEquation(expression, instructions)
        } else {
            compileExpression(expression, instructions)
        }

        instructions.add(VMInstruction.Halt)

        val program = VMProgram(
            instructions = instructions,
            metadata = mapOf(
                "isEquation" to isEquation,
                "originalExpression" to expression
            )
        )
        return program
    }

    /**
     * Compile equation (contains = sign)
     */
    private fun compileEquation(equation: String, instructions: MutableList<VMInstruction>) {
        val parts = equation.split("=").map { it.trim() }
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid equation format: $equation")
        }

        // Compile left expression
        compileExpression(parts[0], instructions)

        // Compile right expression
        compileExpression(parts[1], instructions)

        /// Compare if two values are equal
        instructions.add(VMInstruction.Equal)
    }

    /**
     *  Compile single expression
     */
    private fun compileExpression(expression: String, instructions: MutableList<VMInstruction>) {
        // Convert infix expression to postfix, then compile to VM instructions
        val postfix = infixToPostfix(expression)

        for (token in postfix) {
            when {
                token.isNumber() -> {
                    instructions.add(VMInstruction.Push(token.toDouble()))
                }
                isVariable(token) -> {
                    instructions.add(VMInstruction.LoadVar(token))
                }
                isOperator(token) -> {
                    val instruction = when (token) {
                        "+" -> VMInstruction.Add
                        "-" -> VMInstruction.Sub
                        "*" -> VMInstruction.Mul
                        "/" -> VMInstruction.Div
                        "^" -> VMInstruction.Pow
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    }
                    instructions.add(instruction)
                }
            }
        }
    }

    /**
     * Convert infix expression to postfix expression
     */
    private fun infixToPostfix(expression: String): List<String> {
        val output = mutableListOf<String>()
        val operatorStack = mutableListOf<String>()
        val tokens = tokenize(expression)

        for (token in tokens) {
            when {
                token.isNumber() || isVariable(token) -> output.add(token)
                token == "(" -> operatorStack.add(token)
                token == ")" -> {
                    while (operatorStack.isNotEmpty() && operatorStack.last() != "(") {
                        output.add(operatorStack.removeAt(operatorStack.size - 1))
                    }
                    if (operatorStack.isNotEmpty()) {
                        operatorStack.removeAt(operatorStack.size - 1) // Remove "("
                    }
                }
                isOperator(token) -> {
                    while (operatorStack.isNotEmpty() &&
                        operatorStack.last() != "(" &&
                        precedence(operatorStack.last()) >= precedence(token)) {
                        output.add(operatorStack.removeAt(operatorStack.size - 1))
                    }
                    operatorStack.add(token)
                }
            }
        }

        while (operatorStack.isNotEmpty()) {
            output.add(operatorStack.removeAt(operatorStack.size - 1))
        }

        return output
    }

    /**
     * Expression tokenization
     */
    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0

        while (i < expression.length) {
            val char = expression[i]

            when {
                char.isWhitespace() -> i++
                char.isDigit() || char == '.' -> {
                    val start = i
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        i++
                    }
                    tokens.add(expression.substring(start, i))
                }
                char.isLetter() -> {
                    // Variable name
                    val start = i
                    while (i < expression.length && (expression[i].isLetterOrDigit() || expression[i] == '_')) {
                        i++
                    }
                    tokens.add(expression.substring(start, i))
                }
                char == '-' && (i == 0 || expression[i-1] in "+-*/^(") -> {
                    // Negative sign handling
                    val start = i
                    i++
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        i++
                    }
                    tokens.add(expression.substring(start, i))
                }
                char in "+-*/^()" -> {
                    tokens.add(char.toString())
                    i++
                }
                else -> i++
            }
        }

        return tokens
    }

    private fun String.isNumber(): Boolean = this.toDoubleOrNull() != null
    private fun isVariable(token: String): Boolean = token.matches(Regex("[a-zA-Z][a-zA-Z0-9_]*"))
    private fun isOperator(token: String): Boolean = token in setOf("+", "-", "*", "/", "^")
    private fun precedence(operator: String): Int = when (operator) {
        "+", "-" -> 1
        "*", "/" -> 2
        "^" -> 3
        else -> 0
    }
}

/**
 * Stack-based Virtual Machine Execution Engine
 */
class StackVirtualMachine {

    companion object {
        const val TAG = "StackVM"
    }

    // VM状态
    private val stack = mutableListOf<Double>()
    private val variables = mutableMapOf<String, Double>()
    private var programCounter = 0
    private var running = false
    private var executionSteps = 0

    /**
     * Execute VM program
     */
    fun execute(program: VMProgram): VMExecutionResult {
        try {
            reset()
            running = true

            while (running && programCounter < program.instructions.size) {
                val instruction = program.instructions[programCounter]
               // Log.d(TAG, "[$executionSteps] PC=$programCounter: $instruction, Stack=${stack}")
                executeInstruction(instruction)
                programCounter++
                executionSteps++

                // Prevent infinite loops
                if (executionSteps > 10000) {
                    throw RuntimeException("Execution steps exceeded limit")
                }
            }
            return createResult(program, null)

        } catch (e: Exception) {
            return createResult(program, e.message)
        }
    }

    /**
     * Execute single instruction
     */
    private fun executeInstruction(instruction: VMInstruction) {
        when (instruction) {
            // stack operation
            is VMInstruction.Push -> stack.add(instruction.value)
            VMInstruction.Pop -> {
                if (stack.isEmpty()) throw RuntimeException("Stack underflow on POP")
                stack.removeAt(stack.size - 1)
            }
            VMInstruction.Dup -> {
                if (stack.isEmpty()) throw RuntimeException("Stack underflow on DUP")
                stack.add(stack.last())
            }
            VMInstruction.Swap -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on SWAP")
                val a = stack.removeAt(stack.size - 1)
                val b = stack.removeAt(stack.size - 1)
                stack.add(a)
                stack.add(b)
            }

            // Arithmetic operations
            VMInstruction.Add -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on ADD")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(a + b)
            }
            VMInstruction.Sub -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on SUB")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(a - b)
            }
            VMInstruction.Mul -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on MUL")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(a * b)
            }
            VMInstruction.Div -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on DIV")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                if (b == 0.0) throw ArithmeticException("Division by zero")
                stack.add(a / b)
            }
            VMInstruction.Pow -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on POW")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(Math.pow(a, b))
            }
            VMInstruction.Neg -> {
                if (stack.isEmpty()) throw RuntimeException("Stack underflow on NEG")
                val a = stack.removeAt(stack.size - 1)
                stack.add(-a)
            }

            // Comparison operations
            VMInstruction.Equal -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on EQ")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(if (Math.abs(a - b) < 0.0001) 1.0 else 0.0)
            }
            VMInstruction.NotEqual -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on NEQ")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(if (Math.abs(a - b) >= 0.0001) 1.0 else 0.0)
            }
            VMInstruction.Greater -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on GT")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(if (a > b) 1.0 else 0.0)
            }
            VMInstruction.Less -> {
                if (stack.size < 2) throw RuntimeException("Stack underflow on LT")
                val b = stack.removeAt(stack.size - 1)
                val a = stack.removeAt(stack.size - 1)
                stack.add(if (a < b) 1.0 else 0.0)
            }

            // Variable operations
            is VMInstruction.LoadVar -> {
                val value = variables[instruction.name]
                    ?: throw RuntimeException("Undefined variable: ${instruction.name}")
                stack.add(value)
            }
            is VMInstruction.StoreVar -> {
                if (stack.isEmpty()) throw RuntimeException("Stack underflow on STORE")
                val value = stack.removeAt(stack.size - 1)
                variables[instruction.name] = value
            }

            // Control flow
            is VMInstruction.Jump -> {
                programCounter = instruction.address - 1 // -1 because PC will be incremented
            }
            is VMInstruction.JumpIfFalse -> {
                if (stack.isEmpty()) throw RuntimeException("Stack underflow on JF")
                val condition = stack.removeAt(stack.size - 1)
                if (condition == 0.0) {
                    programCounter = instruction.address - 1
                }
            }
            is VMInstruction.JumpIfTrue -> {
                if (stack.isEmpty()) throw RuntimeException("Stack underflow on JT")
                val condition = stack.removeAt(stack.size - 1)
                if (condition != 0.0) {
                    programCounter = instruction.address - 1
                }
            }

            // Program control
            VMInstruction.Halt -> running = false
            VMInstruction.Nop -> { /* Do nothing */ }
            else -> {}
        }
    }

    /**
     * Set variable value
     */
    fun setVariable(name: String, value: Double) {
        variables[name] = value
    }

    /**
     * Set variables in batch
     */
    fun setVariables(vars: Map<String, Int>) {
        vars.forEach { (name, value) ->
            setVariable(name, value.toDouble())
        }
    }

    /**
     *  Reset VM state
     */
    private fun reset() {
        stack.clear()
        programCounter = 0
        running = false
        executionSteps = 0
    }

    /**
     * Create execution result
     */
    private fun createResult(program: VMProgram, error: String?): VMExecutionResult {
        val isEquation = program.metadata["isEquation"] as? Boolean ?: false

        return if (error != null) {
            VMExecutionResult(
                success = false,
                error = error,
                executionSteps = executionSteps,
                finalStack = stack.toList()
            )
        } else if (isEquation) {
            // Equation result: stack top should be comparison result (1.0 = true, 0.0 = false)
            val comparisonResult = if (stack.isNotEmpty()) stack.last() else 0.0
            VMExecutionResult(
                success = true,
                isEquation = true,
                isEqual = comparisonResult == 1.0,
                executionSteps = executionSteps,
                finalStack = stack.toList()
            )
        } else {
            // Expression result: stack top is calculation result
            val result = if (stack.isNotEmpty()) stack.last() else 0.0
            VMExecutionResult(
                success = true,
                result = result,
                executionSteps = executionSteps,
                finalStack = stack.toList()
            )
        }
    }
}

/**
 *  Educational Stack-based Virtual Machine - Integrates compiler and execution engine
 */
class EducationalVM(private val il: IntermediateLanguage) {

    companion object {
        const val TAG = "EducationalVM"
    }

    private val compiler = ExpressionCompiler()
    private val vm = StackVirtualMachine()
    private val variables = mutableMapOf<String, Int>()

    /**
     *  Initialize random variables
     */
    fun initializeVariables(): Map<String, Int> {
        variables.clear()

        il.randGenerators.forEach { (name, range) ->
            val randomValue = Random.nextInt(range.first, range.second + 1)
            variables[name] = randomValue
        }

        // Set VM variables
        vm.setVariables(variables)

        return variables.toMap()
    }

    /**
     * Execute all exercises
     */
    fun executeExercises(): List<ExecutionResult> {
        val results = mutableListOf<ExecutionResult>()

        il.exercises.forEachIndexed { index, exercise ->
            val result = executeExercise(index + 1, exercise)
            results.add(result)
        }

        return results
    }

    /**
     * Execute single exercise
     */
    private fun executeExercise(id: Int, exercise: IntermediateLanguage.Exercise): ExecutionResult {
        return try {
            // 1. Substitute variables
            val substitutedExpr = substituteVariables(exercise.expression)

            // 2. Compile to VM program
            val program = compiler.compile(substitutedExpr)
            val compiledInstructions = program.disassemble()

            // 3. Set VM variables
            vm.setVariables(variables)

            // 4. Execute VM program
            val vmResult = vm.execute(program)

            // 5. Create execution result
            if (vmResult.success) {
                val evaluation = if (vmResult.isEquation) {
                    ExecutionEvaluation.EquationResult(
                        isEqual = vmResult.isEqual ?: false,
                        debugInfo = "Final stack: ${vmResult.finalStack}"
                    )
                } else {
                    ExecutionEvaluation.ExpressionResult(
                        result = vmResult.result ?: 0.0,
                        debugInfo = "Final stack: ${vmResult.finalStack}"
                    )
                }
                ExecutionResult(
                    id = id,
                    originalExpression = exercise.expression,
                    substitutedExpression = substitutedExpr,
                    isValid = true,
                    result = evaluation,
                    vmSteps = vmResult.executionSteps,
                    compiledInstructions = compiledInstructions,
                    finalStack = vmResult.finalStack
                )
            } else {
                ExecutionResult(
                    id = id,
                    originalExpression = exercise.expression,
                    substitutedExpression = substitutedExpr,
                    isValid = false,
                    errorMessage = vmResult.error,
                    vmSteps = vmResult.executionSteps,
                    compiledInstructions = compiledInstructions,
                    finalStack = vmResult.finalStack
                )
            }

        } catch (e: Exception) {
            ExecutionResult(
                id = id,
                originalExpression = exercise.expression,
                substitutedExpression = exercise.expression,
                isValid = false,
                errorMessage = e.message,
                vmSteps = 0,
                finalStack = emptyList()
            )
        }
    }

    /**
     * Test single expression (for exercise generation and verification)
     */
    fun testExpression(expression: String): ExecutionResult {

        return try {
            // Substitute variables (if expression contains variables)
            val substituted = substituteVariables(expression)

            // Compile expression
            val program = compiler.compile(substituted)
            val compiledInstructions = program.disassemble()

            // Set VM variable state
            vm.setVariables(variables)

            // Execute VM program
            val vmResult = vm.execute(program)

            // Create execution result
            val evaluation = if (vmResult.isEquation) {
                ExecutionEvaluation.EquationResult(
                    isEqual = vmResult.isEqual ?: false,
                    debugInfo = "Test execution - VM Steps: ${vmResult.executionSteps}, Stack: ${vmResult.finalStack}"
                )
            } else {
                ExecutionEvaluation.ExpressionResult(
                    result = vmResult.result ?: 0.0,
                    debugInfo = "Test execution - VM Steps: ${vmResult.executionSteps}, Stack: ${vmResult.finalStack}"
                )
            }

            ExecutionResult(
                id = 0, // Test ID
                originalExpression = expression,
                substitutedExpression = substituted,
                isValid = vmResult.success,
                result = if (vmResult.success) evaluation else null,
                errorMessage = vmResult.error,
                vmSteps = vmResult.executionSteps,
                compiledInstructions = compiledInstructions,
                finalStack = vmResult.finalStack
            )

        } catch (e: Exception) {
            ExecutionResult(
                id = 0,
                originalExpression = expression,
                substitutedExpression = expression,
                isValid = false,
                errorMessage = "Test failed: ${e.message}",
                vmSteps = 0,
                finalStack = emptyList()
            )
        }
    }


    /**
     * Substitute variables in expression
     */
    private fun substituteVariables(expression: String): String {
        var result = expression
        variables.forEach { (name, value) ->
            result = result.replace(name, value.toString())
        }
        return result
    }

    /**
     * Get current variable values
     */
    fun getVariables(): Map<String, Int> = variables.toMap()

    /**
     * Get labels
     */
    fun getLabels(): Map<String, String> = il.labels

    /**
     * Get solutions list
     */
    fun getSolutions(): List<IntermediateLanguage.Solution> = il.solutions
}