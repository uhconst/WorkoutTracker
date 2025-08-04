package com.uhc.workouttracker.muscle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.muscle.data.MuscleGroup
import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MuscleGroupsViewModel(
    getMuscleGroupsUseCase: GetMuscleGroupsUseCase
) : ViewModel() {
    val muscles: StateFlow<List<MuscleGroup>> = getMuscleGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}