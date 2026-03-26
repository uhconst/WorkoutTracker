package com.uhc.workouttracker.wear.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import com.uhc.workouttracker.wear.domain.model.Exercise
import com.uhc.workouttracker.wear.domain.model.WeightLog
import com.uhc.workouttracker.wear.theme.WearTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseDetailScreen(
    muscleId: Long,
    viewModel: ExerciseDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberScalingLazyListState()

    Scaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ExerciseDetailContent(state = state)
    }
}

@Composable
internal fun ExerciseDetailContent(state: ExerciseDetailUiState) {
    val listState = rememberScalingLazyListState()

    when (val s = state) {
        is ExerciseDetailUiState.Loading -> CircularProgressIndicator()

        is ExerciseDetailUiState.Error -> Text(
            text = s.message,
            style = MaterialTheme.typography.body2
        )

        is ExerciseDetailUiState.Success -> AnimatedVisibility(
            visible = true,
            enter = slideInVertically { it / 2 } + fadeIn()
        ) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(s.exercises) { exercise ->
                    val lastWeight = exercise.weightLogs.lastOrNull()?.weight
                    Chip(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {},
                        colors = ChipDefaults.secondaryChipColors(),
                        label = { Text(exercise.name) },
                        secondaryLabel = {
                            if (lastWeight != null) Text("$lastWeight kg")
                        }
                    )
                }
            }
        }
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun ExerciseDetailLoadingPreview() {
    WearTheme {
        ExerciseDetailContent(state = ExerciseDetailUiState.Loading)
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun ExerciseDetailSuccessPreview() {
    WearTheme {
        ExerciseDetailContent(
            state = ExerciseDetailUiState.Success(
                exercises = listOf(
                    Exercise(id = 1, name = "Barbell Curl", weightLogs = listOf(WeightLog(id = 1, weight = 15f))),
                    Exercise(id = 2, name = "Dumbbell Curl", weightLogs = listOf(WeightLog(id = 2, weight = 10f))),
                    Exercise(id = 3, name = "Hammer Curl", weightLogs = emptyList())
                )
            )
        )
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun ExerciseDetailErrorPreview() {
    WearTheme {
        ExerciseDetailContent(state = ExerciseDetailUiState.Error("Failed to load exercises. Please try again."))
    }
}
