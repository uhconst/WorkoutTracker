package com.uhc.workouttracker.core.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackTypeError
import platform.UIKit.UINotificationFeedbackTypeSuccess
import platform.UIKit.UINotificationFeedbackTypeWarning
import platform.UIKit.UISelectionFeedbackGenerator

@Composable
actual fun rememberHapticFeedback(): HapticFeedback = remember { IosHapticFeedback() }

class IosHapticFeedback : HapticFeedback {
    private val selectionGenerator = UISelectionFeedbackGenerator()
    private val notificationGenerator = UINotificationFeedbackGenerator()

    override fun perform(type: HapticType) {
        when (type) {
            HapticType.Selection -> selectionGenerator.selectionChanged()
            HapticType.Confirm   -> notificationGenerator.notificationOccurred(UINotificationFeedbackTypeSuccess)
            HapticType.Reject    -> notificationGenerator.notificationOccurred(UINotificationFeedbackTypeError)
            HapticType.Warning   -> notificationGenerator.notificationOccurred(UINotificationFeedbackTypeWarning)
        }
    }
}
