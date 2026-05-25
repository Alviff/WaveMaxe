package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.data.model.VisualizerTemplate
import com.example.ui.theme.*

@Composable
fun VisualizerEditor(
    activeTemplate: VisualizerTemplate,
    templates: List<VisualizerTemplate>,
    spectrum: FloatArray,
    isPlaying: Boolean,
    currentSong: Song,
    shaderFilter: String,
    isExporting: Boolean,
    exportProgress: Float,
    exportLogs: List<String>,
    exportedVideoUri: String?,
    onCustomize: (
        name: String?, primaryHex: String?, secondaryHex: String?, glowHex: String?,
        layout: String?, particles: Int?, speed: Float?, size: Float?,
        glow: Boolean?, reactive: Boolean?, text: String?, bgType: String?
    ) -> Unit,
    onSelectTemplate: (VisualizerTemplate) -> Unit,
    onSaveTemplate: (String) -> Unit,
    onDeleteTemplate: (Long) -> Unit,
    onSetShaderFilter: (String) -> Unit,
    onStartExport: () -> Unit,
    onCloseExport: () -> Unit,
    // Optional Marketplace Community Additions
    communityTemplates: List<VisualizerTemplate> = emptyList(),
    onPublishToCommunity: (VisualizerTemplate) -> Unit = {},
    onDownloadCommunityTemplate: (VisualizerTemplate) -> Unit = {}
) {
    var currentVisualizerTab by remember { mutableStateOf("Local") }
    var templateNameInput by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    val layouts = listOf("Circular Wave", "Spectrum Bars", "Neon Starburst", "Line Wave")
    val backgroundTypes = listOf("Dark Cyber", "Neon Purple", "Glow Blue")
    val shaderFilters = listOf("None", "Glitch Neon", "Vapor Blur", "VHS Grain")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp, top = 28.dp)
            ) {
                Icon(Icons.Default.MovieFilter, contentDescription = "Layers", tint = NeonPink, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AVEE STUDIO DESIGNER",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }

        // Real-Time Visualizer preview box embedded directly in editor!
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Brush.horizontalGradient(listOf(CyberCyan, NeonPink)), RoundedCornerShape(20.dp))
                    .background(CyberDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Avee canvas preview rendering active tweaks
                AveeVisualizer(
                    spectrum = spectrum,
                    template = activeTemplate,
                    isPlaying = isPlaying,
                    modifier = Modifier.fillMaxSize()
                )

                // Render watermark overlays on top
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Text(
                        text = activeTemplate.customText.uppercase(),
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    Badge(
                        containerColor = TranslucentBlack,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = if (isPlaying) "60FPS REACTIVE" else "STUDIO PAUSED",
                            color = if (isPlaying) NeonGreen else MutedText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (shaderFilter != "None") {
                        Badge(
                            containerColor = NeonPink.copy(alpha = 0.2f),
                            modifier = Modifier.align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = "SHADER: ${shaderFilter.uppercase()}",
                                color = NeonPink,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Primary Control panel: Style picker and Video Exporter triggers
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large glowing exporter button
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(NeonPink, Color(0xFFC0007A))))
                        .clickable { onStartExport() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VideoCall, contentDescription = "Export Video", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXPORT MP4 VIDEO",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Preset Save button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberMutedSurface)
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .clickable { showSaveDialog = true }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SAVE PRESET",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Tweak panel headers
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text(
                    text = "TEMPLATES MARKETPLACE",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberDarkSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentVisualizerTab == "Local") CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { currentVisualizerTab = "Local" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOCAL PRESETS",
                            color = if (currentVisualizerTab == "Local") CyberCyan else MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentVisualizerTab == "Community") NeonPink.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { currentVisualizerTab = "Community" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = if (currentVisualizerTab == "Community") NeonPink else MutedText,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COMMUNITY EXCHANGE",
                                color = if (currentVisualizerTab == "Community") NeonPink else MutedText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Marketplace Content Slots (Local or Online browser)
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                if (currentVisualizerTab == "Local") {
                    templates.forEach { t ->
                        val isSelected = t.id == activeTemplate.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha = 0.08f) else CyberDarkSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) CyberCyan else DividerColor,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectTemplate(t) }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color.parseColorSafe(t.primaryColor))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = t.name,
                                    color = if (isSelected) CyberCyan else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Layout: ${t.layoutStyle} • By ${t.creatorName}",
                                    color = MutedText,
                                    fontSize = 10.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // If they have created this custom template, let them publish it!
                                if (t.id > 4) {
                                    IconButton(
                                        onClick = { onPublishToCommunity(t) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CloudUpload,
                                            contentDescription = "Publish to Community Marketplace",
                                            tint = CyberCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onDeleteTemplate(t.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Custom Preset",
                                            tint = NeonPink.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Protected Core Preset",
                                        tint = MutedText.copy(alpha = 0.3f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Render online community catalog index!
                    if (communityTemplates.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberDarkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NO COMMUNITY EXCHANGES UPLOADS YET",
                                color = MutedText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        communityTemplates.forEach { ct ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CyberDarkSurface)
                                    .border(
                                        width = 1.dp,
                                        color = DividerColor,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(Color.parseColorSafe(ct.primaryColor))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ct.name,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "By ${ct.creatorName} • layout: ${ct.layoutStyle}",
                                        color = NeonPink,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Try template immediately button
                                    IconButton(
                                        onClick = { onSelectTemplate(ct) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Brush,
                                            contentDescription = "Instant Preview Preset",
                                            tint = CyberCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Instantly Download & Store into SQLite library Room
                                    IconButton(
                                        onClick = { onDownloadCommunityTemplate(ct) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CloudDownload,
                                            contentDescription = "Install Preset to Database",
                                            tint = NeonGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Shader effects controls
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(
                    text = "NEON SHADER CODES",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    shaderFilters.forEach { filter ->
                        val active = filter == shaderFilter
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (active) NeonPink.copy(alpha = 0.15f) else CyberDarkSurface)
                                .border(1.dp, if (active) NeonPink else GlassBorder, RoundedCornerShape(10.dp))
                                .clickable { onSetShaderFilter(filter) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter.replace(" ", "\n").uppercase(),
                                color = if (active) NeonPink else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Sliding parameters editors (Sliders list)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberDarkSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "LAYERS TWEAK ENGINES",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Circle style layout picker
                Column {
                    Text(text = "STRUCTURE GEOMETRY STYLE", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        layouts.forEach { layout ->
                            val active = layout == activeTemplate.layoutStyle
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) CyberCyan.copy(alpha = 0.15f) else CyberMutedSurface)
                                    .border(1.dp, if (active) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { onCustomize(null, null, null, null, layout, null, null, null, null, null, null, null) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = layout.replace(" ", "\n").uppercase(),
                                    color = if (active) CyberCyan else Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }

                // Background type pickers
                Column {
                    Text(text = "AMBIENT ATMOSPHERE BACKGROUND", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        backgroundTypes.forEach { bg ->
                            val active = bg == activeTemplate.backgroundType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) CyberCyan.copy(alpha = 0.15f) else CyberMutedSurface)
                                    .border(1.dp, if (active) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { onCustomize(null, null, null, null, null, null, null, null, null, null, null, bg) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bg.uppercase(),
                                    color = if (active) CyberCyan else Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Particles slider
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "PARTICLES POPULATION CELL", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${activeTemplate.particleCount} cells", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = activeTemplate.particleCount.toFloat(),
                        onValueChange = { onCustomize(null, null, null, null, null, it.toInt(), null, null, null, null, null, null) },
                        valueRange = 10f..200f,
                        colors = SliderDefaults.colors(activeTrackColor = CyberCyan, thumbColor = CyberCyan)
                    )
                }

                // Speed scale slider
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "REACTION VELOCITY DRAG", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = String.format("%.1fx", activeTemplate.speedScale), color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = activeTemplate.speedScale,
                        onValueChange = { onCustomize(null, null, null, null, null, null, it, null, null, null, null, null) },
                        valueRange = 0.2f..2.5f,
                        colors = SliderDefaults.colors(activeTrackColor = NeonPink, thumbColor = NeonPink)
                    )
                }

                // Watermark Text Editor
                Column {
                    Text(text = "WATERMARK TEXT LABEL OVERLAY", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = activeTemplate.customText,
                        onValueChange = { onCustomize(null, null, null, null, null, null, null, null, null, null, it, null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                // Dual Switches: Glow & Beat Reactive
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = activeTemplate.isGlowEnabled,
                            onCheckedChange = { onCustomize(null, null, null, null, null, null, null, null, it, null, null, null) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonGreen)
                        )
                        Text(text = "GLOW EFFECTS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = activeTemplate.isBeatReactive,
                            onCheckedChange = { onCustomize(null, null, null, null, null, null, null, null, null, it, null, null) },
                            colors = CheckboxDefaults.colors(checkedColor = CyberCyan)
                        )
                        Text(text = "BPM VECTOR BEATS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Modal dialog to Save customized template
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(text = "SAVE TEMPLATE VECTOR", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(text = "Tweak settings look awesome. Enter index name to lock database row.", color = MutedText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                    OutlinedTextField(
                        value = templateNameInput,
                        onValueChange = { templateNameInput = it },
                        placeholder = { Text(text = "Cool Avee Circular Preset", color = MutedText.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = GlassBorder),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (templateNameInput.isNotBlank()) {
                            onSaveTemplate(templateNameInput)
                            templateNameInput = ""
                            showSaveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text(text = "SAVE ROW", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text(text = "ABORT", color = NeonPink)
                }
            },
            containerColor = CyberDarkSurface,
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
        )
    }

    // Modal dialog for Video Exporting simulated log terminal overlay!
    if (isExporting) {
        AlertDialog(
            onDismissRequest = {}, // enforce active loading wait locks
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeveloperMode, contentDescription = "Terminal", tint = NeonPink, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VULKAN EXPORT MATRIX",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "RENDERING MP4 CONTAINER ...",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Linear progression indicator
                    LinearProgressIndicator(
                        progress = exportProgress,
                        color = NeonPink,
                        trackColor = CyberMutedSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "PERCENT: ${(exportProgress * 100).toInt()}% READY",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "TRANSLATING PIPELINE LOGS:",
                        color = MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    // Logs terminal area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF020205))
                            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        LazyColumn(
                            reverseLayout = true,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(exportLogs.asReversed()) { logLine ->
                                Text(
                                    text = "> $logLine",
                                    color = if (logLine.startsWith("SUCCESS")) NeonGreen else if (logLine.startsWith("ERR")) NeonPink else MutedText,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (exportProgress == 1.0f) {
                    Button(
                        onClick = onCloseExport,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                    ) {
                        Text(text = "SHARE & FINISH", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (exportProgress < 1.0f) {
                    TextButton(onClick = onCloseExport) {
                        Text(text = "TERMINATE PIPELINE", color = NeonPink)
                    }
                }
            },
            containerColor = CyberDarkSurface,
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
        )
    }
}

private fun Color.Companion.parseColorSafe(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color.Cyan
    }
}
