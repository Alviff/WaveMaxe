package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.VisualizerTemplate
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

// High-performance Particle structure for Avee particles system
private data class AveeParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    var angle: Float,
    var speed: Float,
    var color: Color
)

@Composable
fun AveeVisualizer(
    spectrum: FloatArray,
    template: VisualizerTemplate,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    // Rotation animation for the circular visualizer
    val infiniteTransition = rememberInfiniteTransition(label = "AveeRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = taylorTween(35000), // slow elegant rotation
            repeatMode = RepeatMode.Restart
        ),
        label = "Angle"
    )

    // Average energy of bass bands (indices 0 to 5)
    val bassEnergy = remember(spectrum) {
        if (spectrum.size >= 6) {
            spectrum.slice(0..5).average().toFloat()
        } else if (spectrum.isNotEmpty()) {
            spectrum.average().toFloat()
        } else {
            0.1f
        }
    }

    // Smoothly interpolate beat scale without complex, high-overhead suspension cancellations
    val targetScale = remember(isPlaying, bassEnergy, template.speedScale) {
        if (isPlaying && bassEnergy > 0.65f) {
            1.0f + (bassEnergy * 0.15f * template.speedScale).coerceAtMost(0.28f)
        } else {
            1.0f
        }
    }

    val beatScaleAmount by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "BeatScale"
    )

    // Initialize particles matching particleCount
    val maxParticles = template.particleCount.coerceIn(10, 200)
    val particles = remember { mutableStateListOf<AveeParticle>() }
    
    // Ambient touch interactive ripple values
    var interactiveRippleCenter by remember { mutableStateOf<Offset?>(null) }
    val interactiveRippleRadius = remember { Animatable(0f) }
    val corScope = rememberCoroutineScope()

    LaunchedEffect(maxParticles) {
        particles.clear()
        val random = Random(42)
        for (i in 0 until maxParticles) {
            val angle = random.nextFloat() * 2 * PI.toFloat()
            val speed = (1f + random.nextFloat() * 2f) * template.speedScale
            particles.add(
                AveeParticle(
                    x = 0f, // updated dynamically on canvas size known
                    y = 0f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    radius = (dpToPx(2f) + random.nextFloat() * dpToPx(5f)) * template.sizeScale,
                    alpha = 0.2f + random.nextFloat() * 0.7f,
                    angle = angle,
                    speed = speed,
                    color = if (random.nextBoolean()) Color.parseHex(template.primaryColor) else Color.parseHex(template.secondaryColor)
                )
            )
        }
    }

    // Reactively refresh particles colors when template modifications occur
    LaunchedEffect(template.primaryColor, template.secondaryColor) {
        val rand = Random(24)
        particles.forEach { p ->
            p.color = if (rand.nextBoolean()) Color.parseHex(template.primaryColor) else Color.parseHex(template.secondaryColor)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Interactive cyberpunk shockwave trigger
                        interactiveRippleCenter = offset
                        corScope.launch {
                            interactiveRippleRadius.snapTo(0f)
                            interactiveRippleRadius.animateTo(
                                targetValue = 500f,
                                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
                            )
                            interactiveRippleCenter = null
                        }
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val baseRadius = (size.width.coerceAtMost(size.height) * 0.22f) * beatScaleAmount

            // Draw Background style gradient overlay
            drawVisualizerBackground(template, isPlaying, bassEnergy)

            // Update and Draw floating particles
            updateAndDrawParticles(
                particles = particles,
                cx = cx,
                cy = cy,
                isPlaying = isPlaying,
                bassEnergy = bassEnergy,
                template = template,
                baseRadius = baseRadius
            )

            // Draw customizable interactive ripple wave
            interactiveRippleCenter?.let { center ->
                drawCircle(
                    color = Color.parseHex(template.glowColor).copy(alpha = (1.0f - interactiveRippleRadius.value / 500f).coerceIn(0f, 1f)),
                    radius = interactiveRippleRadius.value,
                    center = center,
                    style = Stroke(width = 4f)
                )
            }

            // Draw specific Layout Waveform Architecture
            when (template.layoutStyle) {
                "Circular Wave" -> {
                    rotate(rotationAngle, pivot = Offset(cx, cy)) {
                        drawCircularWave(
                            cx = cx,
                            cy = cy,
                            radius = baseRadius,
                            spectrum = spectrum,
                            primaryColor = Color.parseHex(template.primaryColor),
                            secondaryColor = Color.parseHex(template.secondaryColor),
                            glowColor = Color.parseHex(template.glowColor),
                            isGlowEnabled = template.isGlowEnabled
                        )
                    }
                }
                "Spectrum Bars" -> {
                    drawSpectrumBars(
                        cx = cx,
                        cy = cy,
                        spectrum = spectrum,
                        primaryColor = Color.parseHex(template.primaryColor),
                        secondaryColor = Color.parseHex(template.secondaryColor),
                        glowColor = Color.parseHex(template.glowColor),
                        isGlowEnabled = template.isGlowEnabled
                    )
                }
                "Neon Starburst" -> {
                    rotate(rotationAngle * 1.5f, pivot = Offset(cx, cy)) {
                        drawNeonStarburst(
                            cx = cx,
                            cy = cy,
                            spectrum = spectrum,
                            primaryColor = Color.parseHex(template.primaryColor),
                            secondaryColor = Color.parseHex(template.secondaryColor)
                        )
                    }
                }
                "Line Wave" -> {
                    drawLineWave(
                        cx = cx,
                        cy = cy,
                        spectrum = spectrum,
                        primaryColor = Color.parseHex(template.primaryColor),
                        secondaryColor = Color.parseHex(template.secondaryColor),
                        isPlaying = isPlaying
                    )
                }
            }

            // Central rotating core core-accent
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.parseHex(template.primaryColor).copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = baseRadius * 0.65f
                ),
                radius = baseRadius * 0.65f,
                center = Offset(cx, cy)
            )
        }
    }
}

