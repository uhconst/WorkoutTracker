package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddExerciseViewModel(
    muscleGroupRepository: MuscleGroupRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = muscleGroupRepository.observeMuscleGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingExercise = MutableStateFlow<Exercise?>(null)
    val editingExercise = _editingExercise.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<String>()
    val saveSuccess = _saveSuccess.asSharedFlow()

    fun saveExercise(name: String, muscleGroupId: Long, weight: Double) {
        viewModelScope.launch {
            if (_editingExercise.value == null) {
                exerciseRepository.saveExercise(
                    name = name,
                    muscleGroupId = muscleGroupId,
                    weight = weight
                )
            } else {
                exerciseRepository.updateExercise(
                    id = _editingExercise.value!!.id,
                    name = name,
                    muscleGroupId = muscleGroupId,
                    weight = weight
                )
            }
            _saveSuccess.emit("Exercise saved")
        }
    }

    fun setExerciseToEdit(exerciseId: Long) {
        viewModelScope.launch {
            _editingExercise.value = exerciseRepository.getExerciseById(exerciseId)
        }
    }
}
