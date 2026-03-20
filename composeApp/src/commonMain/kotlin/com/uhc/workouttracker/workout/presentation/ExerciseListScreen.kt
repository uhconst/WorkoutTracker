package com.uhc.workouttracker.workout.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.navigation.NavRoute
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.model.WeightLog
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseListScreen(drawerState: DrawerState? = null) {
    val navController = LocalNavController.current

    val viewModel: ExerciseListViewModel = koinViewModel()
    val exercisesGroupedByMuscle by viewModel.filteredExercises.collectAsState()
    val muscles by viewModel.muscles.collectAsState()
    val selectedMuscleIds by viewModel.selectedMuscleIds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExercises()
    }

    ExerciseListLayout(
        exercisesGroupedByMuscle = exercisesGroupedByMuscle,
        muscles = muscles,
        selectedMuscleIds = selectedMuscleIds,
        onMuscleSelected = viewModel::selectMuscleFilter,
        drawerState = drawerState,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExerciseListLayout(
    exercisesGroupedByMuscle: List<MuscleWithExercises> = emptyList(),
    muscles: List<MuscleGroup> = emptyList(),
    selectedMuscleIds: Set<Long> = emptySet(),
    onMuscleSelected: (Long?) -> Unit = {},
    drawerState: DrawerState? = null,
    navController: NavController? = null,
    expandedState: SnapshotStateMap<Long, Boolean> = remember { mutableStateMapOf() }
) {
    WorkoutTrackerAppBar(
        title = "Exercises",
        drawerState = drawerState,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Theme.dimensions.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Theme.dimensions.spacing.small)
            ) {
                item {
                    val allScale by animateFloatAsState(
                        targetValue = if (selectedMuscleIds.isEmpty()) 1.08f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "allChipScale"
                    )
                    FilterChip(
                        modifier = Modifier.graphicsLayer { scaleX = allScale; scaleY = allScale },
                        selected = selectedMuscleIds.isEmpty(),
                        onClick = { onMuscleSelected(null) },
                        label = { Text("All") }
                    )
                }

                items(muscles) { muscle ->
                    val isSelected = muscle.id in selectedMuscleIds
                    val chipScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.08f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "chipScale"
                    )
                    FilterChip(
                        modifier = Modifier.graphicsLayer { scaleX = chipScale; scaleY = chipScale },
                        selected = isSelected,
                        onClick = { onMuscleSelected(muscle.id) },
                        label = { Text(muscle.name) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(Theme.dimensions.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.small),
                modifier = Modifier.fillMaxSize()
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedState[muscleGroup.id] = !isExpanded
                                    }
                                    .padding(Theme.dimensions.spacing.medium)
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

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (muscleGroup.exercises.isEmpty()) {
                                        Text(
                                            text = "No exercises available for this muscle group",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(Theme.dimensions.spacing.small)
                                        )
                                    } else {
                                        muscleGroup.exercises.forEach { exercise ->
                                            ExerciseItem(
                                                exercise = exercise,
                                                navController = navController
                                            )
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
}

@Composable
private fun ExerciseItem(
    exercise: Exercise,
    navController: NavController? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "exerciseCardScale"
    )

    Card(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ) {
                navController?.navigate(NavRoute.AddExerciseDestination(exercise.id)) {
                    launchSingleTop = true
                }
            },
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.dimensions.spacing.medium, vertical = Theme.dimensions.spacing.small),
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

                exercise.weightLogs.lastOrNull()?.let { lastLog ->
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
                MuscleWithExercises(
                    id = 1,
                    muscleName = "Biceps",
                    exercises = listOf(
                        Exercise(
                            id = 1,
                            name = "Barbell Curl",
                            weightLogs = listOf(WeightLog(id = 1, weight = 15f, exerciseId = 1))
                        ),
                        Exercise(
                            id = 2,
                            name = "Dumbbell Curl",
                            weightLogs = listOf(WeightLog(id = 2, weight = 10f, exerciseId = 2))
                        ),
                        Exercise(
                            id = 3,
                            name = "Hammer Curl",
                            weightLogs = listOf(WeightLog(id = 3, weight = 12.5f, exerciseId = 3))
                        )
                    )
                ),
                MuscleWithExercises(
                    id = 2,
                    muscleName = "Triceps",
                    exercises = listOf(
                        Exercise(
                            id = 4,
                            name = "Tricep Pushdown",
                            weightLogs = listOf(WeightLog(id = 4, weight = 20f, exerciseId = 4))
                        ),
                        Exercise(
                            id = 5,
                            name = "Overhead Extension",
                            weightLogs = listOf(WeightLog(id = 5, weight = 8f, exerciseId = 5))
                        )
                    )
                ),
                MuscleWithExercises(
                    id = 3,
                    muscleName = "Chest",
                    exercises = listOf(
                        Exercise(
                            id = 6,
                            name = "Bench Press",
                            weightLogs = listOf(WeightLog(id = 6, weight = 60f, exerciseId = 6))
                        ),
                        Exercise(
                            id = 7,
                            name = "Incline Press",
                            weightLogs = listOf(WeightLog(id = 7, weight = 50f, exerciseId = 7))
                        ),
                        Exercise(
                            id = 8,
                            name = "Chest Fly",
                            weightLogs = listOf(WeightLog(id = 8, weight = 15f, exerciseId = 8))
                        )
                    )
                ),
                MuscleWithExercises(
                    id = 4,
                    muscleName = "Shoulders",
                    exercises = emptyList()
                )
            ),
            muscles = listOf(
                MuscleGroup(id = 1, name = "Biceps"),
                MuscleGroup(id = 2, name = "Triceps"),
                MuscleGroup(id = 3, name = "Chest"),
                MuscleGroup(id = 4, name = "Shoulders")
            ),
            selectedMuscleIds = setOf(1L, 3L),
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
                weightLogs = listOf(WeightLog(id = 1, weight = 15f, exerciseId = 1))
            )
        )
    }
}
