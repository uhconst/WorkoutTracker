package com.uhc.workouttracker.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muscle_groups")
data class MuscleGroupEntity(
    @PrimaryKey val id: Long,
    val name: String
)
