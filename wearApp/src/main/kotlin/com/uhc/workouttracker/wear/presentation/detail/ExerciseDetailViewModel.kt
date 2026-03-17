package com.uhc.workouttracker.wear.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.wear.data.repository.WearExerciseRepository
import com.uhc.workouttracker.wear.domain.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ExerciseDetailUiState {
    data object Loading : ExerciseDetailUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseDetailUiState()
    data class Error(val message: String) : ExerciseDetailUiState()
}

class ExerciseDetailViewModel(
    private val exerciseRepository: WearExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val muscleId: Long = savedStateHandle.get<String>("muscleId")?.toLong() ?: -1L

    private val _state = MutableStateFlow<ExerciseDetailUiState>(ExerciseDetailUiState.Loading)
    val state: StateFlow<ExerciseDetailUiState> = _state

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            runCatching {
                exerciseRepository.getExercisesGroupedByMuscle()
                    .find { it.id == muscleId }
                    ?.exercises ?: emptyList()
            }.fold(
                onSuccess = { exercises -> _state.value = ExerciseDetailUiState.Success(exercises) },
                onFailure = { e -> _state.value = ExerciseDetailUiState.Error(e.message ?: "Unknown error") }
            )
        }
    }
}
