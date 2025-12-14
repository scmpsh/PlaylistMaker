package com.practicum.playlistmaker.util.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.creator.Creator
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type
) : StorageClient<T> {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PLAYLIST_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Creator.provideGson()

    override fun storeData(data: T) {
        prefs.edit { putString(dataKey, gson.toJson(data, type)) }
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        return if (dataJson == null) {
            null
        } else {
            gson.fromJson(dataJson, type)
        }
    }

    override fun removeData() {
        prefs.edit { remove(dataKey) }
    }

    companion object {
        private const val PLAYLIST_PREFS_NAME = "PLAYLIST_PREFS"
    }
}