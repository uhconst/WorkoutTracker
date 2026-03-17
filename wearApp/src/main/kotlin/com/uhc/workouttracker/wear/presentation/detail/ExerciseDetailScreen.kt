package com.uhc.workouttracker.wear.presentation.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseDetailScreen(
    muscleId: Long,
    viewModel: ExerciseDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (val s = state) {
        is ExerciseDetailUiState.Loading -> CircularProgressIndicator()

        is ExerciseDetailUiState.Error -> Text(
            text = s.message,
            style = MaterialTheme.typography.body2
        )

        is ExerciseDetailUiState.Success -> ScalingLazyColumn(modifier = Modifier.fillMaxSize()) {
            items(s.exercises) { exercise ->
                val lastWeight = exercise.weightLogs.lastOrNull()?.weight
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {},
                    colors = ChipDefaults.secondaryChipColors(),
                    label = { Text(exercise.name) },
                    secondaryLabel = {
                        if (lastWeight != null) {
                            Text("$lastWeight kg")
                        }
                    }
                )
            }
        }
    }
}
