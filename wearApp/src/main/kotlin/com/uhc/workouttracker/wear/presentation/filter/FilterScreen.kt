package com.uhc.workouttracker.wear.presentation.filter

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.uhc.workouttracker.wear.presentation.workouts.WorkoutsUiState
import com.uhc.workouttracker.wear.presentation.workouts.WorkoutsViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    viewModel: WorkoutsViewModel,
    scrollState: ScrollState
) {
    val state by viewModel.state.collectAsState()
    val selectedMuscleIds by viewModel.selectedMuscleIds.collectAsState()

    val allGroups = (state as? WorkoutsUiState.Success)?.allGroups ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Filter",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val allSelected = selectedMuscleIds.isEmpty()
            Chip(
                onClick = { viewModel.toggleFilter(null) },
                colors = if (allSelected) ChipDefaults.primaryChipColors()
                         else ChipDefaults.secondaryChipColors(),
                label = { Text("All") }
            )

            allGroups.forEach { group ->
                val selected = group.id in selectedMuscleIds
                Chip(
                    onClick = { viewModel.toggleFilter(group.id) },
                    colors = if (selected) ChipDefaults.primaryChipColors()
                             else ChipDefaults.secondaryChipColors(),
                    label = { Text(group.muscleName) }
                )
            }
        }
    }
}
