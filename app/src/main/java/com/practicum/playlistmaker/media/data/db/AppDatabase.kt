package com.practicum.playlistmaker.media.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.dao.TrackDao
import com.practicum.playlistmaker.media.data.db.dao.TracksInPlaylistsDao
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.media.data.db.entity.TracksInPlaylistsEntity

@Database(
    version = 3,
    entities = [TrackEntity::class, PlaylistEntity::class, TracksInPlaylistsEntity::class],
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3)
    ]
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun tracksInPlaylistsDao(): TracksInPlaylistsDao

}