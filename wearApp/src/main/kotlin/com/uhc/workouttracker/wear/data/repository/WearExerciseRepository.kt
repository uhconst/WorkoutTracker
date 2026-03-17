package com.uhc.workouttracker.wear.data.repository

import com.uhc.workouttracker.wear.domain.model.MuscleWithExercises

interface WearExerciseRepository {
    suspend fun getExercisesGroupedByMuscle(): List<MuscleWithExercises>
}