// Specialize tween generator that supports custom durations
private fun taylorTween(duration: Int): DurationBasedAnimationSpec<Float> {
    return tween(durationMillis = duration, easing = LinearEasing)
}

private fun Color.Companion.parseHex(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.Cyan // fallback safe
    }
}

private fun Float.dpToPx(density: Float = 2.5f): Float = this * density
private fun dpToPx(dp: Float): Float = dp * 2.5f

private fun DrawScope.drawVisualizerBackground(template: VisualizerTemplate, isPlaying: Boolean, bassEnergy: Float) {
    val alphaFactor = if (isPlaying) 0.15f + (bassEnergy * 0.1f).coerceAtMost(0.2f) else 0.1f
    when (template.backgroundType) {
        "Dark Cyber" -> {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.parseHex(template.primaryColor).copy(alpha = alphaFactor * 0.8f),
                        Color(0xFF04040A)
                    ),
                    center = center,
                    radius = size.width * 0.5f
                )
            )
        }
        "Neon Purple" -> {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1B0323),
                        Color(0xFF04040A),
                        Color.parseHex(template.secondaryColor).copy(alpha = alphaFactor * 0.5f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }
        "Glow Blue" -> {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.parseHex(template.secondaryColor).copy(alpha = alphaFactor * 0.3f),
                        Color(0xFF030A1C),
                        Color.parseHex(template.primaryColor).copy(alpha = alphaFactor * 0.4f)
                    )
                )
            )
        }
        else -> {
            drawRect(color = Color(0xFF05050C))
        }
    }
}

