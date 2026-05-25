package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    // Liked Songs
    @Query("SELECT songId FROM liked_songs")
    fun getLikedSongIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedSong(likedSong: LikedSongEntity)

    @Query("DELETE FROM liked_songs WHERE songId = :songId")
    suspend fun deleteLikedSong(songId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM liked_songs WHERE songId = :songId)")
    suspend fun isSongLiked(songId: String): Boolean

    // Recently Played
    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 15")
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyPlayed(recentlyPlayed: RecentlyPlayedEntity)

    @Query("DELETE FROM recently_played WHERE songId = :songId")
    suspend fun deleteRecentlyPlayed(songId: String)

    // Custom Songs
    @Query("SELECT * FROM custom_songs ORDER BY id DESC")
    fun getAllCustomSongs(): Flow<List<CustomSongEntity>>

    @Query("SELECT * FROM custom_songs WHERE id = :songId")
    suspend fun getCustomSongById(songId: String): CustomSongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomSong(song: CustomSongEntity)

    @Query("DELETE FROM custom_songs WHERE id = :songId")
    suspend fun deleteCustomSong(songId: String)

    // Playlists
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: String)

    @Query("SELECT songId FROM playlist_songs WHERE playlistId = :playlistId")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<String>>

    // User Accounts Authentication
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash LIMIT 1")
    suspend fun loginUser(username: String, passwordHash: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    // Published Custom Albums
    @Query("SELECT * FROM user_albums ORDER BY createdAt DESC")
    fun getAllAlbums(): Flow<List<UserAlbumEntity>>

    @Query("SELECT * FROM user_albums WHERE creatorUsername = :username ORDER BY createdAt DESC")
    fun getAlbumsByCreator(username: String): Flow<List<UserAlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: UserAlbumEntity): Long

    @Query("DELETE FROM user_albums WHERE id = :albumId")
    suspend fun deleteAlbum(albumId: Long)
}

@Dao
interface VisualizerTemplateDao {
    @Query("SELECT * FROM visualizer_templates ORDER BY id ASC")
    fun getAllTemplates(): Flow<List<VisualizerTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: VisualizerTemplate): Long

    @Query("DELETE FROM visualizer_templates WHERE id = :id")
    suspend fun deleteTemplate(id: Long)

    @Query("SELECT * FROM visualizer_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): VisualizerTemplate?
}
