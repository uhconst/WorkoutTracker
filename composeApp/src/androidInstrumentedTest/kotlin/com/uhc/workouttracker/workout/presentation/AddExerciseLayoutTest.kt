package com.uhc.workouttracker.workout.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.WeightLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class AddExerciseLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val chest = MuscleGroup(id = 1L, name = "Chest")
    private val back = MuscleGroup(id = 2L, name = "Back")
    private val weightLog1 = WeightLog(id = 1L, weight = 20f, exerciseId = 1L)
    private val exercise1 = Exercise(id = 1L, name = "Bench Press", muscleGroupId = 1L, weightLogs = listOf(weightLog1))

    @Test
    fun `shows Add Exercise title`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(exercise = null)
            }
        }
        composeTestRule.onNodeWithText("Add Exercise").assertIsDisplayed()
    }

    @Test
    fun `shows Edit Exercise title`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = listOf(chest),
                    exercise = exercise1
                )
            }
        }
        composeTestRule.onNodeWithText("Edit Exercise").assertIsDisplayed()
    }

    @Test
    fun `exercise name pre-filled in edit mode`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = listOf(chest),
                    exercise = exercise1
                )
            }
        }
        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
    }

    @Test
    fun `weight pre-filled from last log in edit mode`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = listOf(chest),
                    exercise = exercise1
                )
            }
        }
        composeTestRule.onNodeWithText("20.0").assertIsDisplayed()
    }

    @Test
    fun `muscle group error shown when Save with no muscle selected`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(muscleGroups = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Please select a muscle group").assertIsDisplayed()
    }

    @Test
    fun `exercise name error shown when name blank`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = listOf(chest),
                    exercise = exercise1.copy(name = "")
                )
            }
        }
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Please enter an exercise name").assertIsDisplayed()
    }

    @Test
    fun `weight error shown when weight blank`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = listOf(chest),
                    exercise = exercise1.copy(weightLogs = emptyList())
                )
            }
        }
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Please enter a valid weight").assertIsDisplayed()
    }

    @Test
    fun `onSaveExercise NOT called when validation fails`() {
        var called = false
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = emptyList(),
                    onSaveExercise = { _, _, _ -> called = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Save").performClick()
        assertFalse(called)
    }

    @Test
    fun `onSaveExercise called with correct params when valid`() {
        var capturedName: String? = null
        var capturedMuscleGroupId: Long? = null
        var capturedWeight: Double? = null
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(
                    muscleGroups = listOf(chest),
                    exercise = exercise1,
                    onSaveExercise = { name, muscleGroupId, weight ->
                        capturedName = name
                        capturedMuscleGroupId = muscleGroupId
                        capturedWeight = weight
                    }
                )
            }
        }
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals("Bench Press", capturedName)
        assertEquals(1L, capturedMuscleGroupId)
        assertEquals(20.0, capturedWeight)
    }

    @Test
    fun `muscle group dropdown shows options on expand`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(muscleGroups = listOf(chest, back))
            }
        }
        composeTestRule.onNodeWithText("Muscle Group").performClick()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun `selecting muscle from dropdown clears error`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                AddExerciseLayout(muscleGroups = listOf(chest))
            }
        }
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Please select a muscle group").assertIsDisplayed()
        composeTestRule.onNodeWithText("Muscle Group").performClick()
        composeTestRule.onNodeWithText("Chest").performClick()
        composeTestRule.onNodeWithText("Please select a muscle group").assertDoesNotExist()
    }
}
