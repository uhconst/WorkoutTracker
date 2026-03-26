package com.uhc.workouttracker.core.haptic

enum class HapticType { Selection, Confirm, Reject, Warning, ImpactLight, ImpactMedium, ImpactStrong }

interface HapticFeedback {
    fun perform(type: HapticType)
}
