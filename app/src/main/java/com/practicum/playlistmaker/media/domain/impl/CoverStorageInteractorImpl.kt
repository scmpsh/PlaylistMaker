package com.practicum.playlistmaker.media.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.media.domain.api.CoverStorageInteractor
import com.practicum.playlistmaker.media.domain.api.CoverStorageRepository

class CoverStorageInteractorImpl(
    private val coverStorageRepository: CoverStorageRepository
) : CoverStorageInteractor {
    override suspend fun saveCover(uri: Uri): String {
        return coverStorageRepository.saveCover(uri)
    }
}