private fun DrawScope.updateAndDrawParticles(
    particles: List<AveeParticle>,
    cx: Float,
    cy: Float,
    isPlaying: Boolean,
    bassEnergy: Float,
    template: VisualizerTemplate,
    baseRadius: Float
) {
    val random = Random(15)
    particles.forEach { p ->
        // Reposition centered around orbit if they float out of bounds
        if (p.x == 0f || p.y == 0f) {
            val startDist = baseRadius * 0.9f
            p.x = cx + cos(p.angle) * startDist
            p.y = cy + sin(p.angle) * startDist
        }

        // Speed increases dynamically when high bass kicks
        val currentSpeed = if (isPlaying) {
            p.speed * (1.0f + bassEnergy * 0.6f * template.speedScale)
        } else {
            p.speed * 0.15f
        }

        // Update positions outward
        p.x += cos(p.angle) * currentSpeed
        p.y += sin(p.angle) * currentSpeed

        // Recycle particles if they exit screen bounds
        val distSquare = (p.x - cx).pow(2) + (p.y - cy).pow(2)
        val limit = (size.width * 1.5f).pow(2)
        if (distSquare > limit) {
            val angle = random.nextFloat() * 2 * PI.toFloat()
            p.angle = angle
            val startDist = baseRadius * (0.4f + random.nextFloat() * 0.5f)
            p.x = cx + cos(p.angle) * startDist
            p.y = cy + sin(p.angle) * startDist
        }

        // Pulse size reactively based on beat
        val pulseSize = if (isPlaying && template.isBeatReactive) {
            p.radius * (1.0f + bassEnergy * 0.5f)
        } else {
            p.radius
        }

        // Draw particle glowing dots
        drawCircle(
            color = p.color.copy(alpha = p.alpha),
            radius = pulseSize,
            center = Offset(p.x, p.y)
        )
    }
}

private fun DrawScope.drawCircularWave(
    cx: Float,
    cy: Float,
    radius: Float,
    spectrum: FloatArray,
    primaryColor: Color,
    secondaryColor: Color,
    glowColor: Color,
    isGlowEnabled: Boolean
) {
    if (spectrum.size < 6) return
    val pathPrimary = Path()
    val pathSecondary = Path()
    val pointsCount = 120
    val spectrumSize = spectrum.size

    for (i in 0 until pointsCount) {
        val angle = i * (2f * PI.toFloat() / pointsCount)
        
        // Map points indices to matching spectrum bands
        val spectrumIdx = (i % (spectrumSize / 2))
        val sampleVal = spectrum[spectrumIdx]
        val ampFactor = if (i % 2 == 0) sampleVal else spectrum[(spectrumIdx + 1) % spectrumSize]

        val deltaRadiusPrimary = ampFactor * radius * 0.45f
        val rPrimary = radius + deltaRadiusPrimary
        
        val xPrimary = cx + cos(angle) * rPrimary
        val yPrimary = cy + sin(angle) * rPrimary

        if (i == 0) {
            pathPrimary.moveTo(xPrimary, yPrimary)
        } else {
            pathPrimary.lineTo(xPrimary, yPrimary)
        }

        // Complementary layer shifting backwards
        val deltaRadiusSecondary = spectrum[(spectrumSize - 1 - spectrumIdx)] * radius * 0.35f
        val rSecondary = radius - deltaRadiusSecondary
        val xSecondary = cx + cos(angle + PI.toFloat() / pointsCount) * rSecondary
        val ySecondary = cy + sin(angle + PI.toFloat() / pointsCount) * rSecondary

        if (i == 0) {
            pathSecondary.moveTo(xSecondary, ySecondary)
        } else {
            pathSecondary.lineTo(xSecondary, ySecondary)
        }
    }
    pathPrimary.close()
    pathSecondary.close()

    // Draw secondary inner layer
    drawPath(
        path = pathSecondary,
        color = secondaryColor.copy(alpha = 0.55f),
        style = Stroke(width = dpToPx(3f))
    )

    // Draw primary outer glowing layer
    if (isGlowEnabled) {
        drawPath(
            path = pathPrimary,
            color = glowColor.copy(alpha = 0.25f),
            style = Stroke(width = dpToPx(8f))
        )
    }
    drawPath(
        path = pathPrimary,
        color = primaryColor,
        style = Stroke(width = dpToPx(4f))
    )
}

