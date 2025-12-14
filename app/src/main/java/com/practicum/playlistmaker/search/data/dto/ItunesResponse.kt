package com.practicum.playlistmaker.search.data.dto

data class ItunesResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()