package com.uhc.workouttracker.workout.presentation

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.model.WeightLog
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExerciseListLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val bicepsMuscle = MuscleGroup(id = 1L, name = "Biceps")
    private val chestMuscle  = MuscleGroup(id = 2L, name = "Chest")
    private val weightLog1 = WeightLog(id = 1L, weight = 20f, exerciseId = 1L)
    private val exercise1 = Exercise(id = 1L, name = "Bench Press", muscleGroupId = 1L, weightLogs = listOf(weightLog1))
    private val bicepsGroup = MuscleWithExercises(id = 1L, muscleName = "Biceps", exercises = listOf(exercise1))
    private val chestGroup  = MuscleWithExercises(id = 2L, muscleName = "Chest",  exercises = emptyList())

    @Test
    fun `shows Exercises title`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout()
            }
        }
        composeTestRule.onNodeWithText("Exercises").assertIsDisplayed()
    }

    @Test
    fun `shows All filter chip`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout()
            }
        }
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }

    @Test
    fun `shows muscle filter chips`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(muscles = listOf(bicepsMuscle, chestMuscle))
            }
        }
        composeTestRule.onNodeWithText("Biceps").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chest").assertIsDisplayed()
    }

    @Test
    fun `muscle group card shown`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(exercisesGroupedByMuscle = listOf(bicepsGroup))
            }
        }
        composeTestRule.onNodeWithText("Biceps").assertIsDisplayed()
    }

    @Test
    fun `exercises hidden when group collapsed`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    exercisesGroupedByMuscle = listOf(bicepsGroup),
                    expandedState = mutableStateMapOf(1L to false)
                )
            }
        }
        composeTestRule.onNodeWithText("Bench Press").assertDoesNotExist()
    }

    @Test
    fun `exercises shown when group expanded`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    exercisesGroupedByMuscle = listOf(bicepsGroup),
                    expandedState = mutableStateMapOf(1L to true)
                )
            }
        }
        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
    }

    @Test
    fun `weight log shown in expanded group`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    exercisesGroupedByMuscle = listOf(bicepsGroup),
                    expandedState = mutableStateMapOf(1L to true)
                )
            }
        }
        composeTestRule.onNodeWithText("20.00 kg").assertIsDisplayed()
    }

    @Test
    fun `empty message shown for expanded group with no exercises`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    exercisesGroupedByMuscle = listOf(chestGroup),
                    expandedState = mutableStateMapOf(2L to true)
                )
            }
        }
        composeTestRule.onNodeWithText("No exercises available for this muscle group").assertIsDisplayed()
    }

    @Test
    fun `onMuscleSelected called with null when All clicked`() {
        var capturedId: Long? = -1L
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    muscles = listOf(bicepsMuscle),
                    onMuscleSelected = { capturedId = it }
                )
            }
        }
        composeTestRule.onNodeWithText("All").performClick()
        assertEquals(null, capturedId)
    }

    @Test
    fun `onMuscleSelected called with id when muscle chip clicked`() {
        var capturedId: Long? = null
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    muscles = listOf(bicepsMuscle),
                    onMuscleSelected = { capturedId = it }
                )
            }
        }
        composeTestRule.onNodeWithText("Biceps").performClick()
        assertEquals(1L, capturedId)
    }

    @Test
    fun `clicking expanded exercise item does not crash`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                ExerciseListLayout(
                    exercisesGroupedByMuscle = listOf(bicepsGroup),
                    expandedState = mutableStateMapOf(1L to true)
                )
            }
        }
        composeTestRule.onNodeWithText("Bench Press").performClick()
        // No crash expected — navController is null so navigate is a no-op
    }
}
