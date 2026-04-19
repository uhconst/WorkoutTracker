package com.uhc.workouttracker.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import com.uhc.workouttracker.workout.domain.repository.ExerciseProgressionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgressionReadinessViewModel(
    private val progressionRepository: ExerciseProgressionRepository
) : ViewModel() {

    private val _exerciseId = MutableStateFlow(-1L)

    val current = combine(
        _exerciseId,
        progressionRepository.observeAll()
    ) { id, progressions ->
        progressions[id] ?: ProgressionReadiness.ON_TRACK
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressionReadiness.ON_TRACK)

    private val _saved = MutableSharedFlow<Unit>()
    val saved = _saved.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun load(exerciseId: Long) {
        _exerciseId.value = exerciseId
    }

    fun select(readiness: ProgressionReadiness) {
        viewModelScope.launch {
            runCatching {
                progressionRepository.update(_exerciseId.value, readiness)
            }.onSuccess {
                _saved.emit(Unit)
            }.onFailure {
                _error.emit("Failed to update. Please try again.")
            }
        }
    }
}
