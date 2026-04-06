package com.uhc.workouttracker.core.haptic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

@Composable
actual fun rememberHapticFeedback(): HapticFeedback = remember { IosHapticFeedback() }

class IosHapticFeedback : HapticFeedback {
    private val selectionGenerator    = UISelectionFeedbackGenerator()
    private val notificationGenerator = UINotificationFeedbackGenerator()
    private val impactLightGenerator  = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    private val impactMediumGenerator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    private val impactHeavyGenerator  = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)

    override fun perform(type: HapticType) {
        when (type) {
            HapticType.Selection    -> selectionGenerator.selectionChanged()
            HapticType.Confirm      -> notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
            HapticType.Reject       -> notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
            HapticType.Warning      -> notificationGenerator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
            HapticType.ImpactLight  -> impactLightGenerator.impactOccurred()
            HapticType.ImpactMedium -> impactMediumGenerator.impactOccurred()
            HapticType.ImpactStrong -> impactHeavyGenerator.impactOccurred()
        }
    }
}
