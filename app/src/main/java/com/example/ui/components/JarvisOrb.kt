package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.AssistantState
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.JarvisBlue
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDim

@Composable
fun JarvisOrb(
    state: AssistantState,
    audioRms: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_anim")

    // Gentle breathing pulse for idle
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_pulse"
    )

    // Fast rotation for processing/thinking
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    val primaryColor = when (state) {
        AssistantState.IDLE -> JarvisCyanDim
        AssistantState.WAKE_WORD_LISTENING -> JarvisCyan
        AssistantState.ACTIVATED -> AccentSuccess
        AssistantState.LISTENING -> JarvisCyan
        AssistantState.PROCESSING, AssistantState.EXECUTING -> JarvisBlue
        AssistantState.SPEAKING -> JarvisCyan
        AssistantState.ERROR -> AccentError
    }

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 3.4f

            // Dynamic scale depending on state and audio level
            val scale = when (state) {
                AssistantState.IDLE -> idlePulse
                AssistantState.WAKE_WORD_LISTENING -> idlePulse
                AssistantState.LISTENING -> 1.0f + (audioRms * 0.4f)
                AssistantState.SPEAKING -> idlePulse * 1.12f
                AssistantState.PROCESSING -> 1.0f
                AssistantState.ACTIVATED -> 1.2f
                AssistantState.ERROR -> 0.95f
                AssistantState.EXECUTING -> 1.05f
            }

            val currentRadius = baseRadius * scale

            // 1. Soft glowing outer ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.15f),
                radius = currentRadius + 22.dp.toPx(),
                center = center
            )

            // 2. Audio reactive middle ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.35f),
                radius = currentRadius + 10.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 3. Main solid core with radial gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.85f),
                        primaryColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius
                ),
                radius = currentRadius,
                center = center
            )

            // 4. Central high-intensity core dot
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx() * if (state == AssistantState.LISTENING) (1f + audioRms * 0.6f) else 1f,
                center = center
            )

            // 5. Orbiting arc indicators when processing/thinking
            if (state == AssistantState.PROCESSING || state == AssistantState.EXECUTING) {
                drawArc(
                    color = primaryColor,
                    startAngle = spinAngle,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(center.x - currentRadius - 14.dp.toPx(), center.y - currentRadius - 14.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size((currentRadius + 14.dp.toPx()) * 2, (currentRadius + 14.dp.toPx()) * 2),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}
