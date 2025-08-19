package com.uhc.workouttracker.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
        startDestination = NavRoute.AuthenticationDestination
    ) {
        composable<NavRoute.AuthenticationDestination> {
            LoginScreen()
        }
        composable<NavRoute.WorkoutListDestination> {
            ExerciseListScreen(drawerState = drawerState)
        }
        composable<NavRoute.MuscleGroupsDestination> {
            MuscleGroupsScreen(drawerState = drawerState)
        }
        composable<NavRoute.AddExerciseDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoute.AddExerciseDestination>()
            AddExerciseScreen(drawerState = drawerState, exerciseId = args.exerciseId)
        }
    }
}
