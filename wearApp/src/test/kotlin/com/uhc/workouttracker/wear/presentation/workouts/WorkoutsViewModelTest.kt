package com.uhc.workouttracker.wear.presentation.workouts

import com.uhc.workouttracker.wear.data.repository.WearExerciseRepository
import com.uhc.workouttracker.wear.data.repository.WearSessionRepository
import com.uhc.workouttracker.wear.domain.model.MuscleWithExercises
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutsViewModelTest {

    private val exerciseRepo = mockk<WearExerciseRepository>(relaxed = true)
    private val sessionRepo = mockk<WearSessionRepository>(relaxed = true)
    private lateinit var vm: WorkoutsViewModel

    private val muscleGroup = MuscleWithExercises(id = 1L, muscleName = "Chest", exercises = emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        // Default: session is valid, exercises load successfully
        coEvery { sessionRepo.importAndValidateSession() } returns true
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } returns listOf(muscleGroup)
        vm = WorkoutsViewModel(exerciseRepo, sessionRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `importAndValidateSession returns false - state is NotAuthenticated`() = runTest {
        coEvery { sessionRepo.importAndValidateSession() } returns false
        vm = WorkoutsViewModel(exerciseRepo, sessionRepo)
        assertEquals(WorkoutsUiState.NotAuthenticated, vm.state.value)
    }

    @Test
    fun `importAndValidateSession returns true and fetch succeeds - state is Success`() = runTest {
        assertEquals(WorkoutsUiState.Success(listOf(muscleGroup), listOf(muscleGroup)), vm.state.value)
    }

    @Test
    fun `importAndValidateSession returns true and fetch throws - state is Error`() = runTest {
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } throws RuntimeException("network error")
        vm = WorkoutsViewModel(exerciseRepo, sessionRepo)
        assertTrue(vm.state.value is WorkoutsUiState.Error)
    }

    @Test
    fun `loadData after initial load - resets to Loading then resolves`() = runTest {
        coEvery { sessionRepo.importAndValidateSession() } returns false
        vm.loadData()
        assertEquals(WorkoutsUiState.NotAuthenticated, vm.state.value)
    }

    @Test
    fun `toggleFilter adds id to selectedMuscleIds`() {
        vm.toggleFilter(1L)
        assertEquals(setOf(1L), vm.selectedMuscleIds.value)
    }

    @Test
    fun `toggleFilter same id twice - toggles off`() {
        vm.toggleFilter(1L)
        vm.toggleFilter(1L)
        assertEquals(emptySet<Long>(), vm.selectedMuscleIds.value)
    }

    @Test
    fun `toggleFilter null - clears all`() {
        vm.toggleFilter(1L)
        vm.toggleFilter(2L)
        vm.toggleFilter(null)
        assertEquals(emptySet<Long>(), vm.selectedMuscleIds.value)
    }

    @Test
    fun `filter applied to Success state - displayedGroups filtered`() = runTest {
        val chest = MuscleWithExercises(id = 1L, muscleName = "Chest", exercises = emptyList())
        val back = MuscleWithExercises(id = 2L, muscleName = "Back", exercises = emptyList())
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } returns listOf(chest, back)
        vm = WorkoutsViewModel(exerciseRepo, sessionRepo)
        vm.toggleFilter(1L)
        val state = vm.state.value as WorkoutsUiState.Success
        assertEquals(listOf(chest, back), state.allGroups)
        assertEquals(listOf(chest), state.displayedGroups)
    }
}
