package com.practicum.playlistmaker.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto

class SharedPrefHistoryStorageImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SharedPrefHistoryStorage {

    override fun read(): MutableList<TrackDto> {
        val cachedTrackListString = sharedPreferences.getString(TRACK_LIST_KEY, null)
            ?: return mutableListOf()
        return gson.fromJson(cachedTrackListString, Array<TrackDto>::class.java).toMutableList()
    }

    override fun write(track: TrackDto) {
        val cachedTrackList = read()
        if (cachedTrackList.size == MAX_CACHED_TRACK_LIST_SIZE) {
            cachedTrackList.removeAt(cachedTrackList.lastIndex)
        }
        if (cachedTrackList.none { it.trackId == track.trackId }) {
            cachedTrackList.add(0, track)
            sharedPreferences.edit {
                putString(TRACK_LIST_KEY, gson.toJson(cachedTrackList))
            }
        }
    }

    override fun cleanCachedTrackList() {
        sharedPreferences.edit {
            remove(TRACK_LIST_KEY)
        }
    }

    companion object {
        private const val TRACK_LIST_KEY = "TRACK_LIST_KEY"
        private const val MAX_CACHED_TRACK_LIST_SIZE = 10
    }
}