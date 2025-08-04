package com.uhc.workouttracker.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uhc.workouttracker.authentication.data.AuthApi
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
//    val supabaseClient: SupabaseClient,
//    private val messageApi: MessageApi,
    private val authApi: AuthApi
) : ViewModel() {

    val sessionStatus = authApi.sessionStatus()
        .stateIn(viewModelScope, SharingStarted.Eagerly, SessionStatus.NotAuthenticated())

    //    val sessionStatus = authApi.sessionStatus().stateIn(coroutineScope, SharingStarted.Eagerly, SessionStatus.NotAuthenticated(false))
    val alert = MutableStateFlow<String?>(null)
//    val messages = MutableStateFlow<List<Message>>(emptyList())
    val passwordReset = MutableStateFlow<Boolean>(false)

    //Auth

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.signUp(email, password)
            }.onSuccess {
                alert.value = "Successfully registered! Check your E-Mail to verify your account."
            }.onFailure {
                alert.value = "There was an error while registering: ${it.message}"
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.signIn(email, password)
            }.onFailure {
                it.printStackTrace()
                alert.value = "There was an error while logging in. Check your credentials and try again."
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.signInWithGoogle()
            }
        }
    }

    fun loginWithOTP(email: String, code: String, reset: Boolean) {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.verifyOtp(email, code)
            }.onSuccess {
                passwordReset.value = reset
            }.onFailure {
                alert.value = "There was an error while verifying the OTP: ${it.message}"
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.resetPassword(email)
            }
        }
    }

    fun changePassword(password: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.changePassword(password)
            }.onSuccess {
                alert.value = "Password changed successfully!"
            }.onFailure {
                alert.value = "There was an error while changing the password: ${it.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            kotlin.runCatching {
                authApi.signOut()
//                messages.value = emptyList()
            }
        }
    }

}