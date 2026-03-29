package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.workout.domain.model.WeightLog
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseProgressionGraphViewModel(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _weightLogs = MutableStateFlow<List<WeightLog>>(emptyList())
    val weightLogs: StateFlow<List<WeightLog>> = _weightLogs.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadWeightLogs(exerciseId: Long) {
        viewModelScope.launch {
            runCatching {
                exerciseRepository.getExerciseById(exerciseId)
            }.onSuccess { exercise ->
                _weightLogs.value = exercise?.weightLogs?.sortedBy { it.date } ?: emptyList()
            }.onFailure {
                if (_weightLogs.value.isEmpty()) {
                    _error.value = "Failed to load progression data."
                }
            }
        }
    }
}
