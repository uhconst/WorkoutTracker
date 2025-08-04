package com.uhc.workouttracker

import androidx.compose.ui.window.ComposeUIViewController
import com.uhc.workouttracker.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }