package com.uhc.workouttracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uhc.workouttracker.authentication.presentation.LoginScreen
import com.uhc.workouttracker.workout.presentation.WorkoutListScreen

@Composable
fun TicketMasterNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.AuthenticationDestination.value
    ) {
        composable(route = NavRoute.AuthenticationDestination.value) {
            LoginScreen(navController)
        }
        composable(route = NavRoute.WorkoutListDestination.value) {
            WorkoutListScreen()
        }
    }
}
