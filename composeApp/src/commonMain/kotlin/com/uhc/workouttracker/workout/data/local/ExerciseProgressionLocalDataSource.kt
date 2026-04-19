package com.uhc.workouttracker.workout.data.local

import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import kotlinx.coroutines.flow.Flow

interface ExerciseProgressionLocalDataSource {
    fun observeAll(): Flow<Map<Long, ProgressionReadiness>>
    suspend fun upsert(exerciseId: Long, readiness: ProgressionReadiness)
    suspend fun reset(exerciseId: Long)
}
