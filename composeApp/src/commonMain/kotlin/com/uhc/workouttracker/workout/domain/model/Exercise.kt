package com.uhc.workouttracker.workout.domain.model

data class Exercise(
    val id: Long,
    val name: String,
    val description: String? = null,
    val muscleGroupId: Long? = null,
    val weightLogs: List<WeightLog> = emptyList()
)
