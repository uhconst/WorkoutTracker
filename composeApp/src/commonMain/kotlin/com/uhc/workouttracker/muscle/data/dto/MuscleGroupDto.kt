package com.uhc.workouttracker.muscle.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroupDto(
    val id: Long = -1,
    val name: String
)
