package com.practicum.playlistmaker.search.data.network

import android.util.Log
import com.practicum.playlistmaker.search.data.dto.ItunesRequest
import com.practicum.playlistmaker.search.data.dto.Response

class RetrofitNetworkClient(
    private val itunesApi: ItunesApi
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is ItunesRequest) {
            try {
                val response = itunesApi.search(dto.term).execute()

                val body = response.body() ?: Response()

                return body.apply { resultCode = response.code() }
            } catch (e: Exception) {
                Log.w(TAG_NETWORK, e.message ?: e.toString())
                return Response().apply { resultCode = -1 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

    companion object {
        private const val TAG_NETWORK = "NETWORK"

    }
}