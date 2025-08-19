package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.data.MuscleGroup
import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import com.uhc.workouttracker.workout.data.Exercise
import com.uhc.workouttracker.workout.data.WeightLogs
import com.uhc.workouttracker.workout.domain.GetExerciseByIdUseCase
import com.uhc.workouttracker.workout.domain.SetExerciseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddExerciseViewModel(
    getMuscleGroupsUseCase: GetMuscleGroupsUseCase,
    private val setExerciseUseCase: SetExerciseUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = getMuscleGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingExercise =
        MutableStateFlow<Exercise?>(null)
    val editingExercise = _editingExercise.asStateFlow()

    init {
        // Clear the selected exercise when the ViewModel is destroyed
        viewModelScope.launch {
            // This will be called when the ViewModel is cleared
            onCleared()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // Clear the selected exercise when navigating away
        ExerciseEditStore.setExercise(null)
    }

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

    fun setExerciseToEdit(exerciseId: Long) {
        viewModelScope.launch {
            _editingExercise.value = getExerciseByIdUseCase(exerciseId)
        }
    }
}