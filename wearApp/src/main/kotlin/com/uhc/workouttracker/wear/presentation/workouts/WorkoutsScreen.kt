package com.uhc.workouttracker.wear.presentation.workouts

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
fun WorkoutsScreen(
    onMuscleGroupClick: (Long) -> Unit,
    viewModel: WorkoutsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (val s = state) {
        is WorkoutsUiState.Loading -> CircularProgressIndicator()

        is WorkoutsUiState.NotAuthenticated -> Text(
            text = "Please log in on your phone",
            style = MaterialTheme.typography.body2
        )

        is WorkoutsUiState.Error -> Text(
            text = s.message,
            style = MaterialTheme.typography.body2
        )

        is WorkoutsUiState.Success -> ScalingLazyColumn(modifier = Modifier.fillMaxSize()) {
            items(s.groups) { group ->
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onMuscleGroupClick(group.id) },
                    colors = ChipDefaults.primaryChipColors(),
                    label = { Text(group.muscleName) },
                    secondaryLabel = {
                        Text("${group.exercises.size} exercises")
                    }
                )
            }
        }
    }
}
