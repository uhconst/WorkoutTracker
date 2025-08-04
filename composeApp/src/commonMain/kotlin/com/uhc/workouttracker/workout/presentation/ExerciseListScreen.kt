package com.uhc.workouttracker.workout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uhc.workouttracker.workout.data.Exercises
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseListScreen() {
    val viewModel: ExerciseListViewModel = koinViewModel()

    val exercises by viewModel.exercises.collectAsState()

    ExerciseListLayout(exercises = exercises)
}

@Composable
private fun ExerciseListLayout(exercises: List<Exercises> = emptyList()) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = exercises,
            key = { it.id }
        ) { muscle ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .animateItem()
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    text = muscle.name
                )
            }
        }
    }
}

@Preview
@Composable
private fun WorkoutListPreview() {
    ExerciseListLayout(
        exercises = listOf(
            Exercises(
                id = 1,
                name = "Biceps"
            ),
            Exercises(
                id = 2,
                name = "Triceps"
            ),
            Exercises(
                id = 3,
                name = "Chest"
            )
        )
    )
}