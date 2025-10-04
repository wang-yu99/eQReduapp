package com.example.eqr_edu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.eqr_edu.ui.theme.EQREduTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EQREduTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNavigation(
                        onProcessQRResult = { bytes, size ->
                            processQRCodeWithStackVM(bytes, size)
                        }
                    )
                }
            }
        }
    }


    private fun processQRCodeWithStackVM(bytes: ByteArray, size: Int) {
        try {
            val decoder = QRBinaryDecoder(bytes)
            val il = decoder.decode()
            val vm = EducationalVM(il)
            vm.initializeVariables()
            vm.executeExercises()
        } catch (e: Exception) {
            // handle QR code processing errors
        }
    }
}