package com.uhc.workouttracker.wear.presentation.filter

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Filter",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Pinned "All" chip above the FlowRow
        val allSelected = selectedMuscleIds.isEmpty()
        val allChipColors by animateColorAsState(
            targetValue = if (allSelected) MaterialTheme.colors.primary
                          else MaterialTheme.colors.surface,
            animationSpec = tween(200),
            label = "allChipColor"
        )
        val allChipContentColors by animateColorAsState(
            targetValue = if (allSelected) MaterialTheme.colors.onPrimary
                          else MaterialTheme.colors.onSurface,
            animationSpec = tween(200),
            label = "allChipContentColor"
        )
        Chip(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(bottom = 8.dp),
            onClick = { viewModel.toggleFilter(null) },
            colors = ChipDefaults.chipColors(
                backgroundColor = allChipColors,
                contentColor = allChipContentColors
            ),
            label = { Text("All") }
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allGroups.forEach { group ->
                val selected = group.id in selectedMuscleIds
                val chipColors by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colors.primary
                                  else MaterialTheme.colors.surface,
                    animationSpec = tween(200),
                    label = "chipColor_${group.id}"
                )
                val chipContentColors by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colors.onPrimary
                                  else MaterialTheme.colors.onSurface,
                    animationSpec = tween(200),
                    label = "chipContentColor_${group.id}"
                )
                Chip(
                    onClick = { viewModel.toggleFilter(group.id) },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = chipColors,
                        contentColor = chipContentColors
                    ),
                    label = { Text(group.muscleName) }
                )
            }
        }
    }
}
