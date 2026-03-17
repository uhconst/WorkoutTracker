package com.uhc.workouttracker.workout.domain.model

data class MuscleWithExercises(val id: Long, val muscleName: String, val exercises: List<Exercise>)
