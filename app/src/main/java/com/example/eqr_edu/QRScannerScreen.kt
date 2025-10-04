
package com.example.eqr_edu
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.zxing.integration.android.IntentIntegrator

@Composable
fun QRScannerScreen(
    onScanResult: (ByteArray, Int) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var errorDetail by remember { mutableStateOf("") }

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(
            result.resultCode,
            result.data
        )

        if (intentResult != null) {
            if (intentResult.contents == null) {
                // Scan cancelled
                showError = true
                errorMessage = "Scan cancelled"
                errorDetail = "Please scan a valid eQR educational QR code again"
            } else {
                // Get raw byte data
                val bytes = intentResult.originalIntent
                    ?.extras
                    ?.getByteArray("SCAN_RESULT_BYTE_SEGMENTS_0")

                if (bytes != null) {
                    // Validate if it's a valid eQR educational format
                    val validationResult = validateEQREducationalFormat(bytes)

                    if (validationResult.isValid) {
                        // Validation passed, process eQR educational content
                        Log.i("QRScanner", "eQR Binary Data: ${bytesToBinary(bytes)}")

                        // Clear error state
                        showError = false
                        errorMessage = ""
                        errorDetail = ""

                        // Callback with result
                        onScanResult(bytes, bytes.size)

                    } else {
                        // Validation failed
                        showError = true
                        errorMessage = "Invalid eQR format"
                        errorDetail = validationResult.errorMessage
                    }
                } else {
                    // This is a text QR code, not binary eQR format
                    val textContent = intentResult.contents ?: ""
                    showError = true
                    errorMessage = "Unsupported QR code format"
                    errorDetail = "Detected text content: \"${textContent.take(50)}${if (textContent.length > 50) "..." else ""}\"\n\nPlease scan a valid eQR educational QR code (binary format)"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title and description
        Text(
            text = "eQR Educational QR Code Scanner",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Only supports eQR compliant educational QR codes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                try {
                    val integrator = IntentIntegrator(activity)
                        .setPrompt("Please scan an educational eQR code")
                        .setOrientationLocked(true)
                        .setBeepEnabled(true)
                    scanLauncher.launch(integrator.createScanIntent())

                    // Clear previous error state
                    showError = false
                    errorMessage = ""
                    errorDetail = ""

                } catch (e: Exception) {
                    errorMessage = "Scanner startup failed"
                    errorDetail = e.message ?: "Unknown error"
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Scan eQR Educational QR Code",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        // Error display area
        if (showError) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "❌",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    if (errorDetail.isNotEmpty()) {
                        Text(
                            text = errorDetail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Retry button
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            showError = false
                            errorMessage = ""
                            errorDetail = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Scan Again")
                    }
                }
            }
        }
    }
}

/**
 * Validation result data class
 */
private data class EQRValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)

/**
 * Validate eQR educational format
 * Uses existing QRBinaryDecoder for validation to avoid code duplication
 */
private fun validateEQREducationalFormat(bytes: ByteArray): EQRValidationResult {
    return try {
        // Check minimum length
        if (bytes.size < 4) {
            return EQRValidationResult(false, "QR code data too short, does not meet eQR format requirements (${bytes.size} bytes)")
        }

        // Use existing QRBinaryDecoder for validation and decoding
        // This ensures validation logic is consistent with actual decoding logic
        try {
            val testDecoder = QRBinaryDecoder(bytes)
            val testIL = testDecoder.decode()

            // Check if expected educational content was decoded
            val hasLabels = testIL.labels.isNotEmpty()
            val hasGenerators = testIL.randGenerators.isNotEmpty()
            val hasSolutions = testIL.solutions.isNotEmpty()
            val hasExercises = testIL.exercises.isNotEmpty()

            val hasContent = hasLabels || hasGenerators || hasSolutions || hasExercises

            if (!hasContent) {
                return EQRValidationResult(false, "Decoding successful but no valid educational content found (labels, generators, solutions, or exercises)")
            }

            return EQRValidationResult(true, "")

        } catch (e: IllegalArgumentException) {
            // This usually indicates it's not a valid eQR format
            return EQRValidationResult(false, "Not a valid eQR educational format: ${e.message}")
        } catch (e: Exception) {
            // Other decoding errors
            return EQRValidationResult(false, "eQR educational content decoding failed: ${e.message}")
        }

    } catch (e: Exception) {
        EQRValidationResult(false, "Validation process error: ${e.message}")
    }
}

fun bytesToBinary(bytes: ByteArray): String {
    val binaryString = StringBuilder()
    for (b in bytes) {
        val binary = String.format("%8s", Integer.toBinaryString(b.toInt() and 0xFF))
            .replace(' ', '0')
        binaryString.append(binary).append(" ")
    }
    return binaryString.toString().trim()
}