private fun DrawScope.drawSpectrumBars(
    cx: Float,
    cy: Float,
    spectrum: FloatArray,
    primaryColor: Color,
    secondaryColor: Color,
    glowColor: Color,
    isGlowEnabled: Boolean
) {
    if (spectrum.size < 6) return
    val barCount = spectrum.size
    val spacing = size.width / (barCount * 1.5f)
    val barWidth = spacing * 0.75f
    val paddingSide = (size.width - (barCount * spacing)) / 2f
    val maxHeight = size.height * 0.38f

    for (i in 0 until barCount) {
        val amp = spectrum[i]
        val currentHeight = (amp * maxHeight).coerceAtMost(maxHeight)
        val x = paddingSide + i * spacing
        val y = cy * 1.4f - currentHeight // position near the lower half

        // Rounded glowing spectrum rectangle
        val rectSize = Size(barWidth, currentHeight)
        val verticalOffset = Offset(x, y)

        if (isGlowEnabled) {
            drawRect(
                color = glowColor.copy(alpha = 0.25f),
                topLeft = verticalOffset - Offset(2f, 2f),
                size = Size(rectSize.width + 4f, rectSize.height + 4f)
            )
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor, secondaryColor),
                startY = y,
                endY = y + currentHeight
            ),
            topLeft = verticalOffset,
            size = rectSize
        )

        // Draw symmetrical reflection underneath (Avee Style!)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(secondaryColor.copy(alpha = 0.25f), Color.Transparent),
                startY = cy * 1.4f,
                endY = cy * 1.4f + currentHeight * 0.5f
            ),
            topLeft = Offset(x, cy * 1.4f),
            size = Size(barWidth, currentHeight * 0.5f)
        )
    }
}

private fun DrawScope.drawNeonStarburst(
    cx: Float,
    cy: Float,
    spectrum: FloatArray,
    primaryColor: Color,
    secondaryColor: Color
) {
    if (spectrum.size < 6) return
    val raysCount = 64
    val minLen = size.width * 0.18f
    val maxLenMultiplier = size.width * 0.22f

    for (i in 0 until raysCount) {
        val angle = i * (2f * PI.toFloat() / raysCount)
        val amp = spectrum[i % spectrum.size]
        
        val rayLength = minLen + amp * maxLenMultiplier
        val xEnd = cx + cos(angle) * rayLength
        val yEnd = cy + sin(angle) * rayLength

        // Alternating color neon rays
        val color = if (i % 2 == 0) primaryColor else secondaryColor
        
        drawLine(
            color = color.copy(alpha = 0.75f),
            start = Offset(cx, cy),
            end = Offset(xEnd, yEnd),
            strokeWidth = 3f
        )

        // Glow spark dot at ray tips
        drawCircle(
            color = color,
            radius = 4f + amp * 6f,
            center = Offset(xEnd, yEnd)
        )
    }
}

private fun DrawScope.drawLineWave(
    cx: Float,
    cy: Float,
    spectrum: FloatArray,
    primaryColor: Color,
    secondaryColor: Color,
    isPlaying: Boolean
) {
    if (spectrum.size < 6) return
    val path = Path()
    val count = size.width.toInt()

    path.moveTo(0f, cy)
    for (x in 0 until count step 6) {
        // Calculate average amplitude of spectrum to affect the overall frequency warp
        val percent = x.toFloat() / size.width
        val specIdx = ((percent * (spectrum.size - 1)).toInt()).coerceIn(0, spectrum.size - 1)
        val amp = spectrum[specIdx]

        // Formula drawing compound glowing wave
        val sineWave = sin(percent * PI * 8 + (if (isPlaying) System.currentTimeMillis() * 0.003f else 0f)) * 40f * amp
        val cosineWave = cos(percent * PI * 4 + (if (isPlaying) System.currentTimeMillis() * 0.005f else 0f)) * 20f * amp
        val finalY = cy + (sineWave + cosineWave)

        path.lineTo(x.toFloat(), finalY.toFloat())
    }

    // Draw main wave layer
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor)),
        style = Stroke(width = dpToPx(4f), cap = StrokeCap.Round)
    )

    // Shadow duplicate layer for visual thickness
    drawPath(
        path = path,
        color = secondaryColor.copy(alpha = 0.2f),
        style = Stroke(width = dpToPx(12f), cap = StrokeCap.Round)
    )
}
