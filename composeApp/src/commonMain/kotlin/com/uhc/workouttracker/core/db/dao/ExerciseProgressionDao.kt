package com.uhc.workouttracker.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.uhc.workouttracker.core.db.entity.ExerciseProgressionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseProgressionDao {
    @Query("SELECT * FROM exercise_progressions")
    fun observeAll(): Flow<List<ExerciseProgressionEntity>>

    @Upsert
    suspend fun upsert(entity: ExerciseProgressionEntity)

    @Query("DELETE FROM exercise_progressions WHERE exercise_id = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: Long)

    @Query("UPDATE exercise_progressions SET readiness = 'ON_TRACK' WHERE exercise_id = :exerciseId")
    suspend fun resetToOnTrack(exerciseId: Long)
}
