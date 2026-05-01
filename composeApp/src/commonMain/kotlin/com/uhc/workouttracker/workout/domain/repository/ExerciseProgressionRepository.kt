package com.uhc.workouttracker.workout.domain.repository

import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import kotlinx.coroutines.flow.Flow

interface ExerciseProgressionRepository {
    fun observeAll(): Flow<Map<Long, ProgressionReadiness>>
    suspend fun update(exerciseId: Long, readiness: ProgressionReadiness)
    suspend fun reset(exerciseId: Long)
}
