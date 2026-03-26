package com.uhc.workouttracker.authentication.domain.repository

import com.uhc.workouttracker.authentication.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signInWithGoogle()
    suspend fun verifyOtp(email: String, otp: String)
    suspend fun signOut()
    suspend fun resetPassword(email: String)
    suspend fun changePassword(newPassword: String)
    suspend fun refreshSession()
    fun sessionStatus(): Flow<AuthState>
}
