package com.uhc.workouttracker.workout.presentation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.workout.data.Exercises
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseListScreen(drawerState: DrawerState? = null) {
    val viewModel: ExerciseListViewModel = koinViewModel()
    val exercises by viewModel.exercises.collectAsState()
    ExerciseListLayout(exercises = exercises, drawerState = drawerState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseListLayout(exercises: List<Exercises> = emptyList(), drawerState: DrawerState? = null) {
    WorkoutTrackerAppBar(
        title = "Exercises",
        drawerState = drawerState,
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(
                items = exercises,
                key = { it.id }
            ) { exercise ->
                Card(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            text = exercise.name
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WorkoutListPreview() {
    WorkoutTrackerTheme {
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
}