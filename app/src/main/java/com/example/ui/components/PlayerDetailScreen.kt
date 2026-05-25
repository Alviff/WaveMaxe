package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.LyricLine
import com.example.data.model.Song
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PlayerDetailScreen(
    currentSong: Song,
    isPlaying: Boolean,
    progress: Float,
    currentPosMs: Long,
    durationMs: Long,
    isMuted: Boolean,
    isShuffle: Boolean,
    isRepeat: Boolean,
    volume: Float,
    likedIds: List<String>,
    downloadedIds: Set<String>,
    downloadProgress: Map<String, Float>,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onVolumeSet: (Float) -> Unit,
    onToggleLike: (String) -> Unit,
    onDownload: (String) -> Unit,
    onBack: () -> Unit,
    onSaveLyrics: (String, String) -> Unit = { _, _ -> }
) {
    var activeTab by remember { mutableStateOf("Cover") } // Cover, Lyrics
    val isLiked = likedIds.contains(currentSong.id)
    val isDownloaded = downloadedIds.contains(currentSong.id)
    val curDlProgress = downloadProgress[currentSong.id]

    // vinyl rotation
    val infiniteTransition = rememberInfiniteTransition(label = "VinylDetails")
    val vinylRotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "VinylAngle"
    )

    // Formatted positions
    val elapsedString = formatTime(currentPosMs)
    val durationString = formatTime(durationMs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Absolute near-black base
                drawRect(color = Color(0xFF050505))
                // Cyan top-left soft atmospheric glow (cyan-500/10)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 0.1f),
                        radius = size.minDimension * 0.9f
                    )
                )
                // Purple bottom-right soft atmospheric glow (purple-600/10)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF9333EA).copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.9f),
                        radius = size.minDimension * 0.9f
                    )
                )
            }
            .padding(horizontal = 20.dp)
    ) {
        // Upper Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minimize", tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Text(
                text = "NOW PLAYING",
                color = MutedText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp
            )
            IconButton(onClick = { onToggleLike(currentSong.id) }) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) NeonPink else Color.White
                )
            }
        }

        // Segmented navigation tab: COVER / LYRICS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(bottom = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberDarkSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Cover", "Lyrics").forEach { tab ->
                val active = tab == activeTab
                Text(
                    text = tab.uppercase(),
                    color = if (active) CyberCyan else MutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { activeTab = tab }
                        .background(if (active) CyberCyan.copy(alpha = 0.08f) else Color.Transparent)
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Active tab container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (activeTab == "Cover") {
                // Cyber Vinyl Rotate Plate mockup
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer structural neon design concentric rings from HTML
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .border(1.dp, Color(0xFF22D3EE).copy(alpha = 0.15f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(250.dp)
                            .border(1.dp, Color(0xFF22D3EE).copy(alpha = 0.35f), CircleShape)
                    )

                    // Album Art / Center with linear/radial gradient sweep
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF22D3EE), Color(0xFFD946EF))
                                )
                            )
                            .padding(2.dp) // border of 1 equivalent
                            .rotate(if (isPlaying) vinylRotationAngle else 0f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(4.dp, Color(0xFF0F172A), CircleShape) // w-full h-full rounded-full overflow-hidden border-4 border-black bg-slate-800
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Dynamic radial gradient inner atmosphere
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .drawBehind {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(Color(0xFF334155), Color(0xFF0F172A)),
                                                center = center,
                                                radius = size.width / 2
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(currentSong.imageUrl),
                                    contentDescription = "Album photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize(0.68f)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                )
                                
                                // Beautiful Center spindle hole
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .border(1.2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                                )
                            }
                        }
                    }
                }
            } else {
                // Karaoke teleprompter synced lyrics view
                SyncedLyricsView(
                    lyrics = currentSong.lyrics,
                    currentPosMs = currentPosMs,
                    songId = currentSong.id,
                    songTitle = currentSong.title,
                    songArtist = currentSong.artist,
                    onSaveLyrics = onSaveLyrics
                )
            }
        }

        // Song Title, Info, and Download Trigger line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentSong.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentSong.artist,
                    color = MutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Downloader Widget Card
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CyberDarkSurface)
                    .clickable { onDownload(currentSong.id) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isDownloaded) {
                    Icon(Icons.Default.OfflinePin, contentDescription = "Downloaded", tint = NeonGreen, modifier = Modifier.size(22.dp))
                } else if (curDlProgress != null) {
                    CircularProgressIndicator(
                        progress = { curDlProgress },
                        color = CyberCyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(Icons.Default.FileDownload, contentDescription = "Download", tint = MutedText, modifier = Modifier.size(22.dp))
                }
            }
        }

        // Timeline Slider Layout
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = progress,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                    activeTrackColor = CyberCyan,
                    inactiveTrackColor = CyberMutedSurface,
                    thumbColor = NeonPink
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = elapsedString, color = MutedText, fontSize = 11.sp)
                Text(text = durationString, color = MutedText, fontSize = 11.sp)
            }
        }

        // Active control rows: Play, Pause, Next, Prev, Shuffle, Repeat Loop
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleShuffle) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffle) CyberCyan else MutedText,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onPrev) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Central core glowing neon play pill
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonPink, NeonPink.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
                    .clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CyberBlack)
                        .border(1.5.dp, NeonPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "PlayPause",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(onClick = onToggleRepeat) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (isRepeat) NeonPink else MutedText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Master Volume controller pill at bottom of detailed view
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberDarkSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isMuted || volume == 0f) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Volume",
                tint = if (isMuted) NeonPink else CyberCyan,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onToggleMute() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = if (isMuted) 0f else volume,
                onValueChange = onVolumeSet,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    activeTrackColor = CyberCyan,
                    inactiveTrackColor = CyberMutedSurface,
                    thumbColor = CyberCyan
                )
            )
        }
    }
}

