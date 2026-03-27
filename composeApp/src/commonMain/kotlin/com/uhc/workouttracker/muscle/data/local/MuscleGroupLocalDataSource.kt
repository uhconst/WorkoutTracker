package com.uhc.workouttracker.muscle.data.local

import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

interface MuscleGroupLocalDataSource {
    fun observeAll(): Flow<List<MuscleGroup>>
    suspend fun replaceAll(groups: List<MuscleGroup>)
    suspend fun upsert(group: MuscleGroup)
    suspend fun delete(id: Long)
}
