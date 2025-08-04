package com.uhc.workouttracker.workout.data

import kotlinx.serialization.Serializable

@Serializable
data class Exercises(
    val id: Long,
    val name: String,
    val description: String? = null,
)