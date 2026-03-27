package com.uhc.workouttracker

import androidx.compose.ui.window.ComposeUIViewController
import com.uhc.workouttracker.di.initKoin
import com.uhc.workouttracker.di.iosDataModule
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            modules(iosDataModule)
        }
    }
) { App() }
