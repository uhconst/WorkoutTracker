package com.uhc.workouttracker.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = color_sky362_100a,
    onPrimary = color_blue10_100a,
    error = color_red288_100a,
    onError = color_black,
    background = color_black,
    onBackground = color_white,
    surface = color_indigo32_100a,
    onSurface = color_blue907_100a,
    surfaceContainerHighest = color_indigo32_100a,
    primaryContainer = color_blue907_100a,
    onPrimaryContainer = color_blue10_100a,
    surfaceContainer = color_blue10_100a,
    secondaryContainer = color_blue907_100a,
    onSecondaryContainer = color_blue10_100a
)

private val LightColorScheme = lightColorScheme(
    primary = color_sky362_100a,
    onPrimary = color_white,
    error = color_red288_100a,
    onError = color_white,
    background = color_white,
    onBackground = color_black,
    surface = color_indigo900_100a,
    onSurface = color_indigo32_100a,
    surfaceContainerHighest = color_indigo900_100a,
    primaryContainer = color_blue10_100a,
    onPrimaryContainer = color_blue907_100a,
    surfaceContainer = color_blue907_100a,
    secondaryContainer = color_blue10_100a,
    onSecondaryContainer = color_blue907_100a
)

@Composable
fun WorkoutTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        },
        content = content
    )
}
