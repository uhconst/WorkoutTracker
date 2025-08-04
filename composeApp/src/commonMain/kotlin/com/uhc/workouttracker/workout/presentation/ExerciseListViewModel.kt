package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.workout.data.Exercises
import com.uhc.workouttracker.workout.domain.GetWorkoutsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ExerciseListViewModel(
    getWorkoutsUseCase: GetWorkoutsUseCase
) : ViewModel() {
    val exercises: StateFlow<List<Exercises>> = getWorkoutsUseCase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}