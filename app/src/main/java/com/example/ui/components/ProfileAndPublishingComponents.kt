package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.UserAlbumEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun CyberAuthDialog(
    onDismiss: () -> Unit,
    onSignup: (username: String, passcode: String, name: String, color: String, bio: String, (Boolean, String) -> Unit) -> Unit,
    onLogin: (username: String, passcode: String, (Boolean, String) -> Unit) -> Unit
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#00F0FF") } // Neon Cyan default

    val colorsOptions = listOf("#00F0FF", "#D946EF", "#10B981", "#F59E0B", "#EF4444")
    val context = LocalContext.current
    var isSubmitting by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .border(1.dp, if (isLoginTab) CyberCyan else NeonPink, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0C0C14)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        if (isLoginTab) Icons.Default.Login else Icons.Default.AppRegistration,
                        contentDescription = null,
                        tint = if (isLoginTab) CyberCyan else NeonPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isLoginTab) "CONNECT BIO-NODE" else "INITIALIZE CORE GENESIS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Dual Switch tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberMutedSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isLoginTab) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { isLoginTab = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOGIN",
                            color = if (isLoginTab) CyberCyan else MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isLoginTab) NeonPink.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { isLoginTab = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SIGNUP",
                            color = if (!isLoginTab) NeonPink else MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Credentials Text Fields
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.replace(" ", "") },
                    label = { Text("Username handle", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isLoginTab) CyberCyan else NeonPink,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it },
                    label = { Text("Passcode keys", fontSize = 11.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isLoginTab) CyberCyan else NeonPink,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                if (!isLoginTab) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio description", fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    // Avatar Color Choices
                    Text(
                        text = "AVATAR BIO-COLOR",
                        color = MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                    ) {
                        colorsOptions.forEach { colStr ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(colStr))
                            } catch (e: Exception) {
                                CyberCyan
                            }
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == colStr) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = colStr }
                            )
                        }
                    }
                }

                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = if (isLoginTab) CyberCyan else NeonPink,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            isSubmitting = true
                            if (isLoginTab) {
                                onLogin(username, passcode) { success, msg ->
                                    isSubmitting = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) onDismiss()
                                }
                            } else {
                                onSignup(username, passcode, displayName, selectedColor, bio) { success, msg ->
                                    isSubmitting = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        onDismiss()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLoginTab) CyberCyan else NeonPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isLoginTab) "LINK SYSTEMS" else "INITIALIZE MATRIX SOURCE",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = onDismiss) {
                    Text(text = "CANCEL LINK", color = MutedText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun CyberProfileDialog(
    currentUser: UserEntity,
    albums: List<UserAlbumEntity>,
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
    onCreateAlbum: (title: String, description: String, genre: String, imageUrl: String, (Boolean, String) -> Unit) -> Unit,
    onPublishTrack: (album: UserAlbumEntity, title: String, genre: String, mood: String, streamUrl: String, imageUrl: String, bpm: Int, lyrics: String, (Boolean, String) -> Unit) -> Unit
) {
    var showCreateAlbum by remember { mutableStateOf(false) }
    var selectedAlbumForTracks by remember { mutableStateOf<UserAlbumEntity?>(null) }
    val myAlbums = remember(albums, currentUser) {
        albums.filter { it.creatorUsername == currentUser.username }
    }

    val selectedAlbum = selectedAlbumForTracks

    if (showCreateAlbum) {
        PublishAlbumDialog(
            onDismiss = { showCreateAlbum = false },
            onCreate = { title, desc, genre, url, cb ->
                onCreateAlbum(title, desc, genre, url) { ok, msg ->
                    cb(ok, msg)
                    if (ok) {
                        showCreateAlbum = false
                    }
                }
            }
        )
    } else if (selectedAlbum != null) {
        AlbumDetailDialog(
            album = selectedAlbum,
            onDismiss = { selectedAlbumForTracks = null },
            onPublishTrack = { title, genre, mood, streamUrl, imageUrl, bpm, lyrics, cb ->
                onPublishTrack(selectedAlbum, title, genre, mood, streamUrl, imageUrl, bpm, lyrics, cb)
            }
        )
    } else {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF07070F),
                border = BorderStroke(1.dp, CyberCyan)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Profile Header card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberDarkSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarColor = remember(currentUser.avatarColor) {
                            try {
                                Color(android.graphics.Color.parseColor(currentUser.avatarColor))
                            } catch (e: Exception) {
                                CyberCyan
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(avatarColor)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.displayName.uppercase(),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "@${currentUser.username} • GOLD NODE",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentUser.userBio,
                                color = MutedText,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Section Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY PUBLISHED ALBUMS",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Button(
                            onClick = { showCreateAlbum = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, CyberCyan),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Publish, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("NEW ALBUM", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Albums List
                    Box(modifier = Modifier.weight(1f)) {
                        if (myAlbums.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberDarkSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Album, contentDescription = null, tint = MutedText.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "NO MOUNTED COMPILATIONS\nCREATE ALBUM TO DISPENSE BEATS",
                                        color = MutedText,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(myAlbums) { album ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CyberDarkSurface)
                                            .border(0.5.dp, GlassBorder, RoundedCornerShape(12.dp))
                                            .clickable { selectedAlbumForTracks = album }
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = album.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(CyberMutedSurface)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = album.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text(text = album.genre.uppercase(), color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text(text = album.description, color = MutedText, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedText, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Lower actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onLogout) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DE-AUTHORIZE USER", color = NeonPink, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("CLOSE PROFILE", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublishAlbumDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, genre: String, imageUrl: String, (Boolean, String) -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0C0C14),
            border = BorderStroke(1.dp, CyberCyan)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DEPLOY ALBUM COMPILATION",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Album Title", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = GlassBorder),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre (e.g. Synthwave)", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = GlassBorder),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Compilation Synopsis / Bio", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = GlassBorder),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Cover Image URL (Optional)", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberCyan, unfocusedBorderColor = GlassBorder),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                )

                if (isSubmitting) {
                    CircularProgressIndicator(color = CyberCyan)
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("CANCEL", color = MutedText, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                isSubmitting = true
                                onCreate(title, desc, genre, imageUrl) { ok, msg ->
                                    isSubmitting = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text("DEPLOY COMPILATION", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumDetailDialog(
    album: UserAlbumEntity,
    onDismiss: () -> Unit,
    onPublishTrack: (title: String, genre: String, mood: String, streamUrl: String, imageUrl: String, bpm: Int, lyrics: String, (Boolean, String) -> Unit) -> Unit
) {
    var showPublishTrack by remember { mutableStateOf(false) }

    if (showPublishTrack) {
        PublishTrackDialog(
            onDismiss = { showPublishTrack = false },
            onPublish = { title, genre, mood, url, b, lyr, cb ->
                onPublishTrack(title, genre, mood, url, album.imageUrl, b, lyr) { ok, msg ->
                    cb(ok, msg)
                    if (ok) {
                        showPublishTrack = false
                    }
                }
            }
        )
    } else {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF07070F),
                border = BorderStroke(1.dp, NeonPink)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Album Card info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = album.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = album.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Artist: ${album.artistName.uppercase()}", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = album.genre.uppercase(), color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = album.description, color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(bottom = 16.dp))

                    Divider(color = DividerColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Button(
                        onClick = { showPublishTrack = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Publish, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PUBLISH TRACK TO COMPILATION", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("BACK", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublishTrackDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, genre: String, mood: String, streamUrl: String, bpm: Int, lyrics: String, (Boolean, String) -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("Cyberpunk Run") }
    var streamUrl by remember { mutableStateOf("") }
    var bpmString by remember { mutableStateOf("120") }
    var lyricsRaw by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val moodsList = listOf("Cyberpunk Run", "Neon Lounge", "Cosmic Chill", "Dark Synth", "Retro Town")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0C0C14),
            border = BorderStroke(1.dp, NeonPink)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Text(
                    text = "PUBLISH DIGI-TRACK",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Track Title", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = GlassBorder),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Genre (Optional, custom overrides album)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = GlassBorder),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = bpmString,
                            onValueChange = { bpmString = it },
                            label = { Text("BPM Tempo (Integer)", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = GlassBorder),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = streamUrl,
                            onValueChange = { streamUrl = it },
                            label = { Text("Streamable URL (MP3 format, Optional)", fontSize = 11.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = GlassBorder),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text(
                            text = "MOOD CLASSIFIER",
                            color = MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            moodsList.take(3).forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (mood == m) NeonPink.copy(alpha = 0.2f) else CyberDarkSurface)
                                        .border(0.5.dp, if (mood == m) NeonPink else GlassBorder, RoundedCornerShape(6.dp))
                                        .clickable { mood = m }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(m, color = if (mood == m) NeonPink else MutedText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = lyricsRaw,
                            onValueChange = { lyricsRaw = it },
                            label = { Text("Synced Lyrics Matrix (ms|LRC string format)", fontSize = 11.sp) },
                            placeholder = { Text("e.g.\n2000|[Synth intro drum kick]\n10000|Cyber beats in alignment", fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = GlassBorder),
                            minLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isSubmitting) {
                    CircularProgressIndicator(color = NeonPink, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("CANCEL", color = MutedText, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                val bpm = bpmString.toIntOrNull() ?: 120
                                isSubmitting = true
                                onPublish(title, genre, mood, streamUrl, bpm, lyricsRaw) { ok, msg ->
                                    isSubmitting = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                        ) {
                            Text("DISPENSE ON GRID", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
