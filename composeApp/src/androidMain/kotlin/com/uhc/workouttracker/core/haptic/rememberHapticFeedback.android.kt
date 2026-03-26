package com.uhc.workouttracker.core.haptic

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) { AndroidHapticFeedback(view) }
}

class AndroidHapticFeedback(private val view: View) : HapticFeedback {
    override fun perform(type: HapticType) {
        view.performHapticFeedback(
            when (type) {
                HapticType.Selection    -> HapticFeedbackConstants.VIRTUAL_KEY
                HapticType.Confirm      -> HapticFeedbackConstants.CONFIRM
                HapticType.Reject       -> HapticFeedbackConstants.REJECT
                HapticType.Warning      -> HapticFeedbackConstants.REJECT
                HapticType.ImpactLight  -> HapticFeedbackConstants.VIRTUAL_KEY
                HapticType.ImpactMedium -> HapticFeedbackConstants.CLOCK_TICK
                HapticType.ImpactStrong -> HapticFeedbackConstants.CONFIRM
            }
        )
    }
}
