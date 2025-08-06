package com.uhc.workouttracker.workout.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroupsWithExercises (
    val id: Long,
    @SerialName("name")
    val muscleName: String,
    val exercises: List<Exercise>? = null
)