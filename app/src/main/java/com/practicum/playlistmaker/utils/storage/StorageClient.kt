package com.practicum.playlistmaker.utils.storage

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?

    fun removeData()
}