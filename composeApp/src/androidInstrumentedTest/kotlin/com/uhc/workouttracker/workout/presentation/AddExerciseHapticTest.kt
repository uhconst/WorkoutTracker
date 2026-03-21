package com.uhc.workouttracker.workout.presentation

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
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.WeightLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class AddExerciseHapticTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fake = FakeHapticFeedback()
    private val chest = MuscleGroup(id = 1L, name = "Chest")
    private val exercise = Exercise(
        id = 1L,
        name = "Bench Press",
        muscleGroupId = 1L,
        weightLogs = listOf(WeightLog(id = 1L, weight = 20f, exerciseId = 1L))
    )

    private fun setContent(block: @Composable () -> Unit) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides fake) {
                WorkoutTrackerTheme { block() }
            }
        }
    }

    @Test
    fun `weight increase button triggers Selection haptic`() {
        setContent { AddExerciseLayout(muscleGroups = listOf(chest), exercise = exercise) }
        composeTestRule.onNodeWithContentDescription("Increase weight").performClick()
        assertEquals(HapticType.Selection, fake.last)
    }

    @Test
    fun `weight decrease button triggers Selection haptic`() {
        setContent { AddExerciseLayout(muscleGroups = listOf(chest), exercise = exercise) }
        composeTestRule.onNodeWithContentDescription("Decrease weight").performClick()
        assertEquals(HapticType.Selection, fake.last)
    }

    @Test
    fun `save with invalid form triggers Reject haptic`() {
        setContent { AddExerciseLayout(muscleGroups = emptyList()) }
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(HapticType.Reject, fake.last)
    }

    @Test
    fun `save with valid form triggers Confirm haptic`() {
        setContent {
            AddExerciseLayout(
                muscleGroups = listOf(chest),
                exercise = exercise,
                onSaveExercise = { _, _, _ -> }
            )
        }
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(HapticType.Confirm, fake.last)
    }

    @Test
    fun `no haptic triggered on initial render`() {
        setContent { AddExerciseLayout(muscleGroups = listOf(chest), exercise = exercise) }
        assertNull(fake.last)
    }
}
