package com.uhc.workouttracker.core.haptic

import kotlin.test.Test
import kotlin.test.assertEquals

class HapticTypeTest {

    @Test
    fun `HapticType has all expected values`() {
        assertEquals(
            setOf("Selection", "Confirm", "Reject", "Warning", "ImpactLight", "ImpactMedium", "ImpactStrong"),
            HapticType.entries.map { it.name }.toSet()
        )
    }
}
