package com.uhc.workouttracker.authentication.domain.model

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated : AuthState()
    data object Loading : AuthState()
}
