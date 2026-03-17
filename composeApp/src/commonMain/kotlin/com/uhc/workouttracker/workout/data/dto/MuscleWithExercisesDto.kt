package com.uhc.workouttracker.workout.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MuscleWithExercisesDto(
    val id: Long,
    @SerialName("name")
    val muscleName: String,
    val exercises: List<ExerciseDto>? = null
)
