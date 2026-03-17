package com.uhc.workouttracker.muscle.presentation

import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel.EditState
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MuscleGroupsViewModelTest {

    private val repo = mockk<MuscleGroupRepository>(relaxed = true)
    private lateinit var vm: MuscleGroupsViewModel

    private val muscle1 = MuscleGroup(id = 1L, name = "Chest")
    private val muscle2 = MuscleGroup(id = 2L, name = "Back")

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repo.observeMuscleGroups() } returns flowOf(emptyList())
        vm = MuscleGroupsViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `editState initial is NotEditing`() {
        assertTrue(vm.editState.value is EditState.NotEditing)
    }

    @Test
    fun `addMuscleGroup blank string - does NOT call repo`() {
        vm.addMuscleGroup("")
        coVerify(exactly = 0) { repo.addMuscleGroup(any()) }
    }

    @Test
    fun `addMuscleGroup whitespace only - does NOT call repo`() {
        vm.addMuscleGroup("   ")
        coVerify(exactly = 0) { repo.addMuscleGroup(any()) }
    }

    @Test
    fun `addMuscleGroup valid name - calls repo`() {
        coEvery { repo.addMuscleGroup(any()) } just Runs
        vm.addMuscleGroup("Chest")
        coVerify { repo.addMuscleGroup("Chest") }
    }

    @Test
    fun `startEditing - transitions to Editing with correct muscle`() {
        vm.startEditing(muscle1)
        assertEquals(muscle1, (vm.editState.value as EditState.Editing).muscleGroup)
    }

    @Test
    fun `cancelEditing - resets to NotEditing`() {
        vm.startEditing(muscle1)
        vm.cancelEditing()
        assertTrue(vm.editState.value is EditState.NotEditing)
    }

    @Test
    fun `updateMuscleGroup when not editing - does nothing`() {
        vm.updateMuscleGroup("New Chest")
        coVerify(exactly = 0) { repo.updateMuscleGroup(any()) }
    }

    @Test
    fun `updateMuscleGroup blank name when editing - does nothing`() {
        vm.startEditing(muscle1)
        vm.updateMuscleGroup("")
        coVerify(exactly = 0) { repo.updateMuscleGroup(any()) }
    }

    @Test
    fun `updateMuscleGroup valid name when editing - calls repo with updated muscle`() {
        coEvery { repo.updateMuscleGroup(any()) } just Runs
        vm.startEditing(muscle1)
        vm.updateMuscleGroup("New Chest")
        coVerify { repo.updateMuscleGroup(muscle1.copy(name = "New Chest")) }
    }

    @Test
    fun `updateMuscleGroup - resets editState to NotEditing`() {
        coEvery { repo.updateMuscleGroup(any()) } just Runs
        vm.startEditing(muscle1)
        vm.updateMuscleGroup("New Chest")
        assertTrue(vm.editState.value is EditState.NotEditing)
    }

    @Test
    fun `deleteMuscleGroup - calls repo with correct id`() {
        coEvery { repo.deleteMuscleGroup(any()) } just Runs
        vm.deleteMuscleGroup(muscle1)
        coVerify { repo.deleteMuscleGroup(muscle1.id) }
    }
}
