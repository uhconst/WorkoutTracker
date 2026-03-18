package com.uhc.workouttracker.wear

import android.util.Log
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

private const val TAG = "WearApp"

@Composable
fun WearApp() {
    val sessionRepo: WearSessionRepository = koinInject()
    val navController = rememberSwipeDismissableNavController()
    val coroutineScope = rememberCoroutineScope()
    var startDest by remember { mutableStateOf<String?>(null) }

    // Initial session check on startup
    LaunchedEffect(Unit) {
        Log.d(TAG, "LaunchedEffect: checking for stored session")
        val session = sessionRepo.readSession()
        startDest = if (session != null) {
            Log.d(TAG, "LaunchedEffect: session found → navigating to workouts")
            "workouts"
        } else {
            Log.d(TAG, "LaunchedEffect: no session → showing notpaired")
            "notpaired"
        }
    }

    // While on the notpaired screen, listen for the phone pushing the session.
    LaunchedEffect(startDest) {
        if (startDest == "notpaired") {
            Log.d(TAG, "LaunchedEffect(startDest): registering live session observer")
            sessionRepo.observeSession().collect { tokens ->
                if (tokens != null) {
                    Log.d(TAG, "LaunchedEffect(startDest): live session received, navigating to workouts")
                    navController.navigate("workouts") {
                        popUpTo("notpaired") { inclusive = true }
                    }
                } else {
                    Log.d(TAG, "LaunchedEffect(startDest): session cleared by phone")
                }
            }
        }
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
                            Log.d(TAG, "Retry tapped: re-checking session")
                            val session = sessionRepo.readSession()
                            if (session != null) {
                                Log.d(TAG, "Retry: session found, navigating to workouts")
                                navController.navigate("workouts") {
                                    popUpTo("notpaired") { inclusive = true }
                                }
                            } else {
                                Log.d(TAG, "Retry: still no session")
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
                val muscleId = backStackEntry.arguments?.getString("muscleId")?.toLong()
                    ?: return@composable
                ExerciseDetailScreen(muscleId = muscleId)
            }
        }
    }
}
