package com.uhc.workouttracker.authentication.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.flow.Flow

sealed interface AuthApi {

    suspend fun signIn(email: String, password: String)

    suspend fun signUp(email: String, password: String)

    suspend fun signInWithGoogle()

    suspend fun verifyOtp(email: String, otp: String)

    suspend fun signOut()

    suspend fun resetPassword(email: String)

    suspend fun changePassword(newPassword: String)

    fun sessionStatus(): Flow<SessionStatus>

}

internal class AuthApiImpl(
//    private val client: SupabaseClient
) : AuthApi {

    private val client by lazy {
        createSupabaseClient(
            "https://evblichpfnyvvboqhsht.supabase.co",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV2YmxpY2hwZm55dnZib3Foc2h0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzM4MzYzNDUsImV4cCI6MjA0OTQxMjM0NX0.9hGBigU1xpJnxH3HdAs3-0I8oq_83P7MfArRL73T62I"
        ) {
            install(Auth)
        }
    }

    private val auth by lazy { client.auth }

    override fun sessionStatus(): Flow<SessionStatus> {
        return auth.sessionStatus
    }

    override suspend fun verifyOtp(email: String, otp: String) {
        auth.verifyEmailOtp(OtpType.Email.EMAIL, email, otp)
    }

    override suspend fun signInWithGoogle() {
        auth.signInWith(Google)
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String) {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun changePassword(newPassword: String) {
        auth.updateUser {
            this.password = newPassword
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String) {
        auth.resetPasswordForEmail(email)
    }


}