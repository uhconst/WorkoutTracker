package com.uhc.workouttracker.workout.presentation

import app.cash.turbine.test
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.WeightLog
import com.uhc.workouttracker.workout.domain.repository.ExerciseProgressionRepository
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AddExerciseViewModelTest {

    private val muscleRepo = mockk<MuscleGroupRepository>(relaxed = true)
    private val exerciseRepo = mockk<ExerciseRepository>(relaxed = true)
    private val progressionRepo = mockk<ExerciseProgressionRepository>(relaxed = true)
    private lateinit var vm: AddExerciseViewModel

    private val weightLog1 = WeightLog(id = 1L, weight = 20f, exerciseId = 1L)
    private val exercise1 = Exercise(id = 1L, name = "Bench Press", muscleGroupId = 1L, weightLogs = listOf(weightLog1))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { muscleRepo.observeMuscleGroups() } returns flowOf(emptyList())
        vm = AddExerciseViewModel(muscleRepo, exerciseRepo, progressionRepo, CoroutineScope(UnconfinedTestDispatcher()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `editingExercise initial is null`() {
        assertNull(vm.editingExercise.value)
    }

    @Test
    fun `setExerciseToEdit - sets editingExercise`() = runTest {
        coEvery { exerciseRepo.getExerciseById(1L) } returns exercise1
        vm.setExerciseToEdit(1L)
        assertEquals(exercise1, vm.editingExercise.value)
    }

    @Test
    fun `setExerciseToEdit non-existent id - sets null`() = runTest {
        coEvery { exerciseRepo.getExerciseById(any()) } returns null
        vm.setExerciseToEdit(999L)
        assertNull(vm.editingExercise.value)
    }

    @Test
    fun `saveExercise no editingExercise - calls saveExercise on repo`() {
        coEvery { exerciseRepo.saveExercise(any(), any(), any()) } just Runs
        vm.saveExercise("Curl", 1L, 20.0)
        coVerify { exerciseRepo.saveExercise("Curl", 1L, 20.0) }
    }

    @Test
    fun `saveExercise no editingExercise - does NOT call updateExercise`() {
        coEvery { exerciseRepo.saveExercise(any(), any(), any()) } just Runs
        vm.saveExercise("Curl", 1L, 20.0)
        coVerify(exactly = 0) { exerciseRepo.updateExercise(any(), any(), any(), any()) }
    }

    @Test
    fun `saveExercise with editingExercise - calls updateExercise`() = runTest {
        coEvery { exerciseRepo.getExerciseById(1L) } returns exercise1
        coEvery { exerciseRepo.updateExercise(any(), any(), any(), any()) } just Runs
        vm.setExerciseToEdit(1L)
        vm.saveExercise("Curl", 1L, 25.0)
        coVerify { exerciseRepo.updateExercise(exercise1.id, "Curl", 1L, 25.0) }
    }

    @Test
    fun `saveExercise with editingExercise - does NOT call saveExercise`() = runTest {
        coEvery { exerciseRepo.getExerciseById(1L) } returns exercise1
        coEvery { exerciseRepo.updateExercise(any(), any(), any(), any()) } just Runs
        vm.setExerciseToEdit(1L)
        vm.saveExercise("Curl", 1L, 25.0)
        coVerify(exactly = 0) { exerciseRepo.saveExercise(any(), any(), any()) }
    }

    @Test
    fun `saveSuccess emits Exercise added after new exercise`() = runTest {
        coEvery { exerciseRepo.saveExercise(any(), any(), any()) } just Runs
        vm.saveSuccess.test {
            vm.saveExercise("Curl", 1L, 20.0)
            assertEquals("Exercise added", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExercise with editingExercise - emits navigateBack immediately`() = runTest {
        coEvery { exerciseRepo.getExerciseById(1L) } returns exercise1
        coEvery { exerciseRepo.updateExercise(any(), any(), any(), any()) } just Runs
        vm.setExerciseToEdit(1L)
        vm.navigateBack.test {
            vm.saveExercise("Curl", 1L, 25.0)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExercise with editingExercise - does not emit saveSuccess`() = runTest {
        coEvery { exerciseRepo.getExerciseById(1L) } returns exercise1
        coEvery { exerciseRepo.updateExercise(any(), any(), any(), any()) } just Runs
        vm.setExerciseToEdit(1L)
        vm.saveSuccess.test {
            vm.saveExercise("Curl", 1L, 25.0)
            expectNoEvents()
        }
    }

    @Test
    fun `saveExercise failure - emits to saveError not saveSuccess`() = runTest {
        coEvery { exerciseRepo.saveExercise(any(), any(), any()) } throws RuntimeException("network error")
        vm.saveError.test {
            vm.saveExercise("Curl", 1L, 20.0)
            val message = awaitItem()
            assert(message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExercise failure - does not emit to saveSuccess`() = runTest {
        coEvery { exerciseRepo.saveExercise(any(), any(), any()) } throws RuntimeException("network error")
        vm.saveSuccess.test {
            vm.saveExercise("Curl", 1L, 20.0)
            expectNoEvents()
        }
    }

    @Test
    fun `saveExercise with editingExercise - calls reset on progressionRepo`() = runTest {
        coEvery { exerciseRepo.getExerciseById(1L) } returns exercise1
        coEvery { exerciseRepo.updateExercise(any(), any(), any(), any()) } just Runs
        coEvery { progressionRepo.reset(any()) } just Runs
        vm.setExerciseToEdit(1L)
        vm.saveExercise("Curl", 1L, 25.0)
        coVerify { progressionRepo.reset(exercise1.id) }
    }

    @Test
    fun `saveExercise new exercise - does NOT call reset on progressionRepo`() = runTest {
        coEvery { exerciseRepo.saveExercise(any(), any(), any()) } just Runs
        vm.saveExercise("Curl", 1L, 20.0)
        coVerify(exactly = 0) { progressionRepo.reset(any()) }
    }
}
