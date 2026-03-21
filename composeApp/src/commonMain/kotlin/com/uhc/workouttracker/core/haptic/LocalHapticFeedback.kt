package com.uhc.workouttracker.core.haptic

import androidx.compose.runtime.staticCompositionLocalOf

object NoOpHapticFeedback : HapticFeedback {
    override fun perform(type: HapticType) = Unit
}

val LocalHapticFeedback = staticCompositionLocalOf<HapticFeedback> { NoOpHapticFeedback }
