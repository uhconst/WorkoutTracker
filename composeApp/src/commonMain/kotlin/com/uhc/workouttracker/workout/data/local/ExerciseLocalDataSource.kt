package com.uhc.workouttracker.workout.data.local

import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import kotlinx.coroutines.flow.Flow

interface ExerciseLocalDataSource {
    fun observeGroupedByMuscle(): Flow<List<MuscleWithExercises>>
    suspend fun getById(id: Long): Exercise?
    suspend fun deleteById(id: Long)
    suspend fun replaceAll(groups: List<MuscleWithExercises>)
}
