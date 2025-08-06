package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.workout.data.Exercise
import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import com.uhc.workouttracker.workout.domain.GetExercisesUseCase
import com.uhc.workouttracker.workout.domain.GetWorkoutWithMusclesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseListViewModel(
    private val getExercisesUseCase: GetExercisesUseCase,
    getWorkoutWithMusclesUseCase: GetWorkoutWithMusclesUseCase,
) : ViewModel() {
/*
    val exercises: StateFlow<List<Exercise>> = getExercisesUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
*/

/*    val exercises: StateFlow<List<MuscleGroupsWithExercises>> = getExercisesUseCase.invoke2()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())*/
/*

    val exercisesGroupedByMuscle: StateFlow<List<MuscleGroupsWithExercises>> = getWorkoutWithMusclesUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
*/

    private val _exercisesGroupedByMuscle = MutableStateFlow<List<MuscleGroupsWithExercises>>(emptyList())
    val exercisesGroupedByMuscle: StateFlow<List<MuscleGroupsWithExercises>> = _exercisesGroupedByMuscle


    fun fetchExercises() {
        viewModelScope.launch {
            _exercisesGroupedByMuscle.value = getExercisesUseCase.invoke2()
        }
    }
}