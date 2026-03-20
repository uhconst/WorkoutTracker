package com.uhc.workouttracker.workout.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import com.uhc.workouttracker.core.ui.AnimatedButton
import com.uhc.workouttracker.core.ui.WorkoutTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.workout.domain.model.Exercise
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddExerciseScreen(
    drawerState: DrawerState? = null,
    exerciseId: Long? = null
) {
    val navController = LocalNavController.current
    val viewModel: AddExerciseViewModel = koinViewModel()
    val muscles by viewModel.muscles.collectAsState()
    val editingExercise by viewModel.editingExercise.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(exerciseId) {
        exerciseId?.let { viewModel.setExerciseToEdit(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect { message ->
            snackbarHostState.showSnackbar(message)
            navController?.popBackStack()
        }
    }

    AddExerciseLayout(
        muscleGroups = muscles,
        exercise = editingExercise,
        onSaveExercise = { name, muscleGroupId, weight ->
            viewModel.saveExercise(name, muscleGroupId, weight)
        },
        drawerState = drawerState,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseLayout(
    muscleGroups: List<MuscleGroup> = emptyList(),
    exercise: Exercise? = null,
    onSaveExercise: (name: String, muscleGroupId: Long, weight: Double) -> Unit = { _, _, _ -> },
    drawerState: DrawerState? = null,
    snackbarHostState: SnackbarHostState? = null
) {
    var exerciseName by remember(exercise) { mutableStateOf(exercise?.name ?: "") }
    var selectedMuscleGroup by remember(exercise, muscleGroups) {
        mutableStateOf(
            exercise?.muscleGroupId?.let { id ->
                muscleGroups.find { it.id == id }
            }
        )
    }
    var weight by remember(exercise) {
        mutableStateOf(
            exercise?.weightLogs?.firstOrNull()?.weight?.toString() ?: ""
        )
    }
    var expanded by remember { mutableStateOf(false) }

    var exerciseNameError by remember { mutableStateOf(false) }
    var muscleGroupError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    val isEditing = exercise != null

    WorkoutTrackerAppBar(
        title = if (isEditing) "Edit Exercise" else "Add Exercise",
        drawerState = drawerState,
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimensions.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        value = selectedMuscleGroup?.name ?: "",
                        onValueChange = {},
                        label = { Text("Muscle Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        isError = muscleGroupError,
                        supportingText = if (muscleGroupError) {
                            { Text("Please select a muscle group") }
                        } else null,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        muscleGroups.forEach { muscleGroup ->
                            DropdownMenuItem(
                                text = { Text(muscleGroup.name) },
                                onClick = {
                                    selectedMuscleGroup = muscleGroup
                                    expanded = false
                                    muscleGroupError = false
                                }
                            )
                        }
                    }
                }

                WorkoutTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.dimensions.spacing.medium),
                    value = exerciseName,
                    onValueChange = {
                        exerciseName = it
                        exerciseNameError = false
                    },
                    label = "Exercise Name",
                    isError = exerciseNameError,
                    supportingText = if (exerciseNameError) {
                        { Text("Please enter an exercise name") }
                    } else null
                )

                WorkoutTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.dimensions.spacing.medium),
                    value = weight,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            weight = it
                            weightError = false
                        }
                    },
                    label = "Weight (kg)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = weightError,
                    supportingText = if (weightError) {
                        { Text("Please enter a valid weight") }
                    } else null
                )
            }

            AnimatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(Theme.dimensions.spacing.medium),
                onClick = {
                    exerciseNameError = exerciseName.isBlank()
                    muscleGroupError = selectedMuscleGroup == null
                    weightError = weight.isBlank() || weight.toDoubleOrNull() == null

                    if (!exerciseNameError && !muscleGroupError && !weightError) {
                        onSaveExercise(
                            exerciseName,
                            selectedMuscleGroup!!.id,
                            weight.toDouble()
                        )
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}

@Preview
@Composable
fun AddExerciseScreenPreview() {
    WorkoutTrackerTheme {
        AddExerciseLayout(
            muscleGroups = listOf(
                MuscleGroup(id = 1, name = "Chest"),
                MuscleGroup(id = 2, name = "Back"),
                MuscleGroup(id = 3, name = "Legs")
            ),
            onSaveExercise = { _, _, _ -> },
            drawerState = null
        )
    }
}
