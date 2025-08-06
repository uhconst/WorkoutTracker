package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import io.github.jan.supabase.annotations.SupabaseExperimental
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

// todo delete
class GetWorkoutWithMusclesUseCase(
    private val getExercisesUseCase: GetExercisesUseCase,
    private val getMuscleGroupsUseCase: GetMuscleGroupsUseCase,
    private val getWeightLogsUseCase: GetWeightLogsUseCase
) {
    @OptIn(SupabaseExperimental::class)
    operator fun invoke(): Flow<List<MuscleGroupsWithExercises>> =
        combine(
            getExercisesUseCase(),
            getMuscleGroupsUseCase(),
            getWeightLogsUseCase()
        ) { exercises, muscles, weightLogs ->
            muscles.map { muscleGroup ->
                MuscleGroupsWithExercises(
                    id = muscleGroup.id,
                    muscleName = muscleGroup.name,
                    exercises = exercises.filter { exercise ->
                        exercise.muscleGroupsId == muscleGroup.id
                    }
                )
            }
        }
}