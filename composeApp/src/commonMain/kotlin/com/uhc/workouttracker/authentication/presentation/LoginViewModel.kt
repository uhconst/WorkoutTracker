package com.uhc.workouttracker.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.authentication.domain.model.AuthState
import com.uhc.workouttracker.authentication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val sessionStatus = authRepository.sessionStatus()
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Unauthenticated)

    private val _alert = MutableStateFlow<String?>(null)
    val alert = _alert.asStateFlow()

    private val _passwordReset = MutableStateFlow<Boolean>(false)
    val passwordReset = _passwordReset.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                authRepository.signUp(email, password)
            }.onSuccess {
                _alert.value = "Successfully registered! Check your E-Mail to verify your account."
            }.onFailure {
                _alert.value = "There was an error while registering. Please try again."
            }
            _isLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                authRepository.signIn(email, password)
            }.onFailure {
                it.printStackTrace()
                _alert.value = "There was an error while logging in. Check your credentials and try again."
            }
            _isLoading.value = false
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            runCatching {
                authRepository.signInWithGoogle()
            }.onFailure {
                _alert.value = "Google sign-in failed. Please try again."
            }
        }
    }

    fun loginWithOTP(email: String, code: String, reset: Boolean) {
        viewModelScope.launch {
            runCatching {
                authRepository.verifyOtp(email, code)
            }.onSuccess {
                _passwordReset.value = reset
            }.onFailure {
                _alert.value = "Invalid or expired code. Please try again."
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.resetPassword(email)
            }.onSuccess {
                _alert.value = "Password reset email sent. Check your inbox."
            }.onFailure {
                _alert.value = "Failed to send password reset email. Please try again."
            }
        }
    }

    fun changePassword(password: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.changePassword(password)
            }.onSuccess {
                _alert.value = "Password changed successfully!"
            }.onFailure {
                _alert.value = "There was an error while changing the password. Please try again."
            }
        }
    }

    fun clearAlert() {
        _alert.value = null
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.signOut()
            }.onFailure {
                _alert.value = "Sign out failed. Please try again."
            }
        }
    }
}
