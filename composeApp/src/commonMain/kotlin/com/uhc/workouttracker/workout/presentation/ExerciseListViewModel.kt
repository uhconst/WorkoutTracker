package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.data.MuscleGroup
import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import com.uhc.workouttracker.workout.data.Exercise
import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import com.uhc.workouttracker.workout.domain.GetExercisesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Shared data store for exercise editing
object ExerciseEditStore {
    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise = _selectedExercise.asStateFlow()
    
    fun setExercise(exercise: Exercise?) {
        _selectedExercise.value = exercise
    }
}

class ExerciseListViewModel(
    private val getExercisesUseCase: GetExercisesUseCase,
    getMuscleGroupsUseCase: GetMuscleGroupsUseCase
) : ViewModel() {

    val muscles: StateFlow<List<MuscleGroup>> = getMuscleGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _exercisesGroupedByMuscle =
        MutableStateFlow<List<MuscleGroupsWithExercises>>(emptyList())

    // Selected muscle filters state - using a Set to store multiple selections
    private val _selectedMuscleIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedMuscleIds = _selectedMuscleIds.asStateFlow()

    fun selectExerciseForEdit(exercise: Exercise) {
        ExerciseEditStore.setExercise(exercise)
    }

    // Filtered exercises based on selected muscles
    val filteredExercises: StateFlow<List<MuscleGroupsWithExercises>> = combine(
        _selectedMuscleIds,
        _exercisesGroupedByMuscle
    ) { selectedIds, exercises ->
        if (selectedIds.isEmpty()) {
            exercises
        } else {
            exercises.filter { it.id in selectedIds }
        }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchExercises() {
        viewModelScope.launch {
            _exercisesGroupedByMuscle.value = getExercisesUseCase()
        }
    }

    fun selectMuscleFilter(muscleId: Long?) {
        if (muscleId == null) {
            // Clear all selections when "All" is clicked
            _selectedMuscleIds.value = emptySet()
        } else {
            // Toggle the selection: add if not present, remove if already selected
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