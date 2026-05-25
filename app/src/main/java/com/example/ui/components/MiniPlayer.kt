package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Song
import com.example.ui.theme.*

@Composable
fun MiniPlayer(
    currentSong: Song,
    isPlaying: Boolean,
    progress: Float,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotation animation for mini disc cover
    val infiniteTransition = rememberInfiniteTransition(label = "MiniDisc")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MiniRotate"
    )

    // Gesture controller for swipe tracking
    var dragAccumulatorX by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1AFFFFFF), // Translucent white (10% opacity)
                        Color(0x0DFFFFFF)  // Translucent white (5% opacity)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0x22FFFFFF), Color(0x08FFFFFF))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .pointerInput(currentSong.id) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragAccumulatorX += dragAmount.x
                    },
                    onDragEnd = {
                        // Swipe gesture triggers
                        if (dragAccumulatorX > 140f) {
                            onSkipPrevious()
                        } else if (dragAccumulatorX < -140f) {
                            onSkipNext()
                        }
                        dragAccumulatorX = 0f
                    },
                    onDragCancel = {
                        dragAccumulatorX = 0f
                    }
                )
            }
            .clickable { onOpenFullPlayer() }
            .padding(10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Mini rotating cover disk icon
                Image(
                    painter = rememberAsyncImagePainter(currentSong.imageUrl),
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, CyberCyan, CircleShape)
                        .rotate(if (isPlaying) rotationAngle else 0f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Song Info details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentSong.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentSong.artist,
                        color = MutedText,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Controls
                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle play pause",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onSkipNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next song",
                        tint = MutedText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Micro progress track line at bottom of mini player card
            LinearProgressIndicator(
                progress = progress,
                trackColor = CyberMutedSurface,
                color = CyberCyan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .clip(CircleShape)
            )
        }
    }
}
