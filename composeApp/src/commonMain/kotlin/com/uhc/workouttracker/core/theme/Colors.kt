package com.uhc.workouttracker.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val color_red288_100a = Color(0xFFF14848)
private val color_black = Color(0xFF000000)
private val color_white = Color(0xFFFFFFFF)
private val color_sky362_100a = Color(0xFF30A9FF)
private val color_blue10_100a = Color(0xFF181B26)
private val color_blue907_100a = Color(0xFFEEF5FF)
private val color_indigo32_100a = Color(0xFF2F3141)
private val color_indigo900_100a = Color(0xFFDDE4FA)

internal val DarkColorScheme = darkColorScheme(
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

internal val LightColorScheme = lightColorScheme(
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