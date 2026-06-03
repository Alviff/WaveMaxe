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
import androidx.compose.material.icons.filled.*
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

data class ThemePreset(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val tintColor: Color
)

@Composable
fun AICopilotScreen(
    prompt: String,
    response: AIDeclaredPlaylistResponse?,
    isThinking: Boolean,
    availableSongs: List<Song>,
    likedSongs: List<Song>,
    recentlyPlayed: List<Song>,
    selectedTheme: String?,
    fineTuneTempo: String,
    fineTuneGenre: String,
    isAdventurous: Boolean,
    onPromptChange: (String) -> Unit,
    onSubmitPrompt: () -> Unit,
    onSelectTheme: (String?) -> Unit,
    onTempoChange: (String) -> Unit,
    onGenreChange: (String) -> Unit,
    onAdventurousChange: (Boolean) -> Unit,
    onLoadAIPlaylist: (String, List<Song>, String) -> Unit
) {
    var showTuningOptions by remember { mutableStateOf(false) }

    val themePresets = listOf(
        ThemePreset("workout", "CYBER-PUMP", "High BPM power outrun", "🏋️‍♂️", NeonPink),
        ThemePreset("chill", "SPACE DRIFT", "Serene nebula lofi", "🍵", CyberCyan),
        ThemePreset("focus", "MATRIX FLOW", "Steady ambient concentration", "🧠", NeonGreen)
    )

    // Calculate taste signature stats
    val likedCount = likedSongs.size
    val recentCount = recentlyPlayed.size
    val promptGenrePreference = if (likedSongs.isNotEmpty()) {
        likedSongs.groupingBy { it.genre }.eachCount().maxByOrNull { it.value }?.key
    } else if (recentlyPlayed.isNotEmpty()) {
        recentlyPlayed.groupingBy { it.genre }.eachCount().maxByOrNull { it.value }?.key
    } else {
        null
    }
    val signatureSummary = promptGenrePreference?.uppercase() ?: "NEUTRAL MATRIX"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = CyberCyan,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "NEURO-COPILOT CHAT",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Quantum AI Soundtrack Synthesizer",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // HUD Dashboard: Taste Signature Analysis Area
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDarkSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "NEURAL TASTE CORE ACTIVE",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Taste Profile", color = MutedText, fontSize = 10.sp)
                        Text(
                            text = signatureSummary,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = "Favorites", tint = NeonPink, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "$likedCount Liked", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = "History", tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "$recentCount Audits", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Section: "Quick-start Chill Vibes Generator" based on low-tempo taste profiles
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CyberCyan.copy(alpha = 0.15f),
                                NeonPink.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(
                        1.2.dp,
                        Brush.horizontalGradient(
                            colors = listOf(CyberCyan.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CloudQueue,
                                contentDescription = "Chill Impulse",
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTOMATED CHILL VECTOR ACTIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Badge(containerColor = CyberCyan.copy(alpha = 0.2f)) {
                            Text(
                                text = "LOW-TEMPO ALIGNED",
                                color = CyberCyan,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "PulseWave neural cores identified ambient, tranquil frequencies (~85-100 BPM) in your listening database. Tap to instantly curate a custom 'Chill Vibes' matrix.",
                        color = MutedText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onSelectTheme("chill")
                            onTempoChange("Slow")
                            onPromptChange("Generate a personalized 'Chill Vibes' playlist based on recent low-tempo listening history and mood waves.")
                            onSubmitPrompt()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Generate",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GENERATE 'CHILL VIBES'",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Section: Themed Mudulators (Workout, Chill, Focus)
        item {
            Column {
                Text(
                    text = "SYSTEM THEME PRESETS",
                    color = MutedText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    themePresets.forEach { theme ->
                        val isSelected = selectedTheme == theme.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) theme.tintColor.copy(alpha = 0.15f) else CyberDarkSurface)
                                .border(
                                    1.2.dp,
                                    if (isSelected) theme.tintColor else GlassBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (isSelected) {
                                        onSelectTheme(null) // deselect
                                    } else {
                                        onSelectTheme(theme.id) // select
                                    }
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = theme.icon, fontSize = 18.sp, modifier = Modifier.padding(bottom = 4.dp))
                                Text(
                                    text = theme.name,
                                    color = if (isSelected) theme.tintColor else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = theme.description,
                                    color = MutedText,
                                    fontSize = 8.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Matrix Collapsible Advanced Tuner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberDarkSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
            ) {
                // Toggle row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTuningOptions = !showTuningOptions }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = "Params Tuner",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MATRIX FINE-TUNE INPUTS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        if (showTuningOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Parameters Panel",
                        tint = MutedText
                    )
                }

                AnimatedVisibility(visible = showTuningOptions) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Divider(color = GlassBorder)

                        // 1. Tempo Filter BPM
                        Column {
                            Text(
                                text = "TEMPO VELOCITY LIMITS: ${fineTuneTempo.uppercase()}",
                                color = MutedText,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("All", "Slow", "Medium", "Fast").forEach { tempoOption ->
                                    val isOptionSelected = fineTuneTempo == tempoOption
                                    val optColor = when (tempoOption) {
                                        "Slow" -> CyberCyan
                                        "Medium" -> NeonGreen
                                        "Fast" -> NeonPink
                                        else -> Color.White
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isOptionSelected) optColor.copy(alpha = 0.2f) else CyberMutedSurface)
                                            .border(
                                                1.dp,
                                                if (isOptionSelected) optColor else GlassBorder,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onTempoChange(tempoOption) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tempoOption,
                                            color = if (isOptionSelected) optColor else Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Genre Matrix Selection
                        Column {
                            Text(
                                text = "TARGET GENRE CONSTRAINTS: ${fineTuneGenre.uppercase()}",
                                color = MutedText,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(listOf("All", "Synthwave", "Vaporwave", "Lofi", "Dark Synth", "Outrun")) { genreOption ->
                                    val isOptionSelected = fineTuneGenre == genreOption
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isOptionSelected) CyberCyan.copy(alpha = 0.2f) else CyberMutedSurface)
                                            .border(
                                                1.dp,
                                                if (isOptionSelected) CyberCyan else GlassBorder,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { onGenreChange(genreOption) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = genreOption,
                                            color = if (isOptionSelected) CyberCyan else Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // 3. Adventurous / Entropy Shift Option
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberMutedSurface)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.OnlinePrediction,
                                        contentDescription = "Entropy Shift",
                                        tint = NeonPink,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Rogue AI Entropy Shift",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Allow unexpected genre crossovers / temperature shifts (+)",
                                    color = MutedText,
                                    fontSize = 8.sp,
                                    lineHeight = 11.sp
                                )
                            }
                            Switch(
                                checked = isAdventurous,
                                onCheckedChange = onAdventurousChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonPink,
                                    checkedTrackColor = NeonPink.copy(alpha = 0.35f),
                                    uncheckedThumbColor = MutedText,
                                    uncheckedTrackColor = CyberDarkSurface
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Prompt Box
        item {
            Column {
                Text(
                    text = "SPECIFY ADD-ON PROMPT CODES (OPTIONAL)",
                    color = MutedText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = onPromptChange,
                        placeholder = {
                            Text(
                                text = "Enter any vibe parameters manually...",
                                color = MutedText.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            )
                        },
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
                            .clip(RoundedCornerShape(14.dp))
                    )
                }
            }
        }

        // Action Trigger Button
        item {
            Button(
                onClick = onSubmitPrompt,
                enabled = !isThinking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTheme == "workout") NeonPink else if (selectedTheme == "chill") CyberCyan else NeonGreen,
                    disabledContainerColor = CyberDarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Synthesize",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isThinking) "SYNTHESIZING MATRIX..." else "SYNTHESIZE AI CURATED MATRIX",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Results Section (Thinking OR Loaded Response)
        if (isThinking) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF030307))
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "NEURO-COPILOT TUNING ARRAY...",
                            color = NeonPink,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        LinearProgressIndicator(
                            color = NeonPink,
                            trackColor = CyberMutedSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "01001100 01101111 01100001 01100100 01101001 01101110 01100111 00100000 01001110 01100101 01110101 01110010 01101111 00100000 01010100 01100001 01110011 01110100 ...",
                            color = MutedText.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        } else if (response != null) {
            item {
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
                                    text = "NEURAL-COPILOT STABLE SUCCESS",
                                    color = NeonGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "VISUAL: ${response.recommendedVisualizerStyle.uppercase()}",
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = response.commentary,
                            color = Color.White,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = DividerColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "CURATED AUDIO CORES: ${response.aiPlaylistTitle.uppercase()}",
                            color = MutedText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        // Match recomended track IDs with our fully dynamic available list
                        val matchedSongs = availableSongs.filter { response.recommendedSongIds.contains(it.id) }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (matchedSongs.isEmpty()) {
                                // Fallback
                                val firstSongOpt = availableSongs.firstOrNull()
                                if (firstSongOpt != null) {
                                    SongRowItem(song = firstSongOpt)
                                }
                            } else {
                                matchedSongs.forEach { song ->
                                    SongRowItem(song = song)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Trigger loading
                        Button(
                            onClick = {
                                val songsToLoad = matchedSongs.ifEmpty { listOfNotNull(availableSongs.firstOrNull()) }
                                onLoadAIPlaylist(response.aiPlaylistTitle, songsToLoad, response.recommendedVisualizerStyle)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = "MOUNT AI PLAYLIST IN ACTIVE CORES",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Un-filled state
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberDarkSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.NetworkPing,
                            contentDescription = "Ready",
                            tint = CyberCyan.copy(alpha = 0.35f),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "AWAITING SYNAPSE TRIGGERS...\nSELECT A THEME PRESET OR ENRICH FINE-TUNING VALUES ABOVE.",
                            color = MutedText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SongRowItem(song: Song) {
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
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "${song.artist} • ${song.genre} (${song.bpm} BPM)",
                color = MutedText,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
        Icon(
            Icons.Default.GraphicEq,
            contentDescription = "Active Match",
            tint = CyberCyan,
            modifier = Modifier.size(16.dp)
        )
    }
}
