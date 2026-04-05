package com.uhc.workouttracker.muscle.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.uhc.workouttracker.core.haptic.HapticType
import com.uhc.workouttracker.core.haptic.LocalHapticFeedback
import com.uhc.workouttracker.core.ui.AnimatedButton
import com.uhc.workouttracker.core.ui.WorkoutTextField
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel.EditState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MuscleGroupsScreen(drawerState: DrawerState? = null) {
    val viewModel: MuscleGroupsViewModel = koinViewModel()

    val muscles by viewModel.muscles.collectAsState()
    val editState by viewModel.editState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.error.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    MuscleGroupsLayout(
        muscleGroups = muscles,
        editState = editState,
        drawerState = drawerState,
        snackbarHostState = snackbarHostState,
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
internal fun MuscleGroupsLayout(
    muscleGroups: List<MuscleGroup> = emptyList(),
    editState: EditState = EditState.NotEditing,
    drawerState: DrawerState? = null,
    snackbarHostState: SnackbarHostState? = null,
    onAddMuscleGroupClick: (String) -> Unit = {},
    onEditClick: (MuscleGroup) -> Unit = {},
    onUpdateMuscleGroupClick: (String) -> Unit = {},
    onCancelEditClick: () -> Unit = {},
    onDeleteClick: (MuscleGroup) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    var inputText by remember { mutableStateOf("") }
    var muscleGroupToDelete by remember { mutableStateOf<MuscleGroup?>(null) }

    val isEditing = editState is EditState.Editing
    val currentlyEditedMuscle = (editState as? EditState.Editing)?.muscleGroup

    // Update input text when edit state changes
    LaunchedEffect(editState) {
        if (editState is EditState.Editing) {
            inputText = editState.muscleGroup.name
        } else {
            inputText = ""
        }
    }

    WorkoutTrackerAppBar(
        title = "Muscle Groups",
        drawerState = drawerState,
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = Theme.dimensions.spacing.medium)
            ) {
            WorkoutTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = if (isEditing) "Edit Muscle group name" else "Add Muscle group name",
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.dimensions.spacing.small),
                onClick = {
                    haptic.perform(HapticType.Confirm)
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
            } // end input section Column

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
                    val animatedCardColor by animateColorAsState(
                        targetValue = if (isCurrentlyEdited)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface,
                        animationSpec = tween(durationMillis = 300),
                        label = "cardColor"
                    )

                    Card(
                        modifier = Modifier
                            .animateItem()
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = animatedCardColor)
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
                                    onClick = { muscleGroupToDelete = muscle }
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

        muscleGroupToDelete?.let { muscle ->
            AlertDialog(
                onDismissRequest = { muscleGroupToDelete = null },
                title = { Text("Delete Muscle Group") },
                text = { Text("Delete \"${muscle.name}\"? All workouts linked to this muscle group will also be deleted. This cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        haptic.perform(HapticType.Warning)
                        onDeleteClick(muscle)
                        muscleGroupToDelete = null
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { muscleGroupToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
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