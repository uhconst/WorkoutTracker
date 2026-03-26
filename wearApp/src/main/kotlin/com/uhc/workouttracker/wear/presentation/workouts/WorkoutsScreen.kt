package com.uhc.workouttracker.wear.presentation.workouts

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
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.uhc.workouttracker.wear.domain.model.MuscleWithExercises
import com.uhc.workouttracker.wear.theme.WearTheme

@Composable
fun WorkoutsScreen(
    onMuscleGroupClick: (Long) -> Unit,
    onFilterClick: () -> Unit,
    listState: ScalingLazyListState,
    viewModel: WorkoutsViewModel
) {
    val state by viewModel.state.collectAsState()
    val selectedMuscleIds by viewModel.selectedMuscleIds.collectAsState()

    WorkoutsContent(
        state = state,
        selectedMuscleIds = selectedMuscleIds,
        onMuscleGroupClick = onMuscleGroupClick,
        onFilterClick = onFilterClick,
        listState = listState
    )
}

@Composable
internal fun WorkoutsContent(
    state: WorkoutsUiState,
    selectedMuscleIds: Set<Long> = emptySet(),
    onMuscleGroupClick: (Long) -> Unit = {},
    onFilterClick: () -> Unit = {},
    listState: ScalingLazyListState = rememberScalingLazyListState()
) {
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

        is WorkoutsUiState.Success -> ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (selectedMuscleIds.isNotEmpty()) {
                item {
                    CompactChip(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onFilterClick,
                        label = { Text("← Filter (${selectedMuscleIds.size})") }
                    )
                }
            }

            items(s.displayedGroups) { group ->
                Chip(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onMuscleGroupClick(group.id) },
                    colors = ChipDefaults.primaryChipColors(),
                    label = { Text(group.muscleName) },
                    secondaryLabel = { Text("${group.exercises.size} exercises") }
                )
            }
        }
    }
}

private val previewGroups = listOf(
    MuscleWithExercises(id = 1, muscleName = "Biceps", exercises = listOf()),
    MuscleWithExercises(id = 2, muscleName = "Triceps", exercises = listOf()),
    MuscleWithExercises(id = 3, muscleName = "Chest", exercises = listOf())
)

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun WorkoutsLoadingPreview() {
    WearTheme {
        WorkoutsContent(state = WorkoutsUiState.Loading)
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun WorkoutsSuccessPreview() {
    WearTheme {
        WorkoutsContent(
            state = WorkoutsUiState.Success(
                allGroups = previewGroups,
                displayedGroups = previewGroups
            )
        )
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun WorkoutsSuccessFilteredPreview() {
    WearTheme {
        WorkoutsContent(
            state = WorkoutsUiState.Success(
                allGroups = previewGroups,
                displayedGroups = previewGroups.take(1)
            ),
            selectedMuscleIds = setOf(1L)
        )
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun WorkoutsErrorPreview() {
    WearTheme {
        WorkoutsContent(state = WorkoutsUiState.Error("Failed to load workouts. Please try again."))
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun WorkoutsNotAuthenticatedPreview() {
    WearTheme {
        WorkoutsContent(state = WorkoutsUiState.NotAuthenticated)
    }
}
