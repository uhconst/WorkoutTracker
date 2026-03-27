package com.uhc.workouttracker

import androidx.compose.ui.window.ComposeUIViewController
import com.uhc.workouttracker.di.initKoin
import com.uhc.workouttracker.di.iosDatabaseModule
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            modules(iosDatabaseModule)
        }
    }
) { App() }
