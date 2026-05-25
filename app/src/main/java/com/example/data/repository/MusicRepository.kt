package com.example.data.repository

import android.content.Context
import com.example.data.database.MusicDao
import com.example.data.database.VisualizerTemplateDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class MusicRepository(
    private val musicDao: MusicDao,
    private val templateDao: VisualizerTemplateDao
) {
    // Standard Synthwave / Cyberpunk mock songs with stable public stream links
    val staticSongs = listOf(
        Song(
            id = "pulsewave_01",
            title = "Quantum Highway",
            artist = "CyberScribe",
            album = "Digital Drift 2099",
            durationMs = 372000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            imageUrl = "https://picsum.photos/id/116/400/400",
            genre = "Synthwave",
            mood = "Cyberpunk Run",
            bpm = 125,
            lyrics = listOf(
                LyricLine(2000, "[Instrumental Intro - Neon lights flicker]"),
                LyricLine(10000, "System status: Online. Subsystems: Loaded."),
                LyricLine(15000, "Initiating hyper-drive... Destination: Neo-Tokyo."),
                LyricLine(20000, "Gazing through the HUD, wireframe rain begins to fall."),
                LyricLine(28000, "Accelerating down the digital speedway, feeling so free."),
                LyricLine(35000, "Chasing shadows in a virtual city of glass and copper!"),
                LyricLine(42000, "Oh, we ride the Quantum Highway tonight."),
                LyricLine(49000, "Escaping the corporate algorithms, stepping in the light."),
                LyricLine(56000, "No firewalls can contain this terminal beat..."),
                LyricLine(63000, "Neon pulses rushing beneath our chrome feet!"),
                LyricLine(70000, "[Synthesizer Solo - Pure Outrun energy]"),
                LyricLine(85000, "Frequencies aligning, waveform expanding slow."),
                LyricLine(92000, "Looking at the mirror, retro sunset glow."),
                LyricLine(99000, "Will you join me on this grid, where the visualizers show?"),
                LyricLine(106000, "We are the PulseWave, let the dark database flow!")
            )
        ),
        Song(
            id = "pulsewave_02",
            title = "Neon Monsoon",
            artist = "TokyoGlitch",
            album = "Shibuya Rain",
            durationMs = 423000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            imageUrl = "https://picsum.photos/id/124/400/400",
            genre = "Vaporwave",
            mood = "Neon Lounge",
            bpm = 100,
            lyrics = listOf(
                LyricLine(1000, "[Rain pattern intro - ambient soft lofi]"),
                LyricLine(8000, "Warm rain dripping over hologram billboards."),
                LyricLine(16000, "Sipping synthetic matcha under flickering neon boards."),
                LyricLine(24000, "Lost in the crowd, translating advertisements in Shibuya."),
                LyricLine(32000, "Our souls are code, wandering the static rain."),
                LyricLine(40000, "Welcome to the Neon Monsoon... lose all your pain."),
                LyricLine(48000, "Let the analog cassette deck rotate again."),
                LyricLine(56000, "Dreaming of visual vectors, in a past that never was."),
                LyricLine(64000, "Just cybernetic nostalgia... floating in the dust.")
            )
        ),
        Song(
            id = "pulsewave_03",
            title = "Grid Runner",
            artist = "VectorCore",
            album = "Megastructure",
            durationMs = 302000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            imageUrl = "https://picsum.photos/id/133/400/400",
            genre = "Outrun",
            mood = "Cyberpunk Run",
            bpm = 135,
            lyrics = listOf(
                LyricLine(3000, "[Pulse beat build up - High tension Outrun]"),
                LyricLine(12000, "They are chasing us from Sector Three!"),
                LyricLine(16000, "Grid Runners, gear up, set the spectrum filter free!"),
                LyricLine(20000, "Maximum speed, do not look back into the terminal."),
                LyricLine(24000, "Avee circles spinning, neon reactive, sub-orbital!"),
                LyricLine(28000, "Neon sparks rise as we tear through the asphalt grid."),
                LyricLine(32000, "Run, runner! You know what the megacorp did!"),
                LyricLine(36000, "Break the firewall! Unlock the code base!"),
                LyricLine(40000, "This is our revolution, our final escape race!")
            )
        ),
        Song(
            id = "pulsewave_04",
            title = "Starlight Drift",
            artist = "Nebula9",
            album = "Cosmic Chillout",
            durationMs = 302000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            imageUrl = "https://picsum.photos/id/200/400/400",
            genre = "Lofi",
            mood = "Cosmic Chill",
            bpm = 85,
            lyrics = listOf(
                LyricLine(2000, "[Space wind ambient intro]"),
                LyricLine(10000, "Adrift in galactic dust, engine vibrating soft and low."),
                LyricLine(20000, "Watching distant supernovas flare and decay in slow-mo."),
                LyricLine(30000, "No distress beacons, just starlight in my eyes."),
                LyricLine(40000, "Chill frequencies floating... under cosmic skies."),
                LyricLine(50000, "Close your eyes, breathe in the vacuum cold."),
                LyricLine(60000, "Let the asteroid field paint the circular screen gold.")
            )
        ),
        Song(
            id = "pulsewave_05",
            title = "Dark Matter Beat",
            artist = "Xenon",
            album = "Synthesized Void",
            durationMs = 302000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            imageUrl = "https://picsum.photos/id/180/400/400",
            genre = "Dark Synth",
            mood = "Dark Hype",
            bpm = 140,
            lyrics = listOf(
                LyricLine(1000, "[Heavy industrial distortion drone]"),
                LyricLine(8000, "Into the virtual dark. Where the rogue AI reigns."),
                LyricLine(15000, "Iron-clad codes, electronic chains."),
                LyricLine(22000, "Feel the sub-bass slamming, vibrating the concrete deck."),
                LyricLine(29000, "Glitch in power grids, a beautiful server wreck."),
                LyricLine(36000, "Dark waves, glow waves, beat-reactive neon lines."),
                LyricLine(43000, "We emerge from the underground cyber mines!")
            )
        ),
        Song(
            id = "pulsewave_06",
            title = "Chrome Horizon",
            artist = "LazerGlance",
            album = "Sunsets in Vector",
            durationMs = 302000,
            streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            imageUrl = "https://picsum.photos/id/142/400/400",
            genre = "Retrowave",
            mood = "Retro Town",
            bpm = 110,
            lyrics = listOf(
                LyricLine(2000, "[Classic 80s gated snare drum roll]"),
                LyricLine(10000, "Drivin' down Sunset Blvd with a VHS camera in hand."),
                LyricLine(18000, "Synthesizer melodies drifting across the orange sand."),
                LyricLine(26000, "Sunglasses at night, neon reflections so bright."),
                LyricLine(34000, "We are living in a dream, a retro-future paradise sight!"),
                LyricLine(42000, "Reach for the Chrome Horizon, where the lines align!"),
                LyricLine(50000, "Spinning classic visualizers, feeling out of time...")
            )
        )
    )

    // Liked Songs Flow
    val likedSongIds: Flow<List<String>> = musicDao.getLikedSongIds()

    // Lyrics Serializer/Deserializer Helpers
    fun lyricsListToString(lyrics: List<LyricLine>): String {
        return lyrics.joinToString("\n") { "${it.timestampMs}|${it.text}" }
    }

    fun stringToLyricsList(text: String): List<LyricLine> {
        if (text.isBlank()) return emptyList()
        return text.lines().mapNotNull { line ->
            val parts = line.split("|", limit = 2)
            if (parts.size == 2) {
                val ts = parts[0].toLongOrNull() ?: 0L
                LyricLine(ts, parts[1])
            } else {
                null
            }
        }
    }

    // Custom Songs Flow
    val customSongsFlow: Flow<List<Song>> = musicDao.getAllCustomSongs().map { entities ->
        entities.map { entity ->
            Song(
                id = entity.id,
                title = entity.title,
                artist = entity.artist,
                album = entity.album,
                durationMs = entity.durationMs,
                streamUrl = entity.streamUrl,
                imageUrl = entity.imageUrl,
                genre = entity.genre,
                mood = entity.mood,
                lyrics = stringToLyricsList(entity.lyricsRaw),
                isLocal = true,
                bpm = entity.bpm
            )
        }
    }

    // All combined songs flow
    val allSongsFlow: Flow<List<Song>> = customSongsFlow.map { customs ->
        staticSongs + customs
    }

    // Recently Played Flow mapped to complete Song models
    val recentlyPlayedSongs: Flow<List<Song>> = combine(
        musicDao.getRecentlyPlayed(),
        customSongsFlow
    ) { recentlyEntities, customs ->
        val mergedList = staticSongs + customs
        recentlyEntities.mapNotNull { entity ->
            mergedList.find { it.id == entity.songId }
        }
    }

    suspend fun insertCustomSong(song: Song) {
        val entity = CustomSongEntity(
            id = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            durationMs = song.durationMs,
            streamUrl = song.streamUrl,
            imageUrl = song.imageUrl,
            genre = song.genre,
            mood = song.mood,
            lyricsRaw = lyricsListToString(song.lyrics),
            bpm = song.bpm,
            source = "YouTube"
        )
        musicDao.insertCustomSong(entity)
    }

    suspend fun deleteCustomSong(songId: String) {
        musicDao.deleteCustomSong(songId)
    }

    suspend fun toggleLike(songId: String) {
        val liked = musicDao.isSongLiked(songId)
        if (liked) {
            musicDao.deleteLikedSong(songId)
        } else {
            musicDao.insertLikedSong(LikedSongEntity(songId))
        }
    }

    suspend fun isLiked(songId: String): Boolean {
        return musicDao.isSongLiked(songId)
    }

    suspend fun addRecentlyPlayed(songId: String) {
        musicDao.insertRecentlyPlayed(RecentlyPlayedEntity(songId))
    }

    // Playlists
    val playlists: Flow<List<PlaylistEntity>> = musicDao.getAllPlaylists()

    suspend fun createPlaylist(name: String, description: String): Long {
        return musicDao.insertPlaylist(PlaylistEntity(name = name, description = description))
    }

    suspend fun deletePlaylist(playlistId: Long) {
        musicDao.deletePlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: String) {
        musicDao.insertPlaylistSong(PlaylistSongCrossRef(playlistId, songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        musicDao.deletePlaylistSong(playlistId, songId)
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return combine(
            musicDao.getSongsInPlaylist(playlistId),
            customSongsFlow
        ) { ids, customs ->
            val mergedList = staticSongs + customs
            mergedList.filter { ids.contains(it.id) }
        }
    }

    // Custom Visualizer Templates
    val templates: Flow<List<VisualizerTemplate>> = templateDao.getAllTemplates()

    suspend fun insertTemplate(template: VisualizerTemplate): Long {
        return templateDao.insertTemplate(template)
    }

    suspend fun deleteTemplate(id: Long) {
        templateDao.deleteTemplate(id)
    }

    suspend fun loadDefaultTemplatesIfEmpty() {
        // We'll populate a few basic Avee style templates to start with
        val defaults = listOf(
            VisualizerTemplate(
                id = 1,
                name = "Avee Cyber Circular",
                primaryColor = "#00FFFF", // Cyber Cyan
                secondaryColor = "#FF007F", // Neon Pink
                glowColor = "#00FF66", // Glow Green
                layoutStyle = "Circular Wave",
                particleCount = 80,
                speedScale = 1.2f,
                sizeScale = 1.0f,
                isGlowEnabled = true,
                isBeatReactive = true,
                customText = "Cyber Sector 4",
                backgroundType = "Dark Cyber"
            ),
            VisualizerTemplate(
                id = 2,
                name = "Neon Starburst Specter",
                primaryColor = "#FF00FF", // Purple Neon
                secondaryColor = "#FFFF00", // Bright Yellow
                glowColor = "#FF007F",
                layoutStyle = "Neon Starburst",
                particleCount = 120,
                speedScale = 1.5f,
                sizeScale = 0.8f,
                isGlowEnabled = true,
                isBeatReactive = true,
                customText = "PulseWave 60FPS",
                backgroundType = "Neon Purple"
            ),
            VisualizerTemplate(
                id = 3,
                name = "Retrowave Spec-Bars",
                primaryColor = "#FF5E00", // Sunny Orange
                secondaryColor = "#FF007F", // Neon Magenta
                glowColor = "#FFFF00",
                layoutStyle = "Spectrum Bars",
                particleCount = 40,
                speedScale = 0.8f,
                sizeScale = 1.2f,
                isGlowEnabled = false,
                isBeatReactive = true,
                customText = "Grid Runner 2099",
                backgroundType = "Glow Blue"
            ),
            VisualizerTemplate(
                id = 4,
                name = "Liquid Neon Wave",
                primaryColor = "#00FF66", // Vibrant Green
                secondaryColor = "#00FFFF", // Cyber Cyan
                glowColor = "#00FF66",
                layoutStyle = "Line Wave",
                particleCount = 50,
                speedScale = 1.1f,
                sizeScale = 1.5f,
                isGlowEnabled = true,
                isBeatReactive = true,
                customText = "Velocity Vectors",
                backgroundType = "Neon Purple"
            )
        )
        for (tmpl in defaults) {
            if (templateDao.getTemplateById(tmpl.id) == null) {
                templateDao.insertTemplate(tmpl)
            }
        }
    }

    // User authentication / registrations
    val allUsers: Flow<List<UserEntity>> = musicDao.getAllUsers()

    suspend fun getUserByUsername(username: String): UserEntity? = musicDao.getUserByUsername(username)

    suspend fun registerUser(user: UserEntity) {
        musicDao.registerUser(user)
    }

    suspend fun loginUser(username: String, passwordHash: String): UserEntity? {
        return musicDao.loginUser(username, passwordHash)
    }

    // Dynamic Albums
    val allUserAlbums: Flow<List<UserAlbumEntity>> = musicDao.getAllAlbums()

    fun getAlbumsByCreator(username: String): Flow<List<UserAlbumEntity>> {
        return musicDao.getAlbumsByCreator(username)
    }

    suspend fun createAlbum(album: UserAlbumEntity): Long {
        return musicDao.insertAlbum(album)
    }

    suspend fun deleteAlbum(albumId: Long) {
        musicDao.deleteAlbum(albumId)
    }
}
