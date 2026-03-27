package com.uhc.workouttracker.core.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.uhc.workouttracker.core.db.entity.ExerciseEntity
import com.uhc.workouttracker.core.db.entity.WeightLogEntity

data class ExerciseWithWeightLogs(
    @Embedded val exercise: ExerciseEntity,
    @Relation(parentColumn = "id", entityColumn = "exercise_id")
    val weightLogs: List<WeightLogEntity>
)
