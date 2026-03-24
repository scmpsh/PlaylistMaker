package com.practicum.playlistmaker.media.data

import android.content.Context
import android.net.Uri
import com.practicum.playlistmaker.media.domain.api.CoverStorageRepository
import java.io.File
import java.util.UUID

class CoverStorageRepositoryImpl(
    private val context: Context
) : CoverStorageRepository {

    override suspend fun saveCover(uri: Uri): String {
        val coversDir = File(context.filesDir, COVERS_DIRECTORY)
        if (!coversDir.exists()) {
            coversDir.mkdirs()
        }

        val targetFile = File(coversDir, "${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri).use { input ->
            targetFile.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        return targetFile.absolutePath
    }

    companion object {
        private const val COVERS_DIRECTORY = "playlist_covers"
    }
}