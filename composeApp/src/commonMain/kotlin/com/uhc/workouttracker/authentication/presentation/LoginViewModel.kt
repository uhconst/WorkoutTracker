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

    val alert = MutableStateFlow<String?>(null)
    val passwordReset = MutableStateFlow<Boolean>(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                authRepository.signUp(email, password)
            }.onSuccess {
                alert.value = "Successfully registered! Check your E-Mail to verify your account."
            }.onFailure {
                alert.value = "There was an error while registering: ${it.message}"
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
                alert.value = "There was an error while logging in. Check your credentials and try again."
            }
            _isLoading.value = false
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            runCatching {
                authRepository.signInWithGoogle()
            }
        }
    }

    fun loginWithOTP(email: String, code: String, reset: Boolean) {
        viewModelScope.launch {
            runCatching {
                authRepository.verifyOtp(email, code)
            }.onSuccess {
                passwordReset.value = reset
            }.onFailure {
                alert.value = "There was an error while verifying the OTP: ${it.message}"
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.resetPassword(email)
            }
        }
    }

    fun changePassword(password: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.changePassword(password)
            }.onSuccess {
                alert.value = "Password changed successfully!"
            }.onFailure {
                alert.value = "There was an error while changing the password: ${it.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.signOut()
            }
        }
    }
}
