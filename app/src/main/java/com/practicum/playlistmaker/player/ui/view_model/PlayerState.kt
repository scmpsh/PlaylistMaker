package com.practicum.playlistmaker.player.ui.view_model

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String,
    val isFavorite: Boolean
) {
    class Default(isFavorite: Boolean = false) :
        PlayerState(true, "00:00", isFavorite)

    class Prepared(isFavorite: Boolean = false) :
        PlayerState(true, "00:00", isFavorite)

    class Playing(progress: String, isFavorite: Boolean = false) :
        PlayerState(false, progress, isFavorite)

    class Paused(progress: String, isFavorite: Boolean = false) :
        PlayerState(true, progress, isFavorite)
}
