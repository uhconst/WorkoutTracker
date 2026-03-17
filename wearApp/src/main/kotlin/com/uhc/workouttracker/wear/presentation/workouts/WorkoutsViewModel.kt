package com.uhc.workouttracker.wear.presentation.workouts

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
            val tokens = sessionRepository.readSession()
            if (tokens == null) {
                _state.value = WorkoutsUiState.NotAuthenticated
                return@launch
            }
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
                exerciseRepository.getExercisesGroupedByMuscle()
            }.fold(
                onSuccess = { groups -> _state.value = WorkoutsUiState.Success(groups) },
                onFailure = { e -> _state.value = WorkoutsUiState.Error(e.message ?: "Unknown error") }
            )
        }
    }
}
