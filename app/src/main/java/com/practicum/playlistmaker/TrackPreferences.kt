package com.practicum.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.Track

class TrackPreferences {

    fun read(sharedPreferences: SharedPreferences): MutableList<Track> {
        val cachedTrackListString = sharedPreferences.getString(TRACK_LIST_KEY, "")
        return Gson().fromJson(cachedTrackListString, Array<Track>::class.java)?.toMutableList()
            ?: emptyArray<Track>().toMutableList()
    }

    fun write(sharedPreferences: SharedPreferences, track: Track) {
        val cachedTrackList = read(sharedPreferences)
        if (cachedTrackList.size == MAX_CACHED_TRACK_LIST_SIZE) {
            cachedTrackList.removeAt(cachedTrackList.lastIndex)
        }
        if (cachedTrackList.none { it.trackId == track.trackId }) {
            cachedTrackList.add(0, track)
            sharedPreferences.edit {
                putString(TRACK_LIST_KEY, Gson().toJson(cachedTrackList))
            }
        }
    }

    fun cleanCachedTrackList(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit {
            remove(TRACK_LIST_KEY)
        }
    }

    companion object {
        private const val TRACK_LIST_KEY = "TRACK_LIST_KEY"
        private const val MAX_CACHED_TRACK_LIST_SIZE = 10
    }
}