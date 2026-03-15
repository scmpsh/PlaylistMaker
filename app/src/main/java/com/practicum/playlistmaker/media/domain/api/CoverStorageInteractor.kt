package com.practicum.playlistmaker.media.domain.api

import android.net.Uri

interface CoverStorageInteractor {

    suspend fun saveCover(uri: Uri): String
}
