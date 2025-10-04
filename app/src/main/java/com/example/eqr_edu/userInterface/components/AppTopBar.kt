package com.example.eqr_edu.userInterface.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.eqr_edu.viewmodel.LanguageViewModel
import com.example.eqr_edu.viewmodel.stringResource

/**
 * Unified application top app bar
 *
 * @param title Title text
 * @param titleColor Title color
 * @param onNavigateBack Back button click event, null to hide back button
 * @param backIconColor Back button color
 * @param showHomeButton Whether to show home button
 * @param onNavigateHome Home button click event
 * @param homeIconColor Home button color
 * @param actions Additional action buttons (placed on the right side)
 * @param languageViewModel Language view model (for multi-language support)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    titleColor: Color = Color(0xFF2E7D32),
    onNavigateBack: (() -> Unit)? = null,
    backIconColor: Color = Color(0xFF2E7D32),
    showHomeButton: Boolean = true,
    onNavigateHome: (() -> Unit)? = null,
    homeIconColor: Color = Color(0xFFFF9800),
    backgroundColor: Color = Color.White,
    actions: @Composable () -> Unit = {},
    languageViewModel: LanguageViewModel? = null
) {
    val config = rememberResponsiveConfig()

    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = config.topAppBarFontSize,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = languageViewModel?.stringResource("back") ?: "Back",
                        tint = backIconColor
                    )
                }
            }
        },
        actions = {
            // Custom action buttons
            actions()

            // Home button
            if (showHomeButton && onNavigateHome != null) {
                IconButton(onClick = onNavigateHome) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = languageViewModel?.stringResource("home") ?: "Home",
                        tint = homeIconColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor.copy(alpha = 0.95f)
        )
    )
}