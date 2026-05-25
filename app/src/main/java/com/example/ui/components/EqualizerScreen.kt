package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EqualizerScreen(
    eqBands: Map<String, Float>,
    bassBoost: Float,
    virtual3D: Float,
    presetName: String,
    onAdjustBand: (String, Float) -> Unit,
    onAdjustBass: (Float) -> Unit,
    onAdjustVirtual: (Float) -> Unit,
    onSelectPreset: (String) -> Unit
) {
    val presets = listOf("CyberBass", "VocalBoost", "Lofi Space", "SynthWave Retro", "Flat Accent")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp)
    ) {
        // Section Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp, top = 28.dp)
        ) {
            Icon(
                Icons.Default.Tune,
                contentDescription = "Tune",
                tint = CyberCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NEURAL EQUALIZER",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Badge(
                containerColor = NeonPink.copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = presetName.uppercase(),
                    color = NeonPink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Quick Presets Selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            items(presets) { preset ->
                val isSelected = preset == presetName
                var modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) CyberCyan else GlassBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectPreset(preset) }
                
                if (isSelected) {
                    modifier = modifier.background(CyberCyan.copy(alpha = 0.12f))
                } else {
                    modifier = modifier.background(CyberDarkSurface)
                }

                Box(
                    modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = preset,
                        color = if (isSelected) CyberCyan else MutedText,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // 5-Band Vertically Structured Sliders Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberDarkSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                eqBands.forEach { (band, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "${value.toInt()}dB",
                            color = if (value > 2f) NeonPink else CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        // Vertical slider simulator (Custom Compose Slider since Android standard lacks vertical natively)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(36.dp)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Back rail track
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(4.dp)
                                    .clip(CircleShape)
                                    .background(CyberMutedSurface)
                            )

                            // Slider drag gesture controller
                            var boxHeight by remember { mutableStateOf(200f) }
                            val limitBottom = -10f
                            val limitTop = 10f

                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(band) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            val dy = dragAmount.y
                                            // Convert pixels dragging directly to dB scale values
                                            val valRange = limitTop - limitBottom
                                            val stepDb = -(dy / boxHeight) * valRange
                                            val resultDb = (value + stepDb).coerceIn(limitBottom, limitTop)
                                            onAdjustBand(band, resultDb)
                                        }
                                    }
                            ) {
                                boxHeight = size.height
                                val normalized = (value - limitBottom) / (limitTop - limitBottom)
                                val thumbY = size.height * (1f - normalized)

                                // Active highlight rail
                                drawLine(
                                    color = CyberCyan,
                                    start = Offset(size.width / 2, size.height),
                                    end = Offset(size.width / 2, thumbY),
                                    strokeWidth = 4.dp.toPx(),
                                    cap = StrokeCap.Round
                                )

                                // Glowing thumb circle
                                drawCircle(
                                    color = NeonPink,
                                    radius = 11.dp.toPx(),
                                    center = Offset(size.width / 2, thumbY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 5.dp.toPx(),
                                    center = Offset(size.width / 2, thumbY)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = band,
                            color = MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Advanced 3D Dial Knobs Section: Bass Boost & Spatial 3D Virtualizer
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            // Bass Boost Dial
            DialKnobCard(
                title = "HYPER BASS",
                value = bassBoost,
                maxVal = 10f,
                accentColor = NeonPink,
                onValueChange = onAdjustBass,
                modifier = Modifier.weight(1f)
            )

            // Virtual 3D Spatializer Dial
            DialKnobCard(
                title = "SURROUND 3D",
                value = virtual3D,
                maxVal = 10f,
                accentColor = CyberCyan,
                onValueChange = onAdjustVirtual,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DialKnobCard(
    title: String,
    value: Float,
    maxVal: Float,
    accentColor: Color,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CyberDarkSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic knob graphic with touch drag detection
            var canvasSize by remember { mutableStateOf(100f) }
            val animatedKnobAngle by animateFloatAsState(
                targetValue = (value / maxVal) * 270f - 135f, // mapping from -135 to 135 degrees
                label = "KnobAngle"
            )

            Box(
                modifier = Modifier
                    .size(85.dp)
                    .pointerInput(title) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            // Calculate simple linear increase/decrease on drag
                            val delta = -dragAmount.y * (maxVal / canvasSize) * 0.45f
                            val result = (value + delta).coerceIn(0f, maxVal)
                            onValueChange(result)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    canvasSize = size.height
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val radius = size.width / 2 * 0.8f

                    // Draw back arc track background
                    drawArc(
                        color = CyberMutedSurface,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(cx - radius, cy - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Draw active glowing colored arc
                    drawArc(
                        color = accentColor,
                        startAngle = 135f,
                        sweepAngle = (value / maxVal) * 270f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(cx - radius, cy - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Central rotating knob disk base
                    drawCircle(
                        color = CyberMutedSurface,
                        radius = radius * 0.72f,
                        center = Offset(cx, cy)
                    )

                    // Rotating indicator notch marker line
                    val angleRad = Math.toRadians(animatedKnobAngle.toDouble() - 90.0)
                    val notchStartX = cx + cos(angleRad) * (radius * 0.2f)
                    val notchStartY = cy + sin(angleRad) * (radius * 0.2f)
                    val notchEndX = cx + cos(angleRad) * (radius * 0.72f)
                    val notchEndY = cy + sin(angleRad) * (radius * 0.72f)

                    drawLine(
                        color = accentColor,
                        start = Offset(notchStartX.toFloat(), notchStartY.toFloat()),
                        end = Offset(notchEndX.toFloat(), notchEndY.toFloat()),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = String.format("%.1f", value),
                color = accentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
