package com.uhc.workouttracker.muscle.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.uhc.workouttracker.core.haptic.FakeHapticFeedback
import com.uhc.workouttracker.core.haptic.HapticType
import com.uhc.workouttracker.core.haptic.LocalHapticFeedback
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel.EditState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MuscleGroupsHapticTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fake = FakeHapticFeedback()
    private val muscle = MuscleGroup(id = 1L, name = "Chest")

    private fun setContent(block: @Composable () -> Unit) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides fake) {
                WorkoutTrackerTheme { block() }
            }
        }
    }

    @Test
    fun `save muscle group triggers Confirm haptic`() {
        setContent {
            MuscleGroupsLayout(
                muscleGroups = emptyList(),
                editState = EditState.NotEditing,
                onAddMuscleGroupClick = {}
            )
        }
        composeTestRule.onNodeWithText("Add Muscle Group").performClick()
        assertEquals(HapticType.Confirm, fake.last)
    }

    @Test
    fun `delete muscle group triggers Warning haptic`() {
        setContent {
            MuscleGroupsLayout(
                muscleGroups = listOf(muscle),
                onDeleteClick = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Delete Muscle Group").performClick()
        assertEquals(HapticType.Warning, fake.last)
    }
}
