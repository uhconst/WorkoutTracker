package com.uhc.workouttracker.workout.domain.model

data class WeightLog(val id: Long, val weight: Float, val exerciseId: Long, val date: String = "")
