package com.uhc.workouttracker.workout.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import org.junit.Rule
import org.junit.Test

class AddExerciseSnackbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `save error message shown in snackbar`() {
        val snackbarHostState = SnackbarHostState()
        composeTestRule.setContent {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("Failed to save exercise. Please try again.")
            }
            WorkoutTrackerTheme {
                AddExerciseLayout(snackbarHostState = snackbarHostState)
            }
        }
        composeTestRule.onNodeWithText("Failed to save exercise. Please try again.").assertIsDisplayed()
    }
}
