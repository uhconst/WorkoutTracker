package com.uhc.workouttracker.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import workouttracker.composeapp.generated.resources.Res
import workouttracker.composeapp.generated.resources.inter_bold
import workouttracker.composeapp.generated.resources.inter_medium
import workouttracker.composeapp.generated.resources.inter_regular
import workouttracker.composeapp.generated.resources.inter_semibold

@Composable
fun interFontFamily() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

@Composable
fun appTypography(): Typography {
    val inter = interFontFamily()
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        displayMedium = base.displayMedium.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        displaySmall = base.displaySmall.copy(fontFamily = inter, fontWeight = FontWeight.Bold),
        headlineLarge = base.headlineLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        headlineMedium = base.headlineMedium.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontFamily = inter, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        titleSmall = base.titleSmall.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        bodyLarge = base.bodyLarge.copy(fontFamily = inter, fontWeight = FontWeight.Normal),
        bodyMedium = base.bodyMedium.copy(fontFamily = inter, fontWeight = FontWeight.Normal),
        bodySmall = base.bodySmall.copy(fontFamily = inter, fontWeight = FontWeight.Normal),
        labelLarge = base.labelLarge.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
        labelSmall = base.labelSmall.copy(fontFamily = inter, fontWeight = FontWeight.Medium),
    )
}

// Fallback for non-composable contexts (unused — appTypography() is the primary entry point)
internal val AppTypography = Typography()
