package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val streamUrl: String,
    val imageUrl: String,
    val genre: String,
    val mood: String,
    val lyrics: List<LyricLine> = emptyList(),
    val isLocal: Boolean = false,
    val bpm: Int = 120
)

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

@Entity(tableName = "custom_songs")
data class CustomSongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val streamUrl: String,
    val imageUrl: String,
    val genre: String,
    val mood: String,
    val lyricsRaw: String, // newline timestamp|text separated
    val bpm: Int = 120,
    val source: String = "YouTube"
)

@Entity(tableName = "liked_songs")
data class LikedSongEntity(
    @PrimaryKey val songId: String,
    val likedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey val songId: String,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isCustom: Boolean = true
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val displayName: String,
    val avatarColor: String = "#00F0FF",
    val userBio: String = "Cyberdeck Composer"
)

@Entity(tableName = "user_albums")
data class UserAlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String,
    val genre: String,
    val creatorUsername: String,
    val artistName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_songs", primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: String
)

@Entity(tableName = "visualizer_templates")
data class VisualizerTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val primaryColor: String = "#00F0FF", // Neon Cyan
    val secondaryColor: String = "#FF007F", // Neon Pink
    val glowColor: String = "#00FF66", // Neon Green
    val layoutStyle: String = "Circular Wave", // Circular Wave, Spectrum Bars, Neon Starburst, Line Wave
    val particleCount: Int = 60,
    val speedScale: Float = 1.0f,
    val sizeScale: Float = 1.0f,
    val isGlowEnabled: Boolean = true,
    val isBeatReactive: Boolean = true,
    val customText: String = "PulseWave Visuals",
    val backgroundType: String = "Neon Purple", // Glow Blue, Dark Cyber, Neon Purple, Custom Canvas
    val creatorName: String = "User"
)
