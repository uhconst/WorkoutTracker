package com.uhc.workouttracker.workout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.muscle.data.MuscleGroup
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddExerciseScreen(
    drawerState: DrawerState? = null,
    navController: NavHostController? = null,
) {
    val viewModel: AddExerciseViewModel = koinViewModel()
    val muscles by viewModel.muscles.collectAsState()

    AddExerciseLayout(
        muscleGroups = muscles,
        onSaveExercise = { name, muscleGroupId, weight ->
            viewModel.saveExercise(name, muscleGroupId, weight)
            navController?.popBackStack()
        },
        drawerState = drawerState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseLayout(
    muscleGroups: List<MuscleGroup> = emptyList(),
    onSaveExercise: (name: String, muscleGroupId: Long, weight: Double) -> Unit = { _, _, _ -> },
    drawerState: DrawerState? = null
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    var weight by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var exerciseNameError by remember { mutableStateOf(false) }
    var muscleGroupError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    WorkoutTrackerAppBar(
        title = "Add Exercise",
        drawerState = drawerState,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    } else null
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

            // Exercise Name Input
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = exerciseName,
                onValueChange = {
                    exerciseName = it
                    exerciseNameError = false
                },
                label = { Text("Exercise Name") },
                isError = exerciseNameError,
                supportingText = if (exerciseNameError) {
                    { Text("Please enter an exercise name") }
                } else null
            )

            // Weight Input
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = weight,
                onValueChange = {
                    // Only allow numbers and a single decimal point
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        weight = it
                        weightError = false
                    }
                },
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = weightError,
                supportingText = if (weightError) {
                    { Text("Please enter a valid weight") }
                } else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
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
