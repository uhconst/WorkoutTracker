package com.uhc.workouttracker.authentication.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shows app title`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme { LoginLayout() }
        }
        composeTestRule.onNodeWithText("Workout Tracker").assertIsDisplayed()
    }

    @Test
    fun `login button disabled when email and password are empty`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme { LoginLayout(email = "", password = "") }
        }
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @Test
    fun `login button shows Login text in login mode`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                LoginLayout(
                    email = "user@example.com",
                    password = "secret",
                    isSignUp = false
                )
            }
        }
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun `login button shows Register text in signup mode`() {
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                LoginLayout(
                    email = "user@example.com",
                    password = "secret",
                    isSignUp = true
                )
            }
        }
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun `onForgotPassword called when forgot password clicked`() {
        var called = false
        composeTestRule.setContent {
            WorkoutTrackerTheme {
                LoginLayout(onForgotPassword = { called = true })
            }
        }
        composeTestRule.onNodeWithText("Forgot password?").performClick()
        assertTrue(called)
    }
}
