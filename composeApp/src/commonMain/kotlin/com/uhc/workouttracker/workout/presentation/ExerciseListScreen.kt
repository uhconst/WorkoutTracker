package com.uhc.workouttracker.workout.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.workout.data.Exercise
import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import com.uhc.workouttracker.workout.data.WeightLogs
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseListScreen(drawerState: DrawerState? = null) {
    val viewModel: ExerciseListViewModel = koinViewModel()
    val exercisesGroupedByMuscle by viewModel.exercisesGroupedByMuscle.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExercises()
    }
    
    ExerciseListLayout(
        exercisesGroupedByMuscle = exercisesGroupedByMuscle,
        drawerState = drawerState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseListLayout(
    exercisesGroupedByMuscle: List<MuscleGroupsWithExercises> = emptyList(),
    drawerState: DrawerState? = null,
    expandedState: SnapshotStateMap<Long, Boolean> = remember { mutableStateMapOf() }
) {
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
                items = exercisesGroupedByMuscle,
                key = { it.id }
            ) { muscleGroup ->
                val isExpanded = expandedState[muscleGroup.id] ?: false
                val rotationState by animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f
                )

                Card(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Muscle group header (always visible)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedState[muscleGroup.id] = !isExpanded
                                }
                                .padding(16.dp)
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                text = muscleGroup.muscleName
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                modifier = Modifier.rotate(rotationState)
                            )
                        }

                        // Exercises list (visible only when expanded)
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (muscleGroup.exercises.isNullOrEmpty()) {
                                    Text(
                                        text = "No exercises available for this muscle group",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    muscleGroup.exercises.forEach { exercise ->
                                        ExerciseItem(exercise = exercise)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseItem(exercise: Exercise) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                // Navigation will be implemented later
            },
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodyLarge
                )

                exercise.weightLogs?.lastOrNull()?.let { lastLog ->
                    Text(
                        text = "${lastLog.weight} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View Exercise Details",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
private fun WorkoutListPreview() {
    WorkoutTrackerTheme {
        ExerciseListLayout(
            exercisesGroupedByMuscle = listOf(
                MuscleGroupsWithExercises(
                    id = 1,
                    muscleName = "Biceps",
                    exercises = listOf(
                        Exercise(
                            id = 1,
                            name = "Barbell Curl",
                            weightLogs = listOf(WeightLogs(id = 1, weight = 15f))
                        ),
                        Exercise(
                            id = 2,
                            name = "Dumbbell Curl",
                            weightLogs = listOf(WeightLogs(id = 2, weight = 10f))
                        ),
                        Exercise(
                            id = 3,
                            name = "Hammer Curl",
                            weightLogs = listOf(WeightLogs(id = 3, weight = 12.5f))
                        )
                    )
                ),
                MuscleGroupsWithExercises(
                    id = 2,
                    muscleName = "Triceps",
                    exercises = listOf(
                        Exercise(
                            id = 4,
                            name = "Tricep Pushdown",
                            weightLogs = listOf(WeightLogs(id = 4, weight = 20f))
                        ),
                        Exercise(
                            id = 5,
                            name = "Overhead Extension",
                            weightLogs = listOf(WeightLogs(id = 5, weight = 8f))
                        )
                    )
                ),
                MuscleGroupsWithExercises(
                    id = 3,
                    muscleName = "Chest",
                    exercises = listOf(
                        Exercise(
                            id = 6,
                            name = "Bench Press",
                            weightLogs = listOf(WeightLogs(id = 6, weight = 60f))
                        ),
                        Exercise(
                            id = 7,
                            name = "Incline Press",
                            weightLogs = listOf(WeightLogs(id = 7, weight = 50f))
                        ),
                        Exercise(
                            id = 8,
                            name = "Chest Fly",
                            weightLogs = listOf(WeightLogs(id = 8, weight = 15f))
                        )
                    )
                ),
                MuscleGroupsWithExercises(
                    id = 4,
                    muscleName = "Shoulders",
                    exercises = emptyList()
                )
            ),
            expandedState = remember { mutableStateMapOf(1L to true) }
        )
    }
}

@Preview
@Composable
private fun ExerciseItemPreview() {
    WorkoutTrackerTheme {
        ExerciseItem(
            exercise = Exercise(
                id = 1,
                name = "Barbell Curl",
                weightLogs = listOf(WeightLogs(id = 1, weight = 15f))
            )
        )
    }
}