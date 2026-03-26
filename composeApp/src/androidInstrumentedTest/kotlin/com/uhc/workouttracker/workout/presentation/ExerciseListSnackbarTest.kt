package com.uhc.workouttracker.workout.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import org.junit.Rule
import org.junit.Test

class ExerciseListSnackbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `error message shown in snackbar`() {
        val snackbarHostState = SnackbarHostState()
        composeTestRule.setContent {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("Failed to load exercises. Please try again.")
            }
            WorkoutTrackerTheme {
                ExerciseListLayout(snackbarHostState = snackbarHostState)
            }
        }
        composeTestRule.onNodeWithText("Failed to load exercises. Please try again.").assertIsDisplayed()
    }
}
