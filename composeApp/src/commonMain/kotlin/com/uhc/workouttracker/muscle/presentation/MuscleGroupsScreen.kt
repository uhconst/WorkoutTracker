package com.uhc.workouttracker.muscle.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.muscle.data.MuscleGroup
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MuscleGroupsScreen() {
    val viewModel: MuscleGroupsViewModel = koinViewModel()

    val muscles by viewModel.muscles.collectAsState()

    MuscleGroupsLayout(muscleGroups = muscles)
}

@Composable
private fun MuscleGroupsLayout(muscleGroups: List<MuscleGroup> = emptyList()) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = /*exerciseName*/"",
            onValueChange = {} /*onExerciseChanged*/,
            label = { Text("Muscle group name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = { }
        ) {
            Text("Add Muscle Group"/*stringResource(R.string.addMuscleGroupBtn)*/)
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = muscleGroups,
                key = { it.id }
            ) { muscle ->
                Card(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp)
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = muscle.name
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MuscleGroupsPreview() {
    WorkoutTrackerTheme {
        MuscleGroupsLayout(
            muscleGroups = listOf(
                MuscleGroup(
                    id = 1,
                    name = "Biceps"
                ),
                MuscleGroup(
                    id = 2,
                    name = "Triceps"
                ),
                MuscleGroup(
                    id = 3,
                    name = "Chest"
                )
            )
        )
    }
}