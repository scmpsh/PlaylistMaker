package com.practicum.playlistmaker.data.storage

import com.practicum.playlistmaker.data.dto.TrackDto

interface SharedPrefHistoryStorage {
    fun read(): List<TrackDto>
    fun write(track: TrackDto)
    fun cleanCachedTrackList()
}