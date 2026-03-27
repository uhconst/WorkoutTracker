package com.uhc.workouttracker.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uhc.workouttracker.core.db.dao.ExerciseDao
import com.uhc.workouttracker.core.db.dao.MuscleGroupDao
import com.uhc.workouttracker.core.db.entity.ExerciseEntity
import com.uhc.workouttracker.core.db.entity.MuscleGroupEntity
import com.uhc.workouttracker.core.db.entity.WeightLogEntity

@Database(
    entities = [MuscleGroupEntity::class, ExerciseEntity::class, WeightLogEntity::class],
    version = 1,
    exportSchema = true
)
abstract class WorkoutTrackerDatabase : RoomDatabase() {
    abstract fun muscleGroupDao(): MuscleGroupDao
    abstract fun exerciseDao(): ExerciseDao
}
