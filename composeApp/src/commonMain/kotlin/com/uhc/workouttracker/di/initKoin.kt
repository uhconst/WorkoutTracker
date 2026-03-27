package com.uhc.workouttracker.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(additionalSetup: KoinApplication.() -> Unit = {}) {
    startKoin {
        additionalSetup()
        modules(dataModule, viewModelModule, supabaseModule)
    }
}