@Composable
private fun SyncedLyricsView(
    lyrics: List<LyricLine>,
    currentPosMs: Long,
    songId: String,
    songTitle: String,
    songArtist: String,
    onSaveLyrics: (String, String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var lyricsInputText by remember { mutableStateOf("") }
    var isFetchingOnline by remember { mutableStateOf(false) }
    val corcope = rememberCoroutineScope()

    // Initialize text with existing lyrics in LRC format if available
    LaunchedEffect(showEditDialog) {
        if (showEditDialog) {
            if (lyrics.isEmpty()) {
                lyricsInputText = """
                [00:02.00] Entering active $songTitle frequency matrix...
                [00:08.50] This is the core rhythm of $songTitle by $songArtist
                [00:15.00] Listening to offline cache system layers
                [00:22.00] Styled inside the cosmic glowing player deck
                """.trimIndent()
            } else {
                lyricsInputText = lyrics.joinToString("\n") { line ->
                    val min = (line.timestampMs / 60000)
                    val sec = (line.timestampMs % 60000) / 1000
                    val ms = (line.timestampMs % 1000) / 10
                    String.format("[%02d:%02d.%02d] %s", min, sec, ms, line.text)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (lyrics.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "No lyrics",
                        tint = MutedText.copy(alpha = 0.4f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "INSTRUMENTAL SECTOR DETECTED\nNO VOCAL CORDS BOUNDS",
                        color = MutedText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    
                    if (isFetchingOnline) {
                        CircularProgressIndicator(color = NeonPink, modifier = Modifier.size(24.dp))
                        Text(text = "SCANNING CYBER LYRIC DATABASE...", color = NeonPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Button(
                                onClick = {
                                    corcope.launch {
                                        isFetchingOnline = true
                                        // Simulate background crawler delay
                                        kotlinx.coroutines.delay(1200)
                                        val compiledOnlineText = """
                                        [00:02.00] // Initializing online audio parsing channels...
                                        [00:05.50] // Mounting lyric core matrix for $songTitle
                                        [00:10.00] Entering active cyber sector frequency v2.1
                                        [00:16.00] This is the code of $songTitle by $songArtist
                                        [00:22.50] Speeding through neon lines inside our cyberspace
                                        [00:28.00] Listening together in the PulseWave sonic fireplace
                                        [00:35.00] [ Procedural instrumental synth solo active ]
                                        [00:44.00] Resynthesizing digital frequencies of our soul
                                        [00:52.50] Moving with the active Avee visualizer control
                                        [01:05.00] // End of live online stream database bounds
                                        """.trimIndent()
                                        onSaveLyrics(songId, compiledOnlineText)
                                        isFetchingOnline = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("ONLINE AI SYNC", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = { showEditDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("OFFLINE SYNC LAB", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        } else {
            // Determine currently active lyrics mark
            val activeIndex = remember(lyrics, currentPosMs) {
                val matchIdx = lyrics.indexOfLast { currentPosMs >= it.timestampMs }
                if (matchIdx != -1) matchIdx else 0
            }

            val listState = rememberLazyListState()

            // Smooth autoscrolling list matches tracking index!
            LaunchedEffect(activeIndex) {
                if (lyrics.isNotEmpty()) {
                    corcope.launch {
                        // Center active line scroll position
                        val targetScrollIdx = (activeIndex - 2).coerceAtLeast(0)
                        listState.animateScrollToItem(targetScrollIdx)
                    }
                }
            }

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = index == activeIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isActive) 1.15f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
                        label = "LyricScale"
                    )

                    Text(
                        text = line.text,
                        color = if (isActive) CyberCyan else MutedText.copy(alpha = 0.65f),
                        fontSize = if (isActive) 18.sp else 15.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .background(if (isActive) GlassTextBacking else Color.Transparent)
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    )
                }
            }

            // Small, float edit trigger on bottom panel
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberDarkSurface.copy(alpha = 0.85f))
                    .border(0.5.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .clickable { showEditDialog = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = NeonPink, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "LAUNCH OFFLINE SYNC LAB", color = NeonPink, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }

        // Sleek Multiline LRC Lyric Offset Dialog
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text(text = "LYRICS CORES OFFSET ENGINE", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 14.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Format: [mm:ss.SS] Line text. Standard timestamps will autoscscroll the karaoke display chronologically.",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                        OutlinedTextField(
                            value = lyricsInputText,
                            onValueChange = { lyricsInputText = it },
                            placeholder = { Text("Enter LRC formatted script line-by-line...", color = MutedText.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = CyberBlack,
                                unfocusedContainerColor = CyberBlack
                            ),
                            singleLine = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onSaveLyrics(songId, lyricsInputText)
                            showEditDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("MOUNT SECURE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("ABORT", color = NeonPink)
                    }
                },
                containerColor = CyberDarkSurface,
                modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val sec = (ms / 1000) % 60
    val min = (ms / 1000) / 60
    return String.format("%02d:%02d", min, sec)
}
