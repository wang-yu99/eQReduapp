package com.example.eqr_edu.userInterface.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive configuration data class - Provides different size parameters based on landscape/portrait orientation
 */
data class ResponsiveConfig(
    val isLandscape: Boolean,

    // Spacing configuration
    val screenPadding: Dp,
    val contentPadding: Dp,
    val itemSpacing: Dp,
    val sectionSpacing: Dp,

    // Font configuration
    val titleFontSize: TextUnit,
    val headlineFontSize: TextUnit,
    val bodyFontSize: TextUnit,
    val captionFontSize: TextUnit,

    // Component size configuration
    val buttonHeight: Dp,
    val cardHeight: Dp,
    val imageHeight: Dp,
    val iconSize: Dp,

    // Special configuration
    val exerciseCardPadding: Dp,
    val topAppBarFontSize: TextUnit
) {
    companion object {
        /**
         * Create default responsive configuration
         */
        fun create(isLandscape: Boolean): ResponsiveConfig {
            return if (isLandscape) {
                // Landscape configuration - All sizes more compact
                ResponsiveConfig(
                    isLandscape = true,
                    screenPadding = 8.dp,
                    contentPadding = 12.dp,
                    itemSpacing = 10.dp,
                    sectionSpacing = 16.dp,
                    titleFontSize = 22.sp,
                    headlineFontSize = 20.sp,
                    bodyFontSize = 16.sp,
                    captionFontSize = 12.sp,
                    buttonHeight = 48.dp,
                    cardHeight = 60.dp,
                    imageHeight = 140.dp,
                    iconSize = 44.dp,
                    exerciseCardPadding = 20.dp,
                    topAppBarFontSize = 18.sp
                )
            } else {
                // Portrait configuration - Standard sizes
                ResponsiveConfig(
                    isLandscape = false,
                    screenPadding = 16.dp,
                    contentPadding = 16.dp,
                    itemSpacing = 12.dp,
                    sectionSpacing = 24.dp,
                    titleFontSize = 26.sp,
                    headlineFontSize = 22.sp,
                    bodyFontSize = 18.sp,
                    captionFontSize = 14.sp,
                    buttonHeight = 56.dp,
                    cardHeight = 72.dp,
                    imageHeight = 200.dp,
                    iconSize = 48.dp,
                    exerciseCardPadding = 32.dp,
                    topAppBarFontSize = 20.sp
                )
            }
        }
    }
}

/**
 * Composable function - Remember and return responsive configuration
 */
@Composable
fun rememberResponsiveConfig(): ResponsiveConfig {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    return remember(isLandscape) {
        ResponsiveConfig.create(isLandscape)
    }
}