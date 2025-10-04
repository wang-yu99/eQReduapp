package com.example.eqr_edu.userInterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eqr_edu.viewmodel.Language
import com.example.eqr_edu.viewmodel.LanguageViewModel
import com.example.eqr_edu.viewmodel.collectLanguageAsState

@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier,
    languageViewModel: LanguageViewModel = viewModel()
) {
    val languageState by languageViewModel.collectLanguageAsState()
    val currentLanguage = languageState.currentLanguage

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            onClick = {
                if (!languageState.isLoading) {
                    expanded = !expanded
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (languageState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF2196F3)
                    )
                } else {
                    Text(
                        text = getLanguageEmoji(currentLanguage),
                        fontSize = 18.sp
                    )
                }
            }
        }

        // dropdown menu
        DropdownMenu(
            expanded = expanded && !languageState.isLoading,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        ) {
            languageViewModel.getAvailableLanguages().forEach { language ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getLanguageEmoji(language),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = language.displayName,
                                fontSize = 14.sp,
                                color = if (language == currentLanguage)
                                    Color(0xFF2196F3) else Color(0xFF666666)
                            )
                        }
                    },
                    onClick = {
                        languageViewModel.switchLanguage(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun getLanguageEmoji(language: Language): String = when (language) {
    Language.ENGLISH -> "🇺🇸"
    Language.ITALIAN -> "🇮🇹"
    Language.CHINESE -> "🇨🇳"
}

