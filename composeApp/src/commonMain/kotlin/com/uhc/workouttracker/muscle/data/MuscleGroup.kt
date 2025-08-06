package com.uhc.workouttracker.muscle.data

import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroup(
    val id: Long = -1,
    val name: String
)