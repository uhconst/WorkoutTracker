package com.uhc.workouttracker.workout.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.uhc.workouttracker.core.haptic.FakeHapticFeedback
import com.uhc.workouttracker.core.haptic.HapticType
import com.uhc.workouttracker.core.haptic.LocalHapticFeedback
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExerciseListHapticTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fake = FakeHapticFeedback()
    private val biceps = MuscleGroup(id = 1L, name = "Biceps")

    private fun setContent(block: @Composable () -> Unit) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides fake) {
                WorkoutTrackerTheme { block() }
            }
        }
    }

    @Test
    fun `All filter chip click triggers Selection haptic`() {
        setContent {
            ExerciseListLayout(
                muscles = listOf(biceps),
                selectedMuscleIds = setOf(1L)
            )
        }
        composeTestRule.onNodeWithText("All").performClick()
        assertEquals(HapticType.Selection, fake.last)
    }

    @Test
    fun `muscle filter chip click triggers Selection haptic`() {
        setContent {
            ExerciseListLayout(muscles = listOf(biceps))
        }
        composeTestRule.onNodeWithText("Biceps").performClick()
        assertEquals(HapticType.Selection, fake.last)
    }
}
