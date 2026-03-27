package com.uhc.workouttracker.core.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.uhc.workouttracker.core.db.entity.ExerciseEntity
import com.uhc.workouttracker.core.db.entity.MuscleGroupEntity

data class MuscleGroupWithExercises(
    @Embedded val muscleGroup: MuscleGroupEntity,
    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "muscle_group_id"
    )
    val exercises: List<ExerciseWithWeightLogs>
)
