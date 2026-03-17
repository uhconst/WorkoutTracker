package com.uhc.workouttracker.wear.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeightLogDto(
    val id: Long = -1,
    val weight: Float
)
