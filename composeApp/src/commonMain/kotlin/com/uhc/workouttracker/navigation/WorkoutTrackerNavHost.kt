package com.uhc.workouttracker.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.uhc.workouttracker.authentication.presentation.LoginScreen
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsScreen
import com.uhc.workouttracker.workout.presentation.AddExerciseScreen
import com.uhc.workouttracker.workout.presentation.ExerciseListScreen

@Composable
fun TicketMasterNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    drawerState: DrawerState? = null
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
            ExerciseListScreen(drawerState = drawerState)
        }
        composable(route = NavRoute.MuscleGroupsDestination.value) {
            MuscleGroupsScreen(drawerState = drawerState)
        }
        composable(route = NavRoute.AddExerciseDestination.value) {
            AddExerciseScreen(drawerState = drawerState, navController = navController)
        }
    }
}
