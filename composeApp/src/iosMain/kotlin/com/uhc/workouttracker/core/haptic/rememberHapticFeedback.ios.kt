package com.uhc.workouttracker.core.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyleHeavy
import platform.UIKit.UIImpactFeedbackStyleLight
import platform.UIKit.UIImpactFeedbackStyleMedium
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackTypeError
import platform.UIKit.UINotificationFeedbackTypeSuccess
import platform.UIKit.UINotificationFeedbackTypeWarning
import platform.UIKit.UISelectionFeedbackGenerator

@Composable
actual fun rememberHapticFeedback(): HapticFeedback = remember { IosHapticFeedback() }

class IosHapticFeedback : HapticFeedback {
    private val selectionGenerator    = UISelectionFeedbackGenerator()
    private val notificationGenerator = UINotificationFeedbackGenerator()
    private val impactLightGenerator  = UIImpactFeedbackGenerator(UIImpactFeedbackStyleLight)
    private val impactMediumGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyleMedium)
    private val impactHeavyGenerator  = UIImpactFeedbackGenerator(UIImpactFeedbackStyleHeavy)

    override fun perform(type: HapticType) {
        when (type) {
            HapticType.Selection    -> selectionGenerator.selectionChanged()
            HapticType.Confirm      -> notificationGenerator.notificationOccurred(UINotificationFeedbackTypeSuccess)
            HapticType.Reject       -> notificationGenerator.notificationOccurred(UINotificationFeedbackTypeError)
            HapticType.Warning      -> notificationGenerator.notificationOccurred(UINotificationFeedbackTypeWarning)
            HapticType.ImpactLight  -> impactLightGenerator.impactOccurred()
            HapticType.ImpactMedium -> impactMediumGenerator.impactOccurred()
            HapticType.ImpactStrong -> impactHeavyGenerator.impactOccurred()
        }
    }
}
