package com.practicum.playlistmaker.util.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val prefs: SharedPreferences,
    private val dataKey: String,
    private val type: Type,
    private val gson: Gson
) : StorageClient<T> {

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
}