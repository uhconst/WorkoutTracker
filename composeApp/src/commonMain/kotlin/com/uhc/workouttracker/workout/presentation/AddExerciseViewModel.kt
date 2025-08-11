package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.data.MuscleGroup
import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import com.uhc.workouttracker.workout.data.Exercise
import com.uhc.workouttracker.workout.data.WeightLogs
import com.uhc.workouttracker.workout.domain.SetExerciseUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddExerciseViewModel(
    getMuscleGroupsUseCase: GetMuscleGroupsUseCase,
    private val setExerciseUseCase: SetExerciseUseCase
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = getMuscleGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveExercise(name: String, muscleGroupId: Long, weight: Double) {
        viewModelScope.launch {
            val exercise = Exercise(
                id = 0,
                name = name,
                muscleGroupsId = muscleGroupId,
                weightLogs = listOf(
                    WeightLogs(
                        id = 0,
                        weight = weight.toFloat()
                    )
                )
            )

            setExerciseUseCase(exercise)
        }
    }
}