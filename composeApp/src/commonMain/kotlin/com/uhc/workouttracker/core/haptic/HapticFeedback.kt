package com.uhc.workouttracker.core.haptic

enum class HapticType { Selection, Confirm, Reject, Warning }

interface HapticFeedback {
    fun perform(type: HapticType)
}
