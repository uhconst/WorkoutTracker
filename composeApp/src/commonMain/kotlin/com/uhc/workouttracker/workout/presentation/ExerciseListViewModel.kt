package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import com.uhc.workouttracker.workout.domain.GetExercisesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseListViewModel(
    private val getExercisesUseCase: GetExercisesUseCase
) : ViewModel() {

    private val _exercisesGroupedByMuscle = MutableStateFlow<List<MuscleGroupsWithExercises>>(emptyList())
    val exercisesGroupedByMuscle: StateFlow<List<MuscleGroupsWithExercises>> = _exercisesGroupedByMuscle


    fun fetchExercises() {
        viewModelScope.launch {
            _exercisesGroupedByMuscle.value = getExercisesUseCase.invoke2()
        }
    }
}