package com.uhc.workouttracker.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weight_logs",
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class,
        parentColumns = ["id"],
        childColumns = ["exercise_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("exercise_id")]
)
data class WeightLogEntity(
    @PrimaryKey val id: Long,
    val weight: Float,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long
)
