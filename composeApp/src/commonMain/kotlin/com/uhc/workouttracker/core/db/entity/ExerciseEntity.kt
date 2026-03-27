package com.uhc.workouttracker.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [ForeignKey(
        entity = MuscleGroupEntity::class,
        parentColumns = ["id"],
        childColumns = ["muscle_group_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("muscle_group_id")]
)
data class ExerciseEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String?,
    @ColumnInfo(name = "muscle_group_id") val muscleGroupId: Long?
)
