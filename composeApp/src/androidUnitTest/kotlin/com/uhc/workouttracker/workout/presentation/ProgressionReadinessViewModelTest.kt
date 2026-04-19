package com.uhc.workouttracker.workout.presentation

import app.cash.turbine.test
import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import com.uhc.workouttracker.workout.domain.repository.ExerciseProgressionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProgressionReadinessViewModelTest {

    private val progressionRepo = mockk<ExerciseProgressionRepository>(relaxed = true)
    private lateinit var vm: ProgressionReadinessViewModel

    private val progressionsFlow = MutableStateFlow<Map<Long, ProgressionReadiness>>(emptyMap())

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { progressionRepo.observeAll() } returns progressionsFlow
        vm = ProgressionReadinessViewModel(progressionRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `current initial value is ON_TRACK`() {
        assertEquals(ProgressionReadiness.ON_TRACK, vm.current.value)
    }

    @Test
    fun `load with known exercise id - current reflects stored readiness`() = runTest {
        progressionsFlow.value = mapOf(42L to ProgressionReadiness.INCREASE_WEIGHT)
        vm.load(42L)
        assertEquals(ProgressionReadiness.INCREASE_WEIGHT, vm.current.value)
    }

    @Test
    fun `load with unknown exercise id - current defaults to ON_TRACK`() = runTest {
        progressionsFlow.value = mapOf(1L to ProgressionReadiness.REDUCE_WEIGHT)
        vm.load(999L)
        assertEquals(ProgressionReadiness.ON_TRACK, vm.current.value)
    }

    @Test
    fun `load then data updates - current reflects new value`() = runTest {
        vm.load(5L)
        progressionsFlow.value = mapOf(5L to ProgressionReadiness.REDUCE_WEIGHT)
        assertEquals(ProgressionReadiness.REDUCE_WEIGHT, vm.current.value)
    }

    @Test
    fun `select success - saved emits`() = runTest {
        coEvery { progressionRepo.update(any(), any()) } just Runs
        vm.load(1L)
        vm.saved.test {
            vm.select(ProgressionReadiness.INCREASE_WEIGHT)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `select success - calls update with correct exercise id and readiness`() = runTest {
        coEvery { progressionRepo.update(any(), any()) } just Runs
        vm.load(7L)
        vm.select(ProgressionReadiness.REDUCE_WEIGHT)
        coVerify { progressionRepo.update(7L, ProgressionReadiness.REDUCE_WEIGHT) }
    }

    @Test
    fun `select failure - error emits static message`() = runTest {
        coEvery { progressionRepo.update(any(), any()) } throws RuntimeException("db error")
        vm.load(1L)
        vm.error.test {
            vm.select(ProgressionReadiness.INCREASE_WEIGHT)
            val message = awaitItem()
            assertEquals("Failed to update. Please try again.", message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `select failure - saved does not emit`() = runTest {
        coEvery { progressionRepo.update(any(), any()) } throws RuntimeException("db error")
        vm.load(1L)
        vm.saved.test {
            vm.select(ProgressionReadiness.INCREASE_WEIGHT)
            expectNoEvents()
        }
    }
}
