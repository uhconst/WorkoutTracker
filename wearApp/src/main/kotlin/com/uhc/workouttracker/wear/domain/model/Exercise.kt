package com.uhc.workouttracker.wear.domain.model

data class Exercise(
    val id: Long,
    val name: String,
    val muscleGroupId: Long? = null,
    val weightLogs: List<WeightLog> = emptyList()
)
