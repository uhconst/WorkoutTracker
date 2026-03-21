package com.uhc.workouttracker.wear.core.haptic

import androidx.compose.runtime.staticCompositionLocalOf

object NoOpHapticFeedback : HapticFeedback {
    override fun perform(type: HapticType) = Unit
}

val LocalHapticFeedback = staticCompositionLocalOf<HapticFeedback> { NoOpHapticFeedback }
