package com.practicum.playlistmaker.media.domain.api

import android.net.Uri

interface CoverStorageRepository {
    suspend fun saveCover(uri: Uri): String

}
