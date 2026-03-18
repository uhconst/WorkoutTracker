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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "WorkoutsVM"

sealed class WorkoutsUiState {
    data object Loading : WorkoutsUiState()
    data class Success(val groups: List<MuscleWithExercises>) : WorkoutsUiState()
    data class Error(val message: String) : WorkoutsUiState()
    data object NotAuthenticated : WorkoutsUiState()
}

class WorkoutsViewModel(
    private val exerciseRepository: WearExerciseRepository,
    private val sessionRepository: WearSessionRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow<WorkoutsUiState>(WorkoutsUiState.Loading)
    val state: StateFlow<WorkoutsUiState> = _state

    init {
        loadData()
    }

    @OptIn(ExperimentalTime::class)
    fun loadData() {
        viewModelScope.launch {
            _state.value = WorkoutsUiState.Loading
            Log.d(TAG, "loadData: reading session from Data Layer")
            val tokens = sessionRepository.readSession()
            if (tokens == null) {
                Log.w(TAG, "loadData: no session tokens — showing NotAuthenticated")
                _state.value = WorkoutsUiState.NotAuthenticated
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
                    _state.value = WorkoutsUiState.Success(groups)
                },
                onFailure = { e ->
                    Log.e(TAG, "loadData: FAILED — ${e.message}", e)
                    _state.value = WorkoutsUiState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }
}
