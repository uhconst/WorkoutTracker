package com.uhc.workouttracker.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.uhc.workouttracker.authentication.domain.model.AuthState
import com.uhc.workouttracker.authentication.domain.repository.AuthRepository
import com.uhc.workouttracker.authentication.presentation.LoginScreen
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsScreen
import com.uhc.workouttracker.workout.presentation.AddExerciseScreen
import com.uhc.workouttracker.workout.presentation.ExerciseListScreen
import com.uhc.workouttracker.workout.presentation.ExerciseProgressionGraphScreen
import com.uhc.workouttracker.workout.presentation.ProgressionReadinessScreen
import org.koin.compose.koinInject

@Composable
fun TicketMasterNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    drawerState: DrawerState? = null
) {
    val authRepository: AuthRepository = koinInject()
    val sessionStatus by remember { authRepository.sessionStatus() }.collectAsState(initial = AuthState.Loading)

    var startDestination by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(sessionStatus) {
        if (sessionStatus != AuthState.Loading && startDestination == null) {
            startDestination = when (sessionStatus) {
                AuthState.Authenticated -> NavRoute.WorkoutListDestination
                else -> NavRoute.AuthenticationDestination
            }
        }
    }

    if (startDestination == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination!!
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
            composable<NavRoute.ExerciseProgressionGraphDestination> { backStackEntry ->
                val args = backStackEntry.toRoute<NavRoute.ExerciseProgressionGraphDestination>()
                ExerciseProgressionGraphScreen(exerciseId = args.exerciseId, exerciseName = args.exerciseName)
            }
            composable<NavRoute.ProgressionReadinessDestination> { backStackEntry ->
                val args = backStackEntry.toRoute<NavRoute.ProgressionReadinessDestination>()
                ProgressionReadinessScreen(
                    exerciseId = args.exerciseId,
                    exerciseName = args.exerciseName
                )
            }
        }
    }
}
