package com.uhc.workouttracker.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_progressions")
data class ExerciseProgressionEntity(
    @PrimaryKey @ColumnInfo(name = "exercise_id") val exerciseId: Long,
    val readiness: String = "ON_TRACK"
)
