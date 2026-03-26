package com.uhc.workouttracker.authentication.presentation

import com.uhc.workouttracker.authentication.domain.model.AuthState
import com.uhc.workouttracker.authentication.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private val repo = mockk<AuthRepository>(relaxed = true)
    private lateinit var vm: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repo.sessionStatus() } returns flowOf(AuthState.Unauthenticated)
        vm = LoginViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sessionStatus initial value is Unauthenticated`() {
        assertEquals(AuthState.Unauthenticated, vm.sessionStatus.value)
    }

    @Test
    fun `sessionStatus reflects Authenticated`() {
        every { repo.sessionStatus() } returns flowOf(AuthState.Authenticated)
        val vm2 = LoginViewModel(repo)
        assertEquals(AuthState.Authenticated, vm2.sessionStatus.value)
    }

    @Test
    fun `login success - no alert set`() {
        coEvery { repo.signIn(any(), any()) } just Runs
        vm.login("user@test.com", "password")
        assertNull(vm.alert.value)
    }

    @Test
    fun `login failure - alert set`() {
        coEvery { repo.signIn(any(), any()) } throws Exception("bad creds")
        vm.login("user@test.com", "wrong")
        assertTrue(vm.alert.value!!.contains("error while logging in"))
    }

    @Test
    fun `login - isLoading resets to false after success`() {
        coEvery { repo.signIn(any(), any()) } just Runs
        vm.login("user@test.com", "password")
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun `login - isLoading resets to false after failure`() {
        coEvery { repo.signIn(any(), any()) } throws Exception("bad creds")
        vm.login("user@test.com", "wrong")
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun `signUp success - registration alert`() {
        coEvery { repo.signUp(any(), any()) } just Runs
        vm.signUp("user@test.com", "password")
        assertTrue(vm.alert.value!!.contains("Successfully registered"))
    }

    @Test
    fun `signUp failure - error alert`() {
        coEvery { repo.signUp(any(), any()) } throws Exception("email taken")
        vm.signUp("user@test.com", "password")
        assertTrue(vm.alert.value!!.contains("error while registering"))
    }

    @Test
    fun `signUp - isLoading resets to false`() {
        coEvery { repo.signUp(any(), any()) } just Runs
        vm.signUp("user@test.com", "password")
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun `loginWithOTP success reset=true - passwordReset=true`() {
        coEvery { repo.verifyOtp(any(), any()) } just Runs
        vm.loginWithOTP("user@test.com", "123456", reset = true)
        assertTrue(vm.passwordReset.value)
    }

    @Test
    fun `loginWithOTP success reset=false - passwordReset=false`() {
        coEvery { repo.verifyOtp(any(), any()) } just Runs
        vm.loginWithOTP("user@test.com", "123456", reset = false)
        assertFalse(vm.passwordReset.value)
    }

    @Test
    fun `loginWithOTP failure - alert set`() {
        coEvery { repo.verifyOtp(any(), any()) } throws Exception("invalid otp")
        vm.loginWithOTP("user@test.com", "000000", reset = false)
        assertNotNull(vm.alert.value)
    }

    @Test
    fun `changePassword success - success alert`() {
        coEvery { repo.changePassword(any()) } just Runs
        vm.changePassword("newPass123")
        assertTrue(vm.alert.value!!.contains("Password changed successfully"))
    }

    @Test
    fun `changePassword failure - error alert`() {
        coEvery { repo.changePassword(any()) } throws Exception("weak password")
        vm.changePassword("weak")
        assertTrue(vm.alert.value!!.contains("error while changing"))
    }

    @Test
    fun `resetPassword - delegates to repository`() {
        coEvery { repo.resetPassword(any()) } just Runs
        vm.resetPassword("a@b.com")
        coVerify { repo.resetPassword("a@b.com") }
    }

    @Test
    fun `logout - calls signOut`() {
        coEvery { repo.signOut() } just Runs
        vm.logout()
        coVerify { repo.signOut() }
    }
}
