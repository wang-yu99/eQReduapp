package com.example.eqr_edu.userInterface.components
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Generic rounded button card component
 *
 * @param text Button text
 * @param onClick Click event
 * @param modifier Modifier
 * @param emoji Emoji (optional)
 * @param backgroundColor Background color
 * @param textColor Text color
 * @param height Button height, null uses ResponsiveConfig
 * @param fontSize Text size, null uses ResponsiveConfig
 * @param emojiSize Emoji size, null uses default value
 * @param showBorder Whether to show white border
 * @param elevation Shadow height
 */
@Composable
fun RoundedButtonCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    backgroundColor: Color = Color(0xFFFFD54F),
    textColor: Color = Color.White,
    height: Dp? = null,
    fontSize: TextUnit? = null,
    emojiSize: TextUnit? = null,
    showBorder: Boolean = true,
    elevation: Dp = 6.dp
) {
    val config = rememberResponsiveConfig()
    val buttonHeight = height ?: config.cardHeight
    val textSize = fontSize ?: config.bodyFontSize
    val emojiTextSize = emojiSize ?: if (config.isLandscape) 18.sp else 20.sp

    Card(
        modifier = modifier
            .height(buttonHeight)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(buttonHeight / 2),
                spotColor = backgroundColor.copy(alpha = 0.3f)
            )
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(buttonHeight / 2)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(buttonHeight / 2),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (emoji != null) {
                    Text(
                        text = emoji,
                        fontSize = emojiTextSize,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = text,
                    fontSize = textSize,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Rounded button card with label (for exercise type buttons)
 * Automatically capitalizes text, suitable for ExerciseSelection
 */
@Composable
fun ExerciseTypeButtonCard(
    tag: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String = "📝",
    backgroundColor: Color = Color(0xFF4CAF50)
) {
    val config = rememberResponsiveConfig()
    val buttonHeight = if (config.isLandscape) 64.dp else 80.dp

    RoundedButtonCard(
        text = text.uppercase(),
        onClick = onClick,
        modifier = modifier,
        emoji = emoji,
        backgroundColor = backgroundColor,
        height = buttonHeight,
        fontSize = config.bodyFontSize,
        emojiSize = if (config.isLandscape) 20.sp else 24.sp,
        showBorder = true,
        elevation = 8.dp
    )
}

/**
 * Solution card (for Solution list in HomePage)
 */
@Composable
fun SolutionButtonCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String = "🌊",
    backgroundColor: Color = Color(0xFF00BCD4),
    config: ResponsiveConfig
) {
    RoundedButtonCard(
        text = text,
        onClick = onClick,
        modifier = modifier,
        emoji = emoji,
        backgroundColor = backgroundColor,
        height = config.cardHeight,
        fontSize = config.bodyFontSize,
        emojiSize = if (config.isLandscape) 18.sp else 20.sp,
        showBorder = false,
        elevation = 6.dp
    )
}
