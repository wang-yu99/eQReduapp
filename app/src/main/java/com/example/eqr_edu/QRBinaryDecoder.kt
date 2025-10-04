package com.example.eqr_edu

import java.io.EOFException

/**
 * Intermediate Language Data Structure
 * Represents parsed educational content from QR code
 */
data class IntermediateLanguage(
    val labels: Map<String, String>,                    // UI display labels
    val randGenerators: Map<String, Pair<Int, Int>>,   // Random variable generators
    val solutions: List<Solution>,                      // Teaching solutions
    val exercises: List<Exercise>                       // Mathematical exercises
) {
    data class Solution(val question: String, val steps: String, val tags: List<String>)
    data class Exercise(val expression: String, val tags: List<String>)
}

/**
 * Enhanced QR Binary Decoder
 * Decodes binary data from QR code into IntermediateLanguage structure
 */
class QRBinaryDecoder(private val byteArray: ByteArray) {
    private val reader = BitReader(byteArray, startOffsetBits = 22)
    private val labels = mutableMapOf<String, String>()
    private val randGenerators = mutableMapOf<String, Pair<Int, Int>>()
    private val solutions = mutableListOf<IntermediateLanguage.Solution>()
    private val exercises = mutableListOf<IntermediateLanguage.Exercise>()

    fun decode(): IntermediateLanguage {
        try {
            decodeHeader()
            decodeSolutionSection()
            decodeExerciseSection()

            return IntermediateLanguage(
                labels = labels.toMap(),
                randGenerators = randGenerators.toMap(),
                solutions = solutions.toList(),
                exercises = exercises.toList()
            )
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Decode header section containing labels and random generators
     */
    private fun decodeHeader() {
        while (true) {
            val marker = reader.readBits(2)
            when (marker) {
                0b01 -> decodeLabel()               // Label marker
                0b10 -> {                           // Function/Variable marker
                    val fzType = reader.readBits(4)
                    if (fzType == 0b0000) decodeRandInt()  // Random integer generator
                }
                0b00 -> return                      // End of header
                else -> throw IllegalArgumentException("Invalid header marker: $marker")
            }
        }
    }

    /**
     * Decode UI display labels (MINUS, PLUS, STAR, MORE_OPERANDS)
     */
    private fun decodeLabel() {
        val opType = reader.readBits(2)
        val str = decodeString()

        val labelName = when (opType) {
            0b00 -> "MINUS"
            0b01 -> "PLUS"
            0b10 -> "STAR"
            0b11 -> "MORE_OPERANDS"
            else -> throw IllegalStateException("Invalid label type: $opType")
        }

        labels[labelName] = str
    }

    /**
     * Decode random integer generator configuration
     */
    private fun decodeRandInt() {
        val name = decodeString()
        val min = eliasDeltaDecodeSigned(reader)
        val max = eliasDeltaDecodeSigned(reader)
        randGenerators[name] = min to max
    }

    /**
     * Decode solution section containing teaching solutions
     */
    private fun decodeSolutionSection() {
        var solutionCount = 0

        while (reader.hasRemaining()) {
            // Check for solution marker (01)
            if (reader.hasRemaining() && reader.peekBits(2) != 0b01) {
                break
            }

            try {
                reader.skipBits(2) // Skip SOLUTION_PREFIX (01)
                reader.skipBits(2) // Skip question prefix (00)
                reader.skipBits(2) // Skip encoding type (00)

                val question = decodeAsciiString()

                if (!reader.hasRemaining()) break

                reader.skipBits(2) // Skip steps prefix
                reader.skipBits(2) // Skip encoding type
                val steps = decodeAsciiString()

                val tags = decodeTags()

                // Skip solution end marker (000)
                if (reader.hasRemaining() && reader.peekBits(3) == 0b000) {
                    reader.skipBits(3)
                }

                solutions.add(IntermediateLanguage.Solution(question, steps, tags))
                solutionCount++

            } catch (e: Exception) {
                break
            }
        }

        // Skip SOLUTION_END marker (00) if present
        if (reader.hasRemaining() && reader.peekBits(2) == 0b00) {
            reader.skipBits(2)
        }
    }

    /**
     * Decode exercise section containing mathematical exercises
     */
    private fun decodeExerciseSection() {
        if (!reader.hasRemaining()) {
            return
        }

        var exerciseCount = 0

        while (reader.hasRemaining()) {
            try {
                // Check for EXERCISES_END marker (00)
                if (reader.hasRemaining() && reader.peekBits(2) == 0b00) {
                    reader.skipBits(2)
                    break
                }

                // Check for EXERCISE_PREFIX (01)
                if (!reader.hasRemaining() || reader.peekBits(2) != 0b01) {
                    break
                }

                reader.skipBits(2) // Skip EXERCISE_PREFIX (01)

                // Check for string prefix flag
                val hasStringPrefix = reader.readBit() == 1
                var prefixText = ""
                if (hasStringPrefix) {
                    prefixText = decodeString()
                }

                // Decode expression
                val expr = decodeExpression()
                if (expr.trim().isEmpty()) break

                // Decode tags
                val tags = decodeTags()

                // Skip tag end marker (000)
                if (reader.hasRemaining() && reader.peekBits(3) == 0b000) {
                    reader.skipBits(3)
                }

                // Combine final expression
                val finalExpression = if (hasStringPrefix && prefixText.isNotEmpty()) {
                    "$prefixText: $expr"
                } else {
                    expr
                }

                exercises.add(IntermediateLanguage.Exercise(finalExpression, tags))
                exerciseCount++

            } catch (e: Exception) {
                if (reader.hasRemaining()) {
                    reader.skipBits(1) // Skip 1 bit and continue trying
                } else {
                    break
                }
            }
        }
    }

    /**
     * Decode mathematical expression with operands and operators
     */
    private fun decodeExpression(): String {
        val expr = StringBuilder()

        while (reader.hasRemaining()) {
            try {
                // Read operand/operator flag bit
                val isOperator = reader.readBit() == 1

                if (isOperator) {
                    // Operator processing
                    val opCode = reader.readBits(3)

                    // Check for End Of Expression (EOE) marker
                    if (opCode == 0b110) break

                    // Check for extended operator (111)
                    if (opCode == 0b111) {
                        val extOpCode = reader.readBits(4)
                        val operator = when (extOpCode) {
                            0b1010 -> " ( "  // Left parenthesis
                            0b1011 -> " ) "  // Right parenthesis
                            else -> " ?ext$extOpCode? "
                        }
                        expr.append(operator)
                    } else {
                        // Basic operators
                        val operator = when (opCode) {
                            0b001 -> " - "  // Minus
                            0b000 -> " + "  // Plus
                            0b010 -> " * "  // Multiply
                            0b011 -> " / "  // Divide
                            0b100 -> " ^ "  // Power
                            0b101 -> " = "  // Equals
                            else -> " ?$opCode? "
                        }
                        expr.append(operator)
                    }

                } else {
                    // Operand processing
                    val operandType = reader.readBits(2)

                    when (operandType) {
                        0b00 -> {
                            // Variable name/string operand
                            val str = decodeString()
                            expr.append(str)
                        }
                        0b01 -> {
                            // Constant operand
                            val constantSubType = reader.readBits(2)
                            when (constantSubType) {
                                0b00 -> {
                                    // Integer type
                                    val sign = if (reader.readBit() == 1) "-" else ""
                                    val value = eliasDeltaDecode(reader)
                                    val number = sign + value
                                    expr.append(number)
                                }
                                0b01 -> {
                                    // Boolean constant type not supported
                                }
                                else -> {
                                    // Unknown constant subtype
                                }
                            }
                        }
                        0b10 -> {
                            // FZ type operand encountered, skipping
                        }
                        0b11 -> {
                            // Reserved operand type encountered, skipping
                        }
                    }
                }

                // Prevent infinite loops
                if (expr.length > 500) {
                    break
                }

            } catch (e: Exception) {
                break
            }
        }

        return expr.toString().trim()
    }

    /**
     * Decode operation tags (MINUS, PLUS, STAR, MORE_OPERANDS)
     */
    private fun decodeTags(): List<String> {
        val tags = mutableListOf<String>()

        // Decode tags until end marker (000)
        while (reader.hasRemaining() && reader.peekBits(3) != 0b000) {
            val tag = reader.readBits(3)

            val tagName = when (tag) {
                0b001 -> "MINUS"
                0b010 -> "PLUS"
                0b011 -> "STAR"
                0b100 -> "MORE_OPERANDS"
                else -> null
            }

            if (tagName != null) {
                tags.add(tagName)
            }
        }

        return tags
    }

    /**
     * Decode string based on encoding type
     */
    private fun decodeString(): String {
        return when (reader.readBits(2)) {
            0b00 -> decodeAsciiString()
            0b01 -> decodeUtf8String()
            else -> throw IllegalArgumentException("Invalid string encoding")
        }
    }

    /**
     * Decode ASCII string (7-bit encoding)
     */
    private fun decodeAsciiString(): String {
        val chars = mutableListOf<Char>()
        while (true) {
            val charBits = reader.readBits(7)
            if (charBits == 0b0000011) break  // End of string marker
            chars.add(charBits.toChar())
        }
        return chars.joinToString("")
    }

    /**
     * Decode UTF-8 string (8-bit encoding)
     */
    private fun decodeUtf8String(): String {
        val bytes = mutableListOf<Byte>()
        while (true) {
            val charBits = reader.readBits(8)
            if (charBits == 0b00000011) break  // End of string marker
            bytes.add(charBits.toByte())
        }
        return bytes.toByteArray().toString(Charsets.UTF_8)
    }
}

/**
 * Bit Reader utility for reading binary data bit by bit
 */
class BitReader(private val bytes: ByteArray, startOffsetBits: Int = 0) {
    var bytePos = startOffsetBits / 8
    var bitPos = startOffsetBits % 8

    fun readBit(): Int {
        if (bytePos >= bytes.size) throw EOFException()
        val bit = (bytes[bytePos].toInt() ushr (7 - bitPos)) and 1
        if (++bitPos == 8) {
            bitPos = 0
            bytePos++
        }
        return bit
    }

    fun readBits(n: Int): Int {
        require(n in 1..32) { "Number of bits must be between 1 and 32" }
        var value = 0
        repeat(n) {
            value = (value shl 1) or readBit()
        }
        return value
    }

    fun peekBits(n: Int): Int {
        val savedPos = bytePos to bitPos
        val value = readBits(n)
        bytePos = savedPos.first
        bitPos = savedPos.second
        return value
    }

    fun skipBits(n: Int) {
        repeat(n) { readBit() }
    }

    fun hasRemaining(): Boolean = bytePos < bytes.size
}

/**
 * Elias Delta Decoding for unsigned integers
 */
private fun eliasDeltaDecode(reader: BitReader): Int {
    var zeros = 0
    while (reader.readBit() == 0) zeros++

    var gamma = 1
    repeat(zeros) {
        gamma = (gamma shl 1) or reader.readBit()
    }

    var result = 1
    for (i in 1 until gamma) {
        result = (result shl 1) or reader.readBit()
    }
    return result
}

/**
 * Elias Delta Decoding for signed integers using zigzag encoding
 */
private fun eliasDeltaDecodeSigned(reader: BitReader): Int {
    val flag = reader.readBit()
    if (flag == 0) return 0

    val zigzag = eliasDeltaDecode(reader)
    return (zigzag ushr 1) xor (-(zigzag and 1))
}