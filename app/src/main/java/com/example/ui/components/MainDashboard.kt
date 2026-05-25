package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
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
import com.example.data.model.PlaylistEntity
import com.example.data.model.Song
import com.example.ui.theme.*

@Composable
fun MainDashboard(
    songs: List<Song>,
    recentlyPlayed: List<Song>,
    likedIds: List<String>,
    playlists: List<PlaylistEntity>,
    searchQuery: String,
    selectedMood: String?,
    onSearchChange: (String) -> Unit,
    onMoodFilter: (String?) -> Unit,
    onSelectSong: (Song) -> Unit,
    onToggleLike: (String) -> Unit,
    onCreatePlaylist: (String, String) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onAddSongToPlaylist: (Long, String) -> Unit,
    // Optional streaming and offline download additions
    ytSearchQuery: String = "",
    ytSearchResults: List<Song> = emptyList(),
    isSearchingYt: Boolean = false,
    downloadProgress: Map<String, Float> = emptyMap(),
    downloadedIds: Set<String> = emptySet(),
    onYtSearchSubmit: (String) -> Unit = {},
    onDownloadYtSong: (Song) -> Unit = {},
    currentUser: com.example.data.model.UserEntity? = null,
    allUserAlbums: List<com.example.data.model.UserAlbumEntity> = emptyList(),
    onSignup: (username: String, passcode: String, name: String, color: String, bio: String, (Boolean, String) -> Unit) -> Unit = { _, _, _, _, _, _ -> },
    onLogin: (username: String, passcode: String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {},
    onCreateAlbum: (title: String, description: String, genre: String, imageUrl: String, (Boolean, String) -> Unit) -> Unit = { _, _, _, _, _ -> },
    onPublishTrack: (album: com.example.data.model.UserAlbumEntity, title: String, genre: String, mood: String, streamUrl: String, imageUrl: String, bpm: Int, lyrics: String, (Boolean, String) -> Unit) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val moods = listOf("Cyberpunk Run", "Neon Lounge", "Cosmic Chill", "Dark Synth", "Retro Town")
    
    var activeLibraryTab by remember { mutableStateOf("Library") }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var playlistDesc by remember { mutableStateOf("") }

    // Dialog state handlers
    var showAuthDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var clickedCommunityAlbum by remember { mutableStateOf<com.example.data.model.UserAlbumEntity?>(null) }

    // Playlist selector modal states for adding a song
    var selectedSongForPlaylistAdd by remember { mutableStateOf<Song?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Futuristic Cyberdeck App Logo Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(CyberCyan, NeonPink)))
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.GraphicEq,
                            contentDescription = "PulseVibe",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PULSEWAVE",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "SENTIENT DIGITAL CORTECS v3.5",
                            color = CyberCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Profile connection badge or avatar
                if (currentUser != null) {
                    val badgeColor = remember(currentUser.avatarColor) {
                        try {
                            Color(android.graphics.Color.parseColor(currentUser.avatarColor))
                        } catch (e: Exception) {
                            CyberCyan
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberDarkSurface)
                            .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                            .clickable { showProfileDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentUser.displayName.uppercase(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 84.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = { showAuthDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, CyberCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Login, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "CONNECT NODE",
                                color = CyberCyan,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Cyber Glowing Outlined Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(text = "Search songs, genres, artists...", color = MutedText.copy(alpha = 0.6f), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = CyberCyan) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CyberDarkSurface,
                    unfocusedContainerColor = CyberDarkSurface
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(0.5.dp, GlassBorder, RoundedCornerShape(14.dp))
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDarkSurface)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (activeLibraryTab == "Library") CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { activeLibraryTab = "Library" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOCAL SOUND DECK",
                        color = if (activeLibraryTab == "Library") CyberCyan else MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (activeLibraryTab == "YouTube") NeonPink.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { activeLibraryTab = "YouTube" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = "Online",
                            tint = if (activeLibraryTab == "YouTube") NeonPink else MutedText,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YOUTUBE FINDER",
                            color = if (activeLibraryTab == "YouTube") NeonPink else MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        if (activeLibraryTab == "Library") {

        // Horizontal Mood Selector Row
        item {
            Column {
                Text(
                    text = "SYNAPSE MOOD INDEX",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "All" selector trigger
                    item {
                        MoodTagBadge(
                            text = "ALL MIXES",
                            isSelected = selectedMood == null,
                            onClick = { onMoodFilter(null) }
                        )
                    }

                    items(moods) { mood ->
                        MoodTagBadge(
                            text = mood.uppercase(),
                            isSelected = mood == selectedMood,
                            onClick = { onMoodFilter(mood) }
                        )
                    }
                }
            }
        }

        // Community Cyber-Albums horizontal list
        item {
            Column {
                Text(
                    text = "COMMUNITY CYBER-ALBUMS",
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (allUserAlbums.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberDarkSurface)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO SYSTEM ALBUMS BROADCASTED YET\nLOG IN TO PUBLISH YOUR OWN BEATS!",
                            color = MutedText.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allUserAlbums) { album: com.example.data.model.UserAlbumEntity ->
                            Column(
                                modifier = Modifier
                                    .width(115.dp)
                                    .clickable { clickedCommunityAlbum = album }
                            ) {
                                AsyncImage(
                                    model = album.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(115.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(CyberMutedSurface)
                                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = album.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "BY ${album.artistName.uppercase()}",
                                    color = CyberCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggested / Recently played horizontal scrolling cover grid
        if (recentlyPlayed.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "RECENT SYNAPTIC LINKS",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(recentlyPlayed) { song ->
                            RecentlyCard(
                                song = song,
                                onClick = { onSelectSong(song) }
                            )
                        }
                    }
                }
            }
        }

        // Playlist curation managers
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "USER DATABASES",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    IconButton(
                        onClick = { showCreatePlaylistDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Playlist", tint = CyberCyan)
                    }
                }

                if (playlists.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberDarkSurface)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO LOCAL PLAYLIST TABLES YET\nCLICK + TO MOUNT MATRIX",
                            color = MutedText,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        playlists.forEach { pl ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CyberDarkSurface)
                                    .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlaylistPlay, contentDescription = "Playlist", tint = CyberCyan, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = pl.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = pl.description, color = MutedText, fontSize = 11.sp)
                                }
                                IconButton(onClick = { onDeletePlaylist(pl.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Playlist", tint = NeonPink.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Primary Song directory directory
        item {
            Text(
                text = "PULSE AUDIO FILES",
                color = MutedText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        // Song vertical lists
        items(songs) { song ->
            val isLiked = likedIds.contains(song.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberDarkSurface)
                    .clickable { onSelectSong(song) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Static/Network mini Album art representation
                Image(
                    painter = rememberAsyncImagePainter(song.imageUrl),
                    contentDescription = "Cover Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${song.artist} • ${song.genre}",
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }

                // Plus button to insert to custom playlist
                if (playlists.isNotEmpty()) {
                    IconButton(onClick = { selectedSongForPlaylistAdd = song }) {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = "Add Playlist", tint = CyberCyan, modifier = Modifier.size(20.dp))
                    }
                }

                IconButton(onClick = { onToggleLike(song.id) }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Song",
                        tint = if (isLiked) NeonPink else MutedText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        } else {
            // YOUTUBE SEARCH FLOW RENDERING
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var tubeSearchValue by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = tubeSearchValue,
                        onValueChange = { tubeSearchValue = it },
                        placeholder = { Text("Search YouTube video titles...", color = MutedText.copy(alpha = 0.5f), fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = CyberDarkSurface,
                            unfocusedContainerColor = CyberDarkSurface
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onYtSearchSubmit(tubeSearchValue) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Scan", tint = Color.White)
                    }
                }
            }

            if (isSearchingYt) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = NeonPink, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "SCANNING REAL-TIME YOUTUBE INDEXING...",
                            color = NeonPink,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (ytSearchResults.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = "Cloud download", tint = MutedText.copy(alpha = 0.3f), modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "YOUTUBE CYBER DECK PORTAL\n\nSearch and download favorite YouTube music videos/songs to play offline with live synchronized lyrics and Avee visualizers automatically configured.",
                            color = MutedText,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "YOUTUBE DECK RESULTS",
                        color = MutedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(ytSearchResults) { track ->
                    val isDownloaded = downloadedIds.contains(track.id)
                    val progress = downloadProgress[track.id]
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberDarkSurface)
                            .border(1.dp, if (isDownloaded) NeonGreen.copy(alpha = 0.25f) else Color.Transparent, RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(track.imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = track.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "${track.artist} • Cached Streaming Layer", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            
                            if (progress != null && progress < 1.0f) {
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = progress,
                                    color = NeonPink,
                                    trackColor = CyberMutedSurface,
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Stream Immediately Button
                        IconButton(onClick = { onSelectSong(track) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Stream Song", tint = CyberCyan, modifier = Modifier.size(22.dp))
                        }

                        // Download Offline Cache Button
                        if (isDownloaded) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Cached", tint = NeonGreen, modifier = Modifier.size(22.dp))
                        } else {
                            IconButton(onClick = { onDownloadYtSong(track) }) {
                                Icon(Icons.Default.CloudDownload, contentDescription = "Download Song", tint = MutedText, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal AlertDialog for Playlist creation!
    if (showCreatePlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text(text = "NEW PLAYLIST MATRIX", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text(text = "TITLE NAME", color = CyberCyan) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = GlassBorder),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = playlistDesc,
                        onValueChange = { playlistDesc = it },
                        label = { Text(text = "DESCRIPTION", color = NeonPink) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = GlassBorder),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            onCreatePlaylist(playlistName, playlistDesc)
                            playlistName = ""
                            playlistDesc = ""
                            showCreatePlaylistDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text(text = "DEPLOY PLAYLIST", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) {
                    Text(text = "CANCEL", color = NeonPink)
                }
            },
            containerColor = CyberDarkSurface,
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
        )
    }

    // Modal dialog to Select Playlist to add a song into!
    selectedSongForPlaylistAdd?.let { song ->
        AlertDialog(
            onDismissRequest = { selectedSongForPlaylistAdd = null },
            title = { Text(text = "ADD TO MATRIX", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Select database destination partition for: ${song.title}", color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))
                    playlists.forEach { pl ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberMutedSurface)
                                .clickable {
                                    onAddSongToPlaylist(pl.id, song.id)
                                    selectedSongForPlaylistAdd = null
                                }
                                .padding(12.dp)
                        ) {
                            Text(text = pl.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedSongForPlaylistAdd = null }) {
                    Text(text = "ABORT", color = NeonPink)
                }
            },
            containerColor = CyberDarkSurface,
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
        )
    }

    if (showAuthDialog) {
        CyberAuthDialog(
            onDismiss = { showAuthDialog = false },
            onSignup = onSignup,
            onLogin = onLogin
        )
    }

    if (showProfileDialog && currentUser != null) {
        CyberProfileDialog(
            currentUser = currentUser,
            albums = allUserAlbums,
            onLogout = {
                onLogout()
                showProfileDialog = false
            },
            onDismiss = { showProfileDialog = false },
            onCreateAlbum = onCreateAlbum,
            onPublishTrack = onPublishTrack
        )
    }

    clickedCommunityAlbum?.let { album ->
        val albumTracks = songs.filter { it.album.equals(album.title, ignoreCase = true) }
        Dialog(onDismissRequest = { clickedCommunityAlbum = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.7f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0C0C14),
                border = BorderStroke(1.dp, CyberCyan)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = album.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberMutedSurface)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = album.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "BY ${album.artistName.uppercase()}", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = album.genre.uppercase(), color = MutedText, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = album.description, color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
                    Divider(color = DividerColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(text = "TRACKS IN COMPILATION", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (albumTracks.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("NO TRACKS PUBLISHED YET", color = MutedText, fontSize = 10.sp)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(albumTracks) { trk ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(CyberDarkSurface)
                                            .clickable {
                                                onSelectSong(trk)
                                                clickedCommunityAlbum = null
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = trk.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${trk.bpm} BPM • ${trk.genre}", color = MutedText, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { clickedCommunityAlbum = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CLOSE", color = Color.White)
                    }
                }
            }
        }
    }
}

// Mood Filter badge composable
@Composable
private fun MoodTagBadge(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) CyberCyan else GlassBorder,
                shape = RoundedCornerShape(30.dp)
            )
            .clickable { onClick() }
            .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else CyberDarkSurface)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) CyberCyan else MutedText,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}

// Recently played Album square composable
@Composable
private fun RecentlyCard(
    song: Song,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(115.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(song.imageUrl),
            contentDescription = "Cover Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(115.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = song.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            color = MutedText,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
