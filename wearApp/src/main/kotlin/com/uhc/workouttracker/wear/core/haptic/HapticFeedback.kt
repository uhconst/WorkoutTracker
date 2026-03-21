package com.uhc.workouttracker.wear.core.haptic

enum class HapticType { Selection, Confirm, Reject, Warning }

interface HapticFeedback {
    fun perform(type: HapticType)
}
