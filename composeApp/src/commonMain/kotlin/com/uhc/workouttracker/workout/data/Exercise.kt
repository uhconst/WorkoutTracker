package com.uhc.workouttracker.workout.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null,
    @SerialName("weight_logs")
    val weightLogs: List<WeightLogs>? = null
)