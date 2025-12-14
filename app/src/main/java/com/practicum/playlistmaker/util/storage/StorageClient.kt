package com.practicum.playlistmaker.util.storage

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?

    fun removeData()
}