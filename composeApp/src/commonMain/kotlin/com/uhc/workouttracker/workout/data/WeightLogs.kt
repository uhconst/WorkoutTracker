package com.uhc.workouttracker.workout.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeightLogs(
    val id: Long = -1,
    val weight: Float,
//    @SerialName("exercise_Id")
//    val exerciseId: Long,
)