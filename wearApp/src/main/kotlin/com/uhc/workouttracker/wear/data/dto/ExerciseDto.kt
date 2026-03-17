package com.uhc.workouttracker.wear.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    val id: Long,
    val name: String,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null,
    @SerialName("weight_logs")
    val weightLogs: List<WeightLogDto>? = null
)
