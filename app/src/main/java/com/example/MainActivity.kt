package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PulseWaveViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContainer()
            }
        }
    }
}

@Composable
fun MainContainer() {
    val context = LocalContext.current
    val viewModel: PulseWaveViewModel = viewModel()

    // ViewModel States
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.playbackProgress.collectAsState()
    val currentPosMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isShuffle by viewModel.isShuffleEnabled.collectAsState()
    val isRepeat by viewModel.isRepeatEnabled.collectAsState()
    val volume by viewModel.volume.collectAsState()

    val songs by viewModel.filteredSongs.collectAsState(initial = viewModel.songs)
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val likedIds by viewModel.likedSongIds.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allUserAlbums by viewModel.allUserAlbums.collectAsState()
    val activeTemplate by viewModel.activeTemplate.collectAsState()
    val visualizerTemplates by viewModel.visualizerTemplates.collectAsState()
    val eqBands by viewModel.eqBands.collectAsState()
    val bassBoost by viewModel.bassBoost.collectAsState()
    val virtual3D by viewModel.virtual3D.collectAsState()
    val presetName by viewModel.selectedPresetName.collectAsState()

    val copilotPrompt by viewModel.copilotPrompt.collectAsState()
    val copilotResponse by viewModel.copilotResponse.collectAsState()
    val isCopilotThinking by viewModel.isCopilotThinking.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val fineTuneTempo by viewModel.fineTuneTempo.collectAsState()
    val fineTuneGenre by viewModel.fineTuneGenre.collectAsState()
    val isAdventurous by viewModel.isAdventurous.collectAsState()
    val likedSongIdsList by viewModel.likedSongIds.collectAsState()
    val recentlyPlayedList by viewModel.recentlyPlayed.collectAsState()
    val allSongsList by viewModel.songsFlow.collectAsState()

    val isExporting by viewModel.isExportingVideo.collectAsState()
    val exportProgress by viewModel.exportVideoProgress.collectAsState()
    val exportLogs by viewModel.exportLogs.collectAsState()
    val exportedVideoUri by viewModel.exportedVideoUri.collectAsState()

    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val downloadedIds by viewModel.downloadedSongIds.collectAsState()

    val ytSearchQuery by viewModel.ytSearchQuery.collectAsState()
    val ytSearchResults by viewModel.ytSearchResults.collectAsState()
    val isSearchingYt by viewModel.isSearchingYt.collectAsState()
    val communityTemplates by viewModel.communityTemplates.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMood by viewModel.selectedMoodFilter.collectAsState()
    val spectrumData by viewModel.spectrumData.collectAsState()
    val activeShaderFilter by viewModel.activeShaderFilter.collectAsState()

    val githubOwner by viewModel.githubOwner.collectAsState()
    val githubRepo by viewModel.githubRepo.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()

    // Nav and view layouts states
    var currentTab by remember { mutableStateOf("Home") } // Home, Visualizer, Equalizer, AI Copilot
    var isFullPlayerExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (!isFullPlayerExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // notch & gesture safe areas
                ) {
                    // Constant floating glassmorphic Mini Player pill right above bottom bar
                    MiniPlayer(
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        progress = progress,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSkipNext = { viewModel.skipNext() },
                        onSkipPrevious = { viewModel.skipPrevious() },
                        onOpenFullPlayer = { isFullPlayerExpanded = true }
                    )

                    // Cyberpunk M3 bottom navigation bar
                    NavigationBar(
                        containerColor = Color(0xFF0C0C16),
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Home tab
                        NavigationBarItem(
                            selected = currentTab == "Home",
                            onClick = { currentTab = "Home" },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF00F0FF),
                                selectedTextColor = Color(0xFF00F0FF),
                                unselectedIconColor = Color(0xFF8A8A9E),
                                unselectedTextColor = Color(0xFF8A8A9E),
                                indicatorColor = Color(0x2200F0FF)
                            )
                        )

                        // Avee Studio template designer tab
                        NavigationBarItem(
                            selected = currentTab == "Visualizer",
                            onClick = { currentTab = "Visualizer" },
                            icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Avee Visualizer") },
                            label = { Text("AVEE STUDIO", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF007F),
                                selectedTextColor = Color(0xFFFF007F),
                                unselectedIconColor = Color(0xFF8A8A9E),
                                unselectedTextColor = Color(0xFF8A8A9E),
                                indicatorColor = Color(0x22FF007F)
                            )
                        )

                        // Neural equalizer tab
                        NavigationBarItem(
                            selected = currentTab == "Equalizer",
                            onClick = { currentTab = "Equalizer" },
                            icon = { Icon(Icons.Default.Tune, contentDescription = "Equalizer") },
                            label = { Text("NEURAL EQ", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF00FF66),
                                selectedTextColor = Color(0xFF00FF66),
                                unselectedIconColor = Color(0xFF8A8A9E),
                                unselectedTextColor = Color(0xFF8A8A9E),
                                indicatorColor = Color(0x2200FF66)
                            )
                        )

                        // AI neural copilot advisor tab
                        NavigationBarItem(
                            selected = currentTab == "Copilot",
                            onClick = { currentTab = "Copilot" },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Neural Copilot") },
                            label = { Text("COPILOT AI", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF00F0FF),
                                selectedTextColor = Color(0xFF00F0FF),
                                unselectedIconColor = Color(0xFF8A8A9E),
                                unselectedTextColor = Color(0xFF8A8A9E),
                                indicatorColor = Color(0x2200F0FF)
                            )
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Absolute near-black base #050505
                    drawRect(color = Color(0xFF050505))
                    // Cyan top-left soft atmospheric glow (cyan-500/10 equivalent)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width * 0.1f, size.height * 0.1f),
                            radius = size.minDimension * 0.9f
                        )
                    )
                    // Purple bottom-right soft atmospheric glow (purple-600/10 equivalent)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF9333EA).copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width * 0.9f, size.height * 0.9f),
                            radius = size.minDimension * 0.9f
                        )
                    )
                }
                .padding(bottom = if (isFullPlayerExpanded) 0.dp else innerPadding.calculateBottomPadding())
        ) {
            // Main views with crossfading transitions for seamless UX
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "TabViewContent"
            ) { tab ->
                when (tab) {
                    "Home" -> {
                        MainDashboard(
                            songs = songs,
                            recentlyPlayed = recentlyPlayed,
                            likedIds = likedIds,
                            playlists = playlists,
                            searchQuery = searchQuery,
                            selectedMood = selectedMood,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onMoodFilter = { viewModel.setMoodFilter(it) },
                            onSelectSong = { viewModel.playSong(it) },
                            onToggleLike = { viewModel.toggleLikeSong(it) },
                            onCreatePlaylist = { name, desc ->
                                viewModel.createPlaylist(name, desc)
                                Toast.makeText(context, "Playlist $name Mounted Successfully!", Toast.LENGTH_SHORT).show()
                            },
                            onDeletePlaylist = { viewModel.deletePlaylist(it) },
                            onAddSongToPlaylist = { plId, sId ->
                                viewModel.addSongToPlaylist(plId, sId)
                                Toast.makeText(context, "Track added to playlist!", Toast.LENGTH_SHORT).show()
                            },
                            ytSearchQuery = ytSearchQuery,
                            ytSearchResults = ytSearchResults,
                            isSearchingYt = isSearchingYt,
                            downloadProgress = downloadProgress,
                            downloadedIds = downloadedIds,
                            onYtSearchSubmit = { query -> viewModel.searchYouTube(query) },
                            onDownloadYtSong = { song ->
                                viewModel.downloadYtSong(song)
                                Toast.makeText(context, "Initiating Offline Cyber Cache...", Toast.LENGTH_SHORT).show()
                            },
                            currentUser = currentUser,
                            allUserAlbums = allUserAlbums,
                            onSignup = { u, p, d, c, b, cb -> viewModel.signupUser(u, p, d, c, b, cb) },
                            onLogin = { u, p, cb -> viewModel.loginUser(u, p, cb) },
                            onLogout = { viewModel.logout() },
                            onCreateAlbum = { t, d, g, i, cb -> viewModel.createAndPublishAlbum(t, d, g, i, cb) },
                            onPublishTrack = { album, title, genre, mood, streamUrl, imageUrl, bpm, lyrics, cb ->
                                viewModel.publishTrackToAlbum(album, title, genre, mood, streamUrl, imageUrl, bpm, lyrics, cb)
                            },
                            onImportLocalSong = { title, artist, album, uri ->
                                viewModel.importLocalSong(title, artist, album, uri)
                            },
                            githubOwner = githubOwner,
                            githubRepo = githubRepo,
                            updateStatus = updateStatus,
                            onSetGitHubConfig = { owner, repo -> viewModel.setGitHubConfig(owner, repo) },
                            onCheckForUpdates = { viewModel.checkForUpdates() },
                            onTriggerInstallUpdate = { url ->
                                viewModel.triggerInstallUpdate(url) {
                                    Toast.makeText(context, "Successfully downloaded new OTA binary via PulseWave Scanner!", Toast.LENGTH_LONG).show()
                                }
                            },
                            onResetUpdateStatus = { viewModel.resetUpdateStatus() }
                        )
                    }

                    "Visualizer" -> {
                        VisualizerEditor(
                            activeTemplate = activeTemplate,
                            templates = visualizerTemplates,
                            spectrum = spectrumData,
                            isPlaying = isPlaying,
                            currentSong = currentSong,
                            shaderFilter = activeShaderFilter,
                            isExporting = isExporting,
                            exportProgress = exportProgress,
                            exportLogs = exportLogs,
                            exportedVideoUri = exportedVideoUri,
                            onCustomize = { name, p, s, g, l, pc, sc, sz, gl, r, t, bt ->
                                viewModel.customizeActiveTemplate(name, p, s, g, l, pc, sc, sz, gl, r, t, bt)
                            },
                            onSelectTemplate = { viewModel.selectTemplate(it) },
                            onSaveTemplate = { name ->
                                viewModel.saveCustomTemplate(name)
                                Toast.makeText(context, "Custom template saved in Database!", Toast.LENGTH_SHORT).show()
                            },
                            onDeleteTemplate = { viewModel.deleteCustomTemplate(it) },
                            onSetShaderFilter = { viewModel.setShaderFilter(it) },
                            onStartExport = { viewModel.startExportingVideo() },
                            onCloseExport = { viewModel.closeExportDialog() },
                            communityTemplates = communityTemplates,
                            onPublishToCommunity = { template ->
                                viewModel.publishTemplateToCommunity(template)
                                Toast.makeText(context, "Custom Visualizer Matrix Published Online!", Toast.LENGTH_LONG).show()
                            },
                            onDownloadCommunityTemplate = { template ->
                                viewModel.downloadCommunityTemplate(template)
                                Toast.makeText(context, "Imported Community Template to Local Storage!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    "Equalizer" -> {
                        EqualizerScreen(
                            eqBands = eqBands,
                            bassBoost = bassBoost,
                            virtual3D = virtual3D,
                            presetName = presetName,
                            onAdjustBand = { band, db -> viewModel.adjustEQBand(band, db) },
                            onAdjustBass = { viewModel.adjustBassBoost(it) },
                            onAdjustVirtual = { viewModel.adjustVirtual3D(it) },
                            onSelectPreset = { viewModel.selectEQPreset(it) }
                        )
                    }

                    "Copilot" -> {
                        val likedPlaylistSongs = remember(allSongsList, likedSongIdsList) {
                            allSongsList.filter { likedSongIdsList.contains(it.id) }
                        }
                        AICopilotScreen(
                            prompt = copilotPrompt,
                            response = copilotResponse,
                            isThinking = isCopilotThinking,
                            availableSongs = allSongsList,
                            likedSongs = likedPlaylistSongs,
                            recentlyPlayed = recentlyPlayedList,
                            selectedTheme = selectedTheme,
                            fineTuneTempo = fineTuneTempo,
                            fineTuneGenre = fineTuneGenre,
                            isAdventurous = isAdventurous,
                            onPromptChange = { viewModel.setCopilotPrompt(it) },
                            onSubmitPrompt = { viewModel.askCopilot() },
                            onSelectTheme = { viewModel.setSelectedTheme(it) },
                            onTempoChange = { viewModel.setFineTuneTempo(it) },
                            onGenreChange = { viewModel.setFineTuneGenre(it) },
                            onAdventurousChange = { viewModel.setAdventurous(it) },
                            onLoadAIPlaylist = { title, recommendedSongs, style ->
                                // Construct interactive quick playlist
                                viewModel.createPlaylist(title, "Neuro-copilot AI synthesized wave models")
                                Toast.makeText(context, "Synthesizing dynamic playlist to active player...", Toast.LENGTH_LONG).show()
                                
                                // Auto-trigger the first song instantly
                                recommendedSongs.firstOrNull()?.let {
                                    viewModel.playSong(it)
                                    isFullPlayerExpanded = true // open detailed player instantly
                                }
                            }
                        )
                    }
                }
            }

            // Expanding Full Screen player slide up overlay panel!
            AnimatedVisibility(
                visible = isFullPlayerExpanded,
                enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)),
                exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400))
            ) {
                PlayerDetailScreen(
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    progress = progress,
                    currentPosMs = currentPosMs,
                    durationMs = durationMs,
                    isMuted = isMuted,
                    isShuffle = isShuffle,
                    isRepeat = isRepeat,
                    volume = volume,
                    likedIds = likedIds,
                    downloadedIds = downloadedIds,
                    downloadProgress = downloadProgress,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onNext = { viewModel.skipNext() },
                    onPrev = { viewModel.skipPrevious() },
                    onSeek = { viewModel.seekToPosition(it) },
                    onToggleMute = { viewModel.toggleMute() },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleRepeat = { viewModel.toggleRepeat() },
                    onVolumeSet = { viewModel.setVolumeLevel(it) },
                    onToggleLike = { viewModel.toggleLikeSong(it) },
                    onDownload = { viewModel.downloadSong(it) },
                    onBack = { isFullPlayerExpanded = false },
                    onSaveLyrics = { id, rawLrcStr ->
                        viewModel.saveSyncedLyrics(id, rawLrcStr)
                    }
                )
            }
        }
    }
}
