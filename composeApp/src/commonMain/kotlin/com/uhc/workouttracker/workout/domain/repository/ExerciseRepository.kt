package com.uhc.workouttracker.workout.domain.repository

import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeExercisesGroupedByMuscle(): Flow<List<MuscleWithExercises>>
    suspend fun getExercisesGroupedByMuscle(): List<MuscleWithExercises>
    suspend fun getExerciseById(id: Long): Exercise?
    suspend fun saveExercise(name: String, muscleGroupId: Long, weight: Double)
    suspend fun updateExercise(id: Long, name: String, muscleGroupId: Long, weight: Double)
    suspend fun deleteExercise(id: Long)
}
