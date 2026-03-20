package com.uhc.workouttracker.wear.presentation.workouts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.wear.data.repository.WearExerciseRepository
import com.uhc.workouttracker.wear.data.repository.WearSessionRepository
import com.uhc.workouttracker.wear.domain.model.MuscleWithExercises
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import kotlin.time.ExperimentalTime
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
    private val sessionRepository: WearSessionRepository,
    private val supabase: SupabaseClient
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

    @OptIn(ExperimentalTime::class)
    fun loadData() {
        viewModelScope.launch {
            _baseState.value = WorkoutsUiState.Loading
            Log.d(TAG, "loadData: reading session from Data Layer")
            val tokens = sessionRepository.readSession()
            if (tokens == null) {
                Log.w(TAG, "loadData: no session tokens — showing NotAuthenticated")
                _baseState.value = WorkoutsUiState.NotAuthenticated
                return@launch
            }
            Log.d(TAG, "loadData: importing session into Supabase client")
            runCatching {
                supabase.auth.importSession(
                    UserSession(
                        accessToken = tokens.first,
                        refreshToken = tokens.second,
                        tokenType = "bearer",
                        expiresIn = 3600L
                    ),
                    autoRefresh = true
                )
                Log.d(TAG, "loadData: session imported, fetching exercises")
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
                    _baseState.value = WorkoutsUiState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }
}
