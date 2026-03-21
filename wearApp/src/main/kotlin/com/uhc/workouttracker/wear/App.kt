package com.uhc.workouttracker.wear

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import com.uhc.workouttracker.wear.core.haptic.AndroidHapticFeedback
import com.uhc.workouttracker.wear.core.haptic.LocalHapticFeedback
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.uhc.workouttracker.wear.data.repository.WearSessionRepository
import com.uhc.workouttracker.wear.presentation.detail.ExerciseDetailScreen
import com.uhc.workouttracker.wear.presentation.filter.FilterScreen
import com.uhc.workouttracker.wear.presentation.notpaired.NotPairedScreen
import com.uhc.workouttracker.wear.presentation.workouts.WorkoutsScreen
import com.uhc.workouttracker.wear.presentation.workouts.WorkoutsViewModel
import com.uhc.workouttracker.wear.theme.WearTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "WearApp"
private const val PAGE_FILTER = 0
private const val PAGE_WORKOUTS = 1

@Composable
fun WearApp() {
    val sessionRepo: WearSessionRepository = koinInject()
    val navController = rememberSwipeDismissableNavController()
    val coroutineScope = rememberCoroutineScope()
    var startDest by remember { mutableStateOf<String?>(null) }

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

    val view = LocalView.current
    val hapticFeedback = remember(view) { AndroidHapticFeedback(view) }

    WearTheme {
      CompositionLocalProvider(LocalHapticFeedback provides hapticFeedback) {
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
                WorkoutsPager(
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
}

/**
 * Two-page horizontal pager: [Filter] ←→ [Workouts].
 * Scroll states are hoisted here so the Scaffold's PositionIndicator
 * can read whichever page is currently active.
 */
@Composable
private fun WorkoutsPager(onMuscleGroupClick: (Long) -> Unit) {
    val viewModel: WorkoutsViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(initialPage = PAGE_WORKOUTS) { 2 }
    val filterScrollState: ScrollState = rememberScrollState()
    val workoutsListState: ScalingLazyListState = rememberScalingLazyListState()

    val pageIndicatorState = remember(pagerState) {
        object : PageIndicatorState {
            override val pageOffset get() = pagerState.currentPageOffsetFraction
            override val selectedPage get() = pagerState.currentPage
            override val pageCount get() = 2
        }
    }

    // True when the content of the currently visible page is being scrolled
    val isContentScrolling by remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                PAGE_FILTER -> filterScrollState.isScrollInProgress
                else -> workoutsListState.isScrollInProgress
            }
        }
    }

    Scaffold(
        positionIndicator = {
            when (pagerState.currentPage) {
                PAGE_FILTER -> PositionIndicator(scrollState = filterScrollState)
                else -> PositionIndicator(scalingLazyListState = workoutsListState)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState) { page ->
                val pageOffset = ((pagerState.currentPage - page) +
                    pagerState.currentPageOffsetFraction).absoluteValue
                val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                ) {
                    when (page) {
                        PAGE_FILTER -> FilterScreen(
                            viewModel = viewModel,
                            scrollState = filterScrollState
                        )
                        PAGE_WORKOUTS -> WorkoutsScreen(
                            viewModel = viewModel,
                            listState = workoutsListState,
                            onMuscleGroupClick = onMuscleGroupClick,
                            onFilterClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(PAGE_FILTER)
                                }
                            }
                        )
                    }
                }
            }

            // Page dots — fade out while the content list is being scrolled
            AnimatedVisibility(
                visible = !isContentScrolling,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HorizontalPageIndicator(pageIndicatorState = pageIndicatorState)
            }
        }
    }
}
