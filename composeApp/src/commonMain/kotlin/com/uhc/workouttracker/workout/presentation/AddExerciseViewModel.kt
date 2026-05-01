package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.repository.ExerciseProgressionRepository
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.CoroutineScope
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
    private val exerciseRepository: ExerciseRepository,
    private val progressionRepository: ExerciseProgressionRepository,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = muscleGroupRepository.observeMuscleGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingExercise = MutableStateFlow<Exercise?>(null)
    val editingExercise = _editingExercise.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<String>()
    val saveSuccess = _saveSuccess.asSharedFlow()

    private val _saveError = MutableSharedFlow<String>()
    val saveError = _saveError.asSharedFlow()

    private val _navigateBack = MutableSharedFlow<Unit>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val _navigateToList = MutableSharedFlow<Unit>()
    val navigateToList = _navigateToList.asSharedFlow()

    fun saveExercise(name: String, muscleGroupId: Long, weight: Double) {
        val editing = _editingExercise.value
        if (editing == null) {
            viewModelScope.launch {
                runCatching {
                    exerciseRepository.saveExercise(
                        name = name,
                        muscleGroupId = muscleGroupId,
                        weight = weight
                    )
                }.onSuccess {
                    _saveSuccess.emit("Exercise added")
                }.onFailure {
                    _saveError.emit("Failed to save exercise. Please try again.")
                }
            }
        } else {
            // Navigate back immediately; save runs in applicationScope so it survives ViewModel clearing.
            viewModelScope.launch { _navigateBack.emit(Unit) }
            applicationScope.launch {
                runCatching {
                    exerciseRepository.updateExercise(
                        id = editing.id,
                        name = name,
                        muscleGroupId = muscleGroupId,
                        weight = weight
                    )
                    progressionRepository.reset(editing.id)
                }
            }
        }
    }

    fun setExerciseToEdit(exerciseId: Long) {
        viewModelScope.launch {
            _editingExercise.value = exerciseRepository.getExerciseById(exerciseId)
        }
    }

    fun deleteExercise() {
        val id = _editingExercise.value?.id ?: return
        viewModelScope.launch {
            runCatching {
                exerciseRepository.deleteExercise(id)
            }.onSuccess {
                _navigateToList.emit(Unit)
            }.onFailure {
                _saveError.emit("Failed to delete exercise. Please try again.")
            }
        }
    }
}
