package com.uhc.workouttracker.muscle.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.muscle.data.MuscleGroup
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel.EditState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MuscleGroupsScreen(drawerState: DrawerState? = null) {
    val viewModel: MuscleGroupsViewModel = koinViewModel()

    val muscles by viewModel.muscles.collectAsState()
    val editState by viewModel.editState.collectAsState()

    MuscleGroupsLayout(
        muscleGroups = muscles,
        editState = editState,
        drawerState = drawerState,
        onAddMuscleGroupClick = { muscleName ->
            viewModel.addMuscleGroup(muscleName)
        },
        onEditClick = { muscleGroup ->
            viewModel.startEditing(muscleGroup)
        },
        onUpdateMuscleGroupClick = { newName ->
            viewModel.updateMuscleGroup(newName)
        },
        onCancelEditClick = {
            viewModel.cancelEditing()
        },
        onDeleteClick = { muscleGroup ->
            viewModel.deleteMuscleGroup(muscleGroup)
        }
    )
}

@Composable
private fun MuscleGroupsLayout(
    muscleGroups: List<MuscleGroup> = emptyList(),
    editState: EditState = EditState.NotEditing,
    drawerState: DrawerState? = null,
    onAddMuscleGroupClick: (String) -> Unit = {},
    onEditClick: (MuscleGroup) -> Unit = {},
    onUpdateMuscleGroupClick: (String) -> Unit = {},
    onCancelEditClick: () -> Unit = {},
    onDeleteClick: (MuscleGroup) -> Unit = {},
) {
    var inputText by remember { mutableStateOf("") }

    // Update input text when edit state changes
    val isEditing = editState is EditState.Editing
    val currentlyEditedMuscle = if (editState is EditState.Editing) editState.muscleGroup else null

    // Set input text when editing starts
    if (isEditing && currentlyEditedMuscle != null && inputText.isEmpty()) {
        inputText = currentlyEditedMuscle.name
    }

    // Clear input text when editing is done
    if (!isEditing && inputText.isNotEmpty()) {
        inputText = ""
    }

    WorkoutTrackerAppBar(
        title = "Muscle Groups",
        drawerState = drawerState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text(if (isEditing) "Edit Muscle group name" else "Add Muscle group name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.dimensions.spacing.small),
                onClick = {
                    if (isEditing) {
                        onUpdateMuscleGroupClick(inputText)
                    } else {
                        onAddMuscleGroupClick(inputText)
                        inputText = ""
                    }
                }
            ) {
                Text(if (isEditing) "Update Muscle Group" else "Add Muscle Group")
            }

            LazyColumn(
                contentPadding = PaddingValues(vertical = Theme.dimensions.spacing.xLarge),
                verticalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.small),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = muscleGroups,
                    key = { it.id }
                ) { muscle ->
                    val isCurrentlyEdited = currentlyEditedMuscle?.id == muscle.id

                    Card(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth(),
                        colors = if (isCurrentlyEdited)
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        else
                            CardDefaults.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Theme.dimensions.spacing.medium)
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                style = MaterialTheme.typography.titleMedium,
                                text = muscle.name,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(Theme.dimensions.spacing.small))

                            IconButton(
                                onClick = {
                                    if (isCurrentlyEdited) {
                                        onCancelEditClick()
                                    } else {
                                        onEditClick(muscle)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = if (isCurrentlyEdited) "Cancel Edit" else "Edit Muscle Group",
                                    tint = if (isCurrentlyEdited) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }

                            // Delete button (only shown when not editing)
                            if (!isCurrentlyEdited) {
                                IconButton(
                                    onClick = { onDeleteClick(muscle) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Muscle Group",
                                        tint = MaterialTheme.colorScheme.error
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

@Preview
@Composable
private fun MuscleGroupsEditingPreview() {
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
            ),
            editState = EditState.Editing(
                MuscleGroup(
                    id = 2,
                    name = "Triceps"
                )
            )
        )
    }
}