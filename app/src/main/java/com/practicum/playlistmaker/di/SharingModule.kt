package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }
    single<SharingInteractor> { SharingInteractorImpl(get()) }
}