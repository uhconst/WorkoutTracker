package com.uhc.workouttracker.wear.core.haptic

import android.view.HapticFeedbackConstants
import android.view.View

class AndroidHapticFeedback(private val view: View) : HapticFeedback {
    override fun perform(type: HapticType) {
        view.performHapticFeedback(
            when (type) {
                HapticType.Selection -> HapticFeedbackConstants.VIRTUAL_KEY
                HapticType.Confirm   -> HapticFeedbackConstants.CONFIRM
                HapticType.Reject    -> HapticFeedbackConstants.REJECT
                HapticType.Warning   -> HapticFeedbackConstants.REJECT
            }
        )
    }
}
