package com.uhc.workouttracker.wear.domain.model

data class MuscleWithExercises(
    val id: Long,
    val muscleName: String,
    val exercises: List<Exercise> = emptyList()
)
