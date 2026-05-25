package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.api.AIDeclaredPlaylistResponse
import com.example.data.model.Song
import com.example.ui.theme.*

@Composable
fun AICopilotScreen(
    prompt: String,
    response: AIDeclaredPlaylistResponse?,
    isThinking: Boolean,
    availableSongs: List<Song>,
    onPromptChange: (String) -> Unit,
    onSubmitPrompt: () -> Unit,
    onLoadAIPlaylist: (String, List<Song>, String) -> Unit
) {
    val quickPrompts = listOf(
        "Adrenaline Outrun on Skybridge",
        "Cosmic Lofi for asteroid drifting",
        "Retro VHS Shibuya Rain mood",
        "Heavy Glitch Dark synth overload"
    )

    // Animated loading/thinking indicators
    val infiniteTransition = rememberInfiniteTransition(label = "ThinkingAnim")
    val binaryOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Offset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp, top = 28.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = CyberCyan, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "NEURO-COPILOT CHAT",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        // Quick suggestions row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            items(quickPrompts) { text ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CyberDarkSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                        .clickable { onPromptChange(text) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = text, color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Scrollable central chat log
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberDarkSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            if (response == null && !isThinking) {
                // Empty state console instruction
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Copilot", tint = CyberCyan.copy(alpha = 0.35f), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NEURAL PORT MOUNTED\nFEED PROMPTS TO CURATE THE FUTURE",
                            color = MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // User Prompt Display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .align(Alignment.CenterStart)
                                .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                                .background(CyberMutedSurface)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = prompt,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isThinking) {
                        item {
                            // Thinking rolling matrix display
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clip(RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp))
                                    .background(Color(0xFF030307))
                                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "SYNAPSE LINK CONVERTING ...",
                                        color = NeonPink,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    LinearProgressIndicator(
                                        color = NeonPink,
                                        trackColor = CyberMutedSurface,
                                        modifier = Modifier.fillMaxWidth().height(4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "01001100 01101111 01100001 01100100 01101001 01101110 01100111 00100000 01001110 01100101 01110101 01110010 01101111 ...",
                                        color = MutedText.copy(alpha = 0.5f),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 2,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                    }

                    response?.let { res ->
                        item {
                            // Sentient AI Copilot commentary response box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xE6050510))
                                    .border(1.2.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Badge(containerColor = NeonGreen.copy(alpha = 0.2f)) {
                                            Text(
                                                text = "NEURO-COPILOT SUCCESS",
                                                color = NeonGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(text = "VISUAL: ${res.recommendedVisualizerStyle.uppercase()}", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = res.commentary,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DividerColor)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "CURATED SYNAPSE MATRIX: ${res.aiPlaylistTitle.uppercase()}",
                                        color = MutedText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )

                                    // Recommended matching tunes in list
                                    val matchedSongs = availableSongs.filter { res.recommendedSongIds.contains(it.id) }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        matchedSongs.forEach { song ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(CyberMutedSurface)
                                                    .padding(8.dp)
                                            ) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(song.imageUrl),
                                                    contentDescription = "Cover Image",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = song.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                    Text(text = song.artist, color = MutedText, fontSize = 10.sp, maxLines = 1)
                                                }
                                                Icon(Icons.Default.GraphicEq, contentDescription = "Active Match", tint = CyberCyan, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Massive Launch Load Playlist Trigger
                                    Button(
                                        onClick = {
                                            onLoadAIPlaylist(res.aiPlaylistTitle, matchedSongs, res.recommendedVisualizerStyle)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                    ) {
                                        Text(text = "MOUNT AI PLAYLIST IN PLAYER", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large glassy input textbox at bottom of page
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                placeholder = { Text(text = "Connect prompt vibe codes...", color = MutedText.copy(alpha = 0.5f), fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CyberDarkSurface,
                    unfocusedContainerColor = CyberDarkSurface
                ),
                maxLines = 2,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp)),
                trailingIcon = {
                    if (prompt.isNotBlank() && !isThinking) {
                        IconButton(onClick = onSubmitPrompt) {
                            Icon(Icons.Default.Send, contentDescription = "Submit link", tint = CyberCyan)
                        }
                    }
                }
            )
        }
    }
}
