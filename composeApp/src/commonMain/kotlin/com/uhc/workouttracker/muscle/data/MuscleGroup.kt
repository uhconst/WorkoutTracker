package com.uhc.workouttracker.muscle.data

import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroup(
    val id: Long,
    val name: String
)