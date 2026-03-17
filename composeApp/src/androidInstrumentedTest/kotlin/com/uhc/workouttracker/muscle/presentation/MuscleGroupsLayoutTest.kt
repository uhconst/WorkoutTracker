package com.uhc.workouttracker.muscle.presentation

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel.EditState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MuscleGroupsLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val muscle1 = MuscleGroup(id = 1L, name = "Chest")
    private val muscle2 = MuscleGroup(id = 2L, name = "Back")

    @Test
    fun `shows Muscle Groups title`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(muscleGroups = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Muscle Groups").assertIsDisplayed()
    }

    @Test
    fun `shows muscle group names`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(muscleGroups = listOf(muscle1, muscle2))
            }
        }
        composeTestRule.onNodeWithText("Chest").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun `shows Add Muscle Group button when not editing`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(editState = EditState.NotEditing)
            }
        }
        composeTestRule.onNodeWithText("Add Muscle Group").assertIsDisplayed()
    }

    @Test
    fun `shows Update button when editing`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(editState = EditState.Editing(muscle1))
            }
        }
        composeTestRule.onNodeWithText("Update Muscle Group").assertIsDisplayed()
    }

    @Test
    fun `text field label changes when editing`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(editState = EditState.Editing(muscle1))
            }
        }
        composeTestRule.onNodeWithText("Edit Muscle group name").assertIsDisplayed()
    }

    @Test
    fun `onAddMuscleGroupClick called with typed text`() {
        var capturedText: String? = null
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(
                    editState = EditState.NotEditing,
                    onAddMuscleGroupClick = { capturedText = it }
                )
            }
        }
        composeTestRule.onNodeWithText("Add Muscle group name").performTextInput("Legs")
        composeTestRule.onNodeWithText("Add Muscle Group").performClick()
        assertEquals("Legs", capturedText)
    }

    @Test
    fun `onUpdateMuscleGroupClick called when editing`() {
        var capturedText: String? = null
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(
                    editState = EditState.Editing(muscle1),
                    onUpdateMuscleGroupClick = { capturedText = it }
                )
            }
        }
        // Clear pre-filled text and type new name
        composeTestRule.onNodeWithText("Chest").performClick()
        composeTestRule.onNodeWithText("Chest").performTextInput(" Updated")
        composeTestRule.onNodeWithText("Update Muscle Group").performClick()
        assertEquals("Chest Updated", capturedText)
    }

    @Test
    fun `edit button calls onEditClick`() {
        var capturedMuscle: MuscleGroup? = null
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(
                    muscleGroups = listOf(muscle1),
                    onEditClick = { capturedMuscle = it }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Edit Muscle Group").performClick()
        assertEquals(muscle1, capturedMuscle)
    }

    @Test
    fun `delete button calls onDeleteClick`() {
        var capturedMuscle: MuscleGroup? = null
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(
                    muscleGroups = listOf(muscle1),
                    onDeleteClick = { capturedMuscle = it }
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Delete Muscle Group").performClick()
        assertEquals(muscle1, capturedMuscle)
    }

    @Test
    fun `cancel edit button shown for currently edited muscle`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(
                    muscleGroups = listOf(muscle1),
                    editState = EditState.Editing(muscle1)
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Cancel Edit").assertIsDisplayed()
    }

    @Test
    fun `delete button hidden for currently edited muscle`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                MuscleGroupsLayout(
                    muscleGroups = listOf(muscle1),
                    editState = EditState.Editing(muscle1)
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Delete Muscle Group").assertDoesNotExist()
    }
}
