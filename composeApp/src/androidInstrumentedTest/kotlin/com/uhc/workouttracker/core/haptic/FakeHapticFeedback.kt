package com.uhc.workouttracker.core.haptic

class FakeHapticFeedback : HapticFeedback {
    val performed = mutableListOf<HapticType>()
    override fun perform(type: HapticType) { performed.add(type) }
    val last: HapticType? get() = performed.lastOrNull()
}
