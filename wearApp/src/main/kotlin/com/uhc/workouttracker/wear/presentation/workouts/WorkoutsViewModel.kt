package com.uhc.workouttracker.wear.presentation.workouts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.wear.data.repository.WearExerciseRepository
import com.uhc.workouttracker.wear.data.repository.WearSessionRepository
import com.uhc.workouttracker.wear.domain.model.MuscleWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "WorkoutsVM"

sealed class WorkoutsUiState {
    data object Loading : WorkoutsUiState()
    data class Success(
        val allGroups: List<MuscleWithExercises>,
        val displayedGroups: List<MuscleWithExercises>
    ) : WorkoutsUiState()
    data class Error(val message: String) : WorkoutsUiState()
    data object NotAuthenticated : WorkoutsUiState()
}

class WorkoutsViewModel(
    private val exerciseRepository: WearExerciseRepository,
    private val sessionRepository: WearSessionRepository
) : ViewModel() {

    private val _baseState = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    private val _selectedMuscleIds = MutableStateFlow<Set<Long>>(emptySet())

    val state: StateFlow<WorkoutsUiState> = combine(_baseState, _selectedMuscleIds) { base, selected ->
        if (base is WorkoutsUiState.Success) {
            base.copy(
                displayedGroups = if (selected.isEmpty()) base.allGroups
                                  else base.allGroups.filter { it.id in selected }
            )
        } else base
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WorkoutsUiState.Loading)

    val selectedMuscleIds: StateFlow<Set<Long>> = _selectedMuscleIds

    init {
        loadData()
    }

    fun toggleFilter(muscleId: Long?) {
        _selectedMuscleIds.update { current ->
            if (muscleId == null) emptySet()
            else if (muscleId in current) current - muscleId
            else current + muscleId
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _baseState.value = WorkoutsUiState.Loading
            Log.d(TAG, "loadData: importing session")
            val sessionReady = sessionRepository.importAndValidateSession()
            if (!sessionReady) {
                Log.w(TAG, "loadData: session not available — showing NotAuthenticated")
                _baseState.value = WorkoutsUiState.NotAuthenticated
                return@launch
            }
            Log.d(TAG, "loadData: session ready, fetching exercises")
            runCatching {
                exerciseRepository.getExercisesGroupedByMuscle()
            }.fold(
                onSuccess = { groups ->
                    Log.d(TAG, "loadData: success — ${groups.size} muscle groups")
                    _baseState.value = WorkoutsUiState.Success(
                        allGroups = groups,
                        displayedGroups = groups
                    )
                },
                onFailure = { e ->
                    Log.e(TAG, "loadData: FAILED — ${e.message}", e)
                    _baseState.value = WorkoutsUiState.Error("Failed to load workouts. Please try again.")
                }
            )
        }
    }
}
