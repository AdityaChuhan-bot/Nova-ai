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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.data.model.AssistantState
import com.example.ui.theme.AccentError
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.JarvisBlue
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanDim

@Composable
fun JarvisOrb(
    state: AssistantState,
    audioRms: Float,
    modifier: Modifier = Modifier,
    lowResourceMode: Boolean = false
) {
    // Only run animations if active or if not in low-resource idle mode
    val shouldAnimatePulse = !lowResourceMode && (state == AssistantState.IDLE || state == AssistantState.WAKE_WORD_LISTENING || state == AssistantState.SPEAKING)
    val shouldAnimateSpin = state == AssistantState.PROCESSING || state == AssistantState.EXECUTING

    val infiniteTransition = rememberInfiniteTransition(label = "orb_anim")

    val idlePulse by if (shouldAnimatePulse) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "idle_pulse"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    val spinAngle by if (shouldAnimateSpin) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin_angle"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val primaryColor = remember(state) {
        when (state) {
            AssistantState.IDLE -> JarvisCyanDim
            AssistantState.WAKE_WORD_LISTENING -> JarvisCyan
            AssistantState.ACTIVATED -> AccentSuccess
            AssistantState.LISTENING -> JarvisCyan
            AssistantState.PROCESSING, AssistantState.EXECUTING -> JarvisBlue
            AssistantState.SPEAKING -> JarvisCyan
            AssistantState.ERROR -> AccentError
        }
    }

    val density = LocalDensity.current
    val strokeWidth2Px = remember(density) { with(density) { 2.dp.toPx() } }
    val strokeWidth3Px = remember(density) { with(density) { 3.dp.toPx() } }
    val outerRingOffsetPx = remember(density) { with(density) { 18.dp.toPx() } }
    val middleRingOffsetPx = remember(density) { with(density) { 8.dp.toPx() } }
    val arcOffsetPx = remember(density) { with(density) { 12.dp.toPx() } }
    val coreDotRadiusPx = remember(density) { with(density) { 6.dp.toPx() } }

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 3.4f

            // Dynamic scale depending on state and audio level
            val scale = when (state) {
                AssistantState.IDLE, AssistantState.WAKE_WORD_LISTENING -> idlePulse
                AssistantState.LISTENING -> 1.0f + (audioRms * 0.35f).coerceAtMost(0.5f)
                AssistantState.SPEAKING -> if (lowResourceMode) 1.05f else idlePulse * 1.1f
                AssistantState.PROCESSING -> 1.0f
                AssistantState.ACTIVATED -> 1.15f
                AssistantState.ERROR -> 0.95f
                AssistantState.EXECUTING -> 1.05f
            }

            val currentRadius = baseRadius * scale

            // 1. Soft glowing outer ring (skip in lowResourceMode when idle to save GPU fill rate)
            if (!lowResourceMode || state != AssistantState.IDLE) {
                drawCircle(
                    color = primaryColor.copy(alpha = 0.15f),
                    radius = currentRadius + outerRingOffsetPx,
                    center = center
                )
            }

            // 2. Audio reactive middle ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.35f),
                radius = currentRadius + middleRingOffsetPx,
                center = center,
                style = Stroke(width = strokeWidth2Px)
            )

            // 3. Main solid core
            if (lowResourceMode) {
                // Flat solid fill in low-resource mode avoids heavy GPU radial shader calculation
                drawCircle(
                    color = primaryColor.copy(alpha = 0.6f),
                    radius = currentRadius,
                    center = center
                )
            } else {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.85f),
                            primaryColor.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = currentRadius.coerceAtLeast(1f)
                    ),
                    radius = currentRadius,
                    center = center
                )
            }

            // 4. Central high-intensity core dot
            val dotScale = if (state == AssistantState.LISTENING) (1f + audioRms * 0.5f).coerceAtMost(1.6f) else 1f
            drawCircle(
                color = Color.White,
                radius = coreDotRadiusPx * dotScale,
                center = center
            )

            // 5. Orbiting arc indicators when processing/thinking
            if (state == AssistantState.PROCESSING || state == AssistantState.EXECUTING) {
                val arcRadius = currentRadius + arcOffsetPx
                drawArc(
                    color = primaryColor,
                    startAngle = spinAngle,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = strokeWidth3Px)
                )
            }
        }
    }
}

