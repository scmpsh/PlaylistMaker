package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.ItunesRequest
import com.practicum.playlistmaker.data.dto.Response

class RetrofitNetworkClient(
    private val itunesApi: ItunesApi
): NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is ItunesRequest) {
            val response = itunesApi.search(dto.term).execute()
            val body = response.body() ?: Response()

            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}