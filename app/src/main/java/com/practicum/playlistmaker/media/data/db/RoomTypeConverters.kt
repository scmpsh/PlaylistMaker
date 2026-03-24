package com.practicum.playlistmaker.media.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class RoomTypeConverters {

    private val gson = Gson()
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromOffsetDateTime(value: OffsetDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return formatter.parse(it, OffsetDateTime::from)
        }
    }

    @TypeConverter
    fun toList(value: String): List<Int> {
        return gson.fromJson(value, Array<Int>::class.java).toList()
    }

    @TypeConverter
    fun fromList(list: List<Int>): String {
        return gson.toJson(list)
    }
}
