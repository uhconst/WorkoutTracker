package com.uhc.workouttracker.muscle.domain.repository

import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

interface MuscleGroupRepository {
    fun observeMuscleGroups(): Flow<List<MuscleGroup>>
    suspend fun addMuscleGroup(name: String)
    suspend fun updateMuscleGroup(muscleGroup: MuscleGroup)
    suspend fun deleteMuscleGroup(id: Long)
}
