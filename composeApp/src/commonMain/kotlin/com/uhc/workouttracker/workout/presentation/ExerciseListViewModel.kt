package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.authentication.domain.repository.AuthRepository
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseListViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val authRepository: AuthRepository,
    muscleGroupRepository: MuscleGroupRepository
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = muscleGroupRepository.observeMuscleGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _exercisesGroupedByMuscle: StateFlow<List<MuscleWithExercises>> =
        exerciseRepository.observeExercisesGroupedByMuscle()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedMuscleIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedMuscleIds = _selectedMuscleIds.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val filteredExercises: StateFlow<List<MuscleWithExercises>> = combine(
        _selectedMuscleIds,
        _exercisesGroupedByMuscle
    ) { selectedIds, exercises ->
        if (selectedIds.isEmpty()) {
            exercises
        } else {
            exercises.filter { it.id in selectedIds }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun fetchExercises() {
        viewModelScope.launch {
            _error.value = null
            runCatching { authRepository.refreshSession() }
            runCatching {
                exerciseRepository.getExercisesGroupedByMuscle()
            }.onFailure {
                if (_exercisesGroupedByMuscle.value.isEmpty()) {
                    _error.value = "Failed to load exercises. Please try again."
                }
            }
        }
    }

    fun selectMuscleFilter(muscleId: Long?) {
        if (muscleId == null) {
            _selectedMuscleIds.value = emptySet()
        } else {
            _selectedMuscleIds.value = _selectedMuscleIds.value.toMutableSet().apply {
                if (muscleId in this) {
                    remove(muscleId)
                } else {
                    add(muscleId)
                }
            }
        }
    }
}
