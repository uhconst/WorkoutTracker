package com.uhc.workouttracker.workout.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeightLogDto(
    val id: Long = -1,
    val weight: Float,
    @SerialName("exercise_id")
    val exerciseId: Long = -1
)
