package com.uhc.workouttracker.workout.presentation

import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.model.WeightLog
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExerciseListViewModelTest {

    private val exerciseRepo = mockk<ExerciseRepository>(relaxed = true)
    private val muscleRepo = mockk<MuscleGroupRepository>(relaxed = true)
    private lateinit var vm: ExerciseListViewModel

    private val weightLog1 = WeightLog(id = 1L, weight = 20f, exerciseId = 1L)
    private val exercise1 = Exercise(id = 1L, name = "Bench Press", muscleGroupId = 1L, weightLogs = listOf(weightLog1))
    private val bicepsGroup = MuscleWithExercises(id = 1L, muscleName = "Biceps", exercises = listOf(exercise1))
    private val chestGroup  = MuscleWithExercises(id = 2L, muscleName = "Chest",  exercises = emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { muscleRepo.observeMuscleGroups() } returns flowOf(emptyList())
        vm = ExerciseListViewModel(exerciseRepo, muscleRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `filteredExercises initial is empty`() {
        assertEquals(emptyList<MuscleWithExercises>(), vm.filteredExercises.value)
    }

    @Test
    fun `selectedMuscleIds initial is empty set`() {
        assertEquals(emptySet<Long>(), vm.selectedMuscleIds.value)
    }

    @Test
    fun `fetchExercises - populates filteredExercises no filter`() = runTest {
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } returns listOf(bicepsGroup, chestGroup)
        vm.fetchExercises()
        assertEquals(listOf(bicepsGroup, chestGroup), vm.filteredExercises.value)
    }

    @Test
    fun `selectMuscleFilter - adds id to set`() {
        vm.selectMuscleFilter(1L)
        assertEquals(setOf(1L), vm.selectedMuscleIds.value)
    }

    @Test
    fun `selectMuscleFilter same id twice - toggles off`() {
        vm.selectMuscleFilter(1L)
        vm.selectMuscleFilter(1L)
        assertEquals(emptySet<Long>(), vm.selectedMuscleIds.value)
    }

    @Test
    fun `selectMuscleFilter multiple ids - accumulates`() {
        vm.selectMuscleFilter(1L)
        vm.selectMuscleFilter(2L)
        assertEquals(setOf(1L, 2L), vm.selectedMuscleIds.value)
    }

    @Test
    fun `selectMuscleFilter null - clears all`() {
        vm.selectMuscleFilter(1L)
        vm.selectMuscleFilter(2L)
        vm.selectMuscleFilter(null)
        assertEquals(emptySet<Long>(), vm.selectedMuscleIds.value)
    }

    @Test
    fun `filteredExercises filters by selected muscle after load`() = runTest {
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } returns listOf(bicepsGroup, chestGroup)
        vm.fetchExercises()
        vm.selectMuscleFilter(1L)
        assertEquals(listOf(bicepsGroup), vm.filteredExercises.value)
    }

    @Test
    fun `filteredExercises shows all when filter cleared after being set`() = runTest {
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } returns listOf(bicepsGroup, chestGroup)
        vm.fetchExercises()
        vm.selectMuscleFilter(1L)
        vm.selectMuscleFilter(null)
        assertEquals(listOf(bicepsGroup, chestGroup), vm.filteredExercises.value)
    }

    @Test
    fun `filteredExercises filters before load - correct result after fetch`() = runTest {
        coEvery { exerciseRepo.getExercisesGroupedByMuscle() } returns listOf(bicepsGroup, chestGroup)
        vm.selectMuscleFilter(1L)
        vm.fetchExercises()
        assertEquals(listOf(bicepsGroup), vm.filteredExercises.value)
    }
}
