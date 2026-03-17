package com.uhc.workouttracker.wear

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.uhc.workouttracker.wear.data.repository.WearSessionRepository
import com.uhc.workouttracker.wear.presentation.detail.ExerciseDetailScreen
import com.uhc.workouttracker.wear.presentation.notpaired.NotPairedScreen
import com.uhc.workouttracker.wear.presentation.workouts.WorkoutsScreen
import com.uhc.workouttracker.wear.theme.WearTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun WearApp() {
    val sessionRepo: WearSessionRepository = koinInject()
    val navController = rememberSwipeDismissableNavController()
    val coroutineScope = rememberCoroutineScope()
    var startDest by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDest = if (sessionRepo.readSession() != null) "workouts" else "notpaired"
    }

    if (startDest == null) {
        WearTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    WearTheme {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = startDest!!
        ) {
            composable("notpaired") {
                NotPairedScreen(
                    onRetry = {
                        coroutineScope.launch {
                            if (sessionRepo.readSession() != null) {
                                navController.navigate("workouts") {
                                    popUpTo("notpaired") { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
            composable("workouts") {
                WorkoutsScreen(
                    onMuscleGroupClick = { muscleId ->
                        navController.navigate("detail/$muscleId")
                    }
                )
            }
            composable("detail/{muscleId}") { backStackEntry ->
                val muscleId = backStackEntry.arguments?.getString("muscleId")?.toLong() ?: return@composable
                ExerciseDetailScreen(muscleId = muscleId)
            }
        }
    }
}
