package com.practicum.playlistmaker.data.dto

data class ItunesResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()