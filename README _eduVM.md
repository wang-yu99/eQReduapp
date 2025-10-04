# EduVM - Educational Virtual Machine

A lightweight stack-based virtual machine for compiling and executing mathematical expressions in educational applications.

---

## Overview

EduVM serves as the computational core of the eQR Educational App. The system handles expression parsing, variable substitution, equation solving, and answer verification for elementary mathematics education.

**Key Features:**
- Stack-based architecture with full instruction set
- Infix to postfix expression compiler
- Dynamic variable generation and substitution
- Equation verification (expressions with `=`)
- Comprehensive error handling and debugging support

---

## Main Components

### 1. VMInstruction (Instruction Set)

**Stack Operations:** `Push`, `Pop`, `Dup`, `Swap`

**Arithmetic:** `Add`, `Sub`, `Mul`, `Div`, `Pow`, `Neg`

**Comparison:** `Equal`, `NotEqual`, `Greater`, `Less`

**Variables:** `LoadVar`, `StoreVar`

**Control Flow:** `Jump`, `JumpIfTrue`, `JumpIfFalse`, `Halt`, `Nop`

---

### 2. ExpressionCompiler

Converts mathematical expressions to VM instructions.

**Process:** Tokenization → Infix to Postfix → VM Instructions

**Supported:**
- Operators: `+`, `-`, `*`, `/`, `^`
- Parentheses and operator precedence
- Variables and negative numbers

**Example:**
```kotlin
val compiler = ExpressionCompiler()
val program = compiler.compile("(a + b) * 2")
```
---
### 3. Stack Virtual Machine
- Executes compiled VM programs
- Manages execution stack and variables
- Provides detailed execution results
- Includes safety checks (stack underflow, division by zero)

######  Usage Examples
1.Basic Expression Evaluation
```kotlin
val exprResult = result.result as ExecutionEvaluation.ExpressionResult
  println(exprResult.result)  // 14.0
```
2.Variable Substitution
```kotlin
// Generate random variables
val variables = eduVM.initializeVariables() // {"a": 7, "b": 4}
// Execute exercises
val results = eduVM.executeExercises()
// Original: "a + b"
// Substituted: "7 + 4"
// Result: 11.0
```
3.Equation Verification
```kotlin
val result = eduVM.testExpression("10 + 5 = 15")// isEqual: true
```
4.Low-Level VM Usage
```kotlin
val compiler = ExpressionCompiler()
val vm = StackVirtualMachine()
// Compile
val program = compiler.compile("(a + b) * 2")
// Set variables
vm.setVariable("a", 3.0)
vm.setVariable("b", 4.0)
// Execute
val result = vm.execute(program)
println("Result: ${result.result}")  // 14.0
```
###### Execution Flow
Steps:
- Split tokens: Expression → Tokens
- Compile: Infix → Postfix → VM Instructions
- Execute: VM runs instructions one by one
- Result: Returns calculated value or equation check

###### Error Handling
- Stack underflow detection
- Division by zero protection
- Undefined variable checks
- Execution step limit (prevents infinite loops)
- Complete error messages

###### VMExecutionResult
```kotlin
data class VMExecutionResult(
    val success: Boolean,
    val result: Double?,        // For expressions
    val isEqual: Boolean?,      // For equations
    val executionSteps: Int,
    val finalStack: List<Double>
)
```
###### ExecutionResult
```kotlin
data class ExecutionResult(
    val originalExpression: String,
    val substitutedExpression: String,
    val isValid: Boolean,
    val result: ExecutionEvaluation?,
    val compiledInstructions: String?
)
```
###### Supported Operations
- Number Comparison: Uses 0.0001 tolerance for equality
- Arithmetic: Addition, Subtraction, Multiplication, Division, Power
- Comparison: Equal, Not Equal, Greater Than, Less Than
- Variables: Single or multi-character (x, var1)
- Parentheses: Full support for grouped expressions
- Negative Numbers: Handles unary minuss

###### The advantages of design
- Educational: Clear separation of compilation and execution phases
- Debuggable: Step-by-step execution tracking
- Extensible: Easy to add new instructions or operations
- Safe: Built-in overflow and error protection
- Portable: Pure Kotlin implementation
---
### 5. Integration with eQR Educational App
EduVM is the core computational engine that powers:
- QR Code Processing - Parses content into intermediate language
- Variable Generation - Creates random problem instances
- Exercise Execution - Evaluates all exercises from QR code
- Answer Verification - Checks student answers
- Solution Display - Provides step-by-step explanations
##### How it integrates with App
Integration Flow:
QR Code → QRBinaryDecoder → IntermediateLanguage → EducationalVM → UI

AppViewModel:
```kotlin
fun processQRData(bytes: ByteArray) {
    val il = QRBinaryDecoder(bytes).decode()
    eduVM = EducationalVM(il)
    _variables.value = eduVM.initializeVariables()
    _results.value = eduVM.executeExercises()
}
```

ExerciseViewModel:
```kotlin
fun submitAnswer(userAnswer: Double, exercise: IRExerciseInstance) {
    val result = generator.verifyAnswer(userAnswer, exercise)
    _verificationResult.value = result
}
```