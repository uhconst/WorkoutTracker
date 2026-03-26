package com.uhc.workouttracker.workout.presentation

import kotlin.text.format
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uhc.workouttracker.core.haptic.HapticType
import com.uhc.workouttracker.core.haptic.LocalHapticFeedback
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.core.ui.AnimatedButton
import com.uhc.workouttracker.core.ui.WorkoutTextField
import com.uhc.workouttracker.core.ui.WorkoutTrackerAppBar
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.WeightLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val isEditing = exerciseId != null
    var saveKey by remember { mutableStateOf(0) }

    LaunchedEffect(exerciseId) {
        exerciseId?.let { viewModel.setExerciseToEdit(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect {
            if (isEditing) {
                navController?.popBackStack()
            } else {
                saveKey++
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.saveError.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    AddExerciseLayout(
        muscleGroups = muscles,
        exercise = editingExercise,
        saveKey = saveKey,
        onSaveExercise = { name, muscleGroupId, weight ->
            viewModel.saveExercise(name, muscleGroupId, weight)
        },
        onViewExercises = if (!isEditing) {
            { navController?.navigate(com.uhc.workouttracker.navigation.NavRoute.WorkoutListDestination) { launchSingleTop = true } }
        } else null,
        drawerState = drawerState,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseLayout(
    muscleGroups: List<MuscleGroup> = emptyList(),
    exercise: Exercise? = null,
    saveKey: Int = 0,
    onSaveExercise: (name: String, muscleGroupId: Long, weight: Double) -> Unit = { _, _, _ -> },
    onViewExercises: (() -> Unit)? = null,
    drawerState: DrawerState? = null,
    snackbarHostState: SnackbarHostState? = null
) {
    var exerciseName by remember(exercise, saveKey) { mutableStateOf(exercise?.name ?: "") }
    var selectedMuscleGroup by remember(exercise, muscleGroups, saveKey) {
        mutableStateOf(
            exercise?.muscleGroupId?.let { id ->
                muscleGroups.find { it.id == id }
            }
        )
    }
    var weight by remember(exercise, saveKey) {
        mutableStateOf(
            exercise?.weightLogs?.firstOrNull()?.weight?.let { "%.2f".format(it) } ?: ""
        )
    }
    var expanded by remember { mutableStateOf(false) }

    var exerciseNameError by remember { mutableStateOf(false) }
    var muscleGroupError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    // Animation state
    var shineBorder by remember { mutableStateOf(false) }
    var fieldsVisible by remember { mutableStateOf(true) }
    var glowButton by remember { mutableStateOf(false) }
    var orbVisible by remember { mutableStateOf(false) }
    val orbProgress = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary

    val shineColor by animateColorAsState(
        targetValue = if (shineBorder) primaryColor else Color.Transparent,
        animationSpec = tween(300),
        label = "shineColor"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (glowButton) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )
    val buttonGlowAlpha by animateFloatAsState(
        targetValue = if (glowButton) 1f else 0f,
        animationSpec = tween(300),
        label = "buttonGlowAlpha"
    )
    val buttonContainerColor by animateColorAsState(
        targetValue = if (glowButton) primaryColor.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(300),
        label = "buttonContainerColor"
    )

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(saveKey) {
        if (saveKey > 0) {
            shineBorder = true
            delay(80)
            fieldsVisible = false
            orbVisible = true
            orbProgress.snapTo(0f)

            launch {
                haptic.perform(HapticType.ImpactLight)
                delay(125)
                haptic.perform(HapticType.ImpactLight)
                delay(125)
                haptic.perform(HapticType.ImpactMedium)
                delay(125)
                haptic.perform(HapticType.ImpactMedium)
                delay(125)
                haptic.perform(HapticType.ImpactStrong)
            }

            orbProgress.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            orbVisible = false
            glowButton = true
            delay(250)
            fieldsVisible = true
            delay(750)
            glowButton = false
            shineBorder = false
        }
    }

    fun adjustWeight(current: String, delta: Double): String {
        val parsed = current.toDoubleOrNull() ?: 0.0
        val result = (parsed + delta).coerceAtLeast(0.0)
        return "%.2f".format(result)
    }

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
            AnimatedVisibility(
                visible = fieldsVisible,
                enter = expandVertically(animationSpec = tween(250), expandFrom = Alignment.CenterVertically) + fadeIn(tween(200)),
                exit = shrinkVertically(animationSpec = tween(350), shrinkTowards = Alignment.CenterVertically) + fadeOut(tween(300))
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
                                .menuAnchor()
                                .shineBorder(shineColor),
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
                            .padding(top = Theme.dimensions.spacing.medium)
                            .shineBorder(shineColor),
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.dimensions.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(onClick = { weight = adjustWeight(weight, -1.0); weightError = false; haptic.perform(HapticType.Selection) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease weight")
                        }
                        WorkoutTextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = Theme.dimensions.spacing.small)
                                .shineBorder(shineColor),
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
                        FilledIconButton(onClick = { weight = adjustWeight(weight, 1.0); weightError = false; haptic.perform(HapticType.Selection) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase weight")
                        }
                    }
                }
            }

            if (orbVisible) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val orbY = size.height * 0.22f + (size.height * 0.66f) * orbProgress.value
                    val orbAlpha = 1f - orbProgress.value * 0.3f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryColor.copy(alpha = orbAlpha * 0.4f), Color.Transparent),
                            center = Offset(size.width / 2f, orbY),
                            radius = 40.dp.toPx()
                        ),
                        radius = 40.dp.toPx(),
                        center = Offset(size.width / 2f, orbY)
                    )
                    drawCircle(
                        color = primaryColor.copy(alpha = orbAlpha * 0.9f),
                        radius = 14.dp.toPx(),
                        center = Offset(size.width / 2f, orbY)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(Theme.dimensions.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.small)
            ) {
                AnimatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        exerciseNameError = exerciseName.isBlank()
                        muscleGroupError = selectedMuscleGroup == null
                        weightError = weight.isBlank() || weight.toDoubleOrNull() == null

                        if (!exerciseNameError && !muscleGroupError && !weightError) {
                            haptic.perform(HapticType.Confirm)
                            onSaveExercise(
                                exerciseName,
                                selectedMuscleGroup!!.id,
                                weight.toDouble()
                            )
                        } else {
                            haptic.perform(HapticType.Reject)
                        }
                    }
                ) {
                    Text("Save")
                }
                if (onViewExercises != null) {
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale }
                            .border(
                                width = 2.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        primaryColor.copy(alpha = buttonGlowAlpha),
                                        Color.White.copy(alpha = buttonGlowAlpha * 0.7f),
                                        primaryColor.copy(alpha = buttonGlowAlpha)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        colors = ButtonDefaults.textButtonColors(containerColor = buttonContainerColor),
                        onClick = onViewExercises
                    ) {
                        Text("View Exercises")
                    }
                }
            }
        }
    }
}

private fun Modifier.shineBorder(color: Color): Modifier = this.drawWithContent {
    drawContent()
    drawRoundRect(
        color = color,
        cornerRadius = CornerRadius(16.dp.toPx()),
        style = Stroke(width = 2.dp.toPx())
    )
}

private val previewMuscleGroups = listOf(
    MuscleGroup(id = 1, name = "Chest"),
    MuscleGroup(id = 2, name = "Back"),
    MuscleGroup(id = 3, name = "Legs")
)

@Preview
@Composable
private fun AddExercisePreview() {
    WorkoutTrackerTheme {
        AddExerciseLayout(
            muscleGroups = previewMuscleGroups,
            onSaveExercise = { _, _, _ -> },
            onViewExercises = {}
        )
    }
}

@Preview
@Composable
private fun AddExerciseEditPreview() {
    WorkoutTrackerTheme {
        AddExerciseLayout(
            muscleGroups = previewMuscleGroups,
            exercise = Exercise(
                id = 1,
                name = "Bench Press",
                muscleGroupId = 1,
                weightLogs = listOf(WeightLog(id = 1, weight = 80f, exerciseId = 1))
            ),
            onSaveExercise = { _, _, _ -> }
        )
    }
}

@Preview
@Composable
private fun AddExerciseGlowPreview() {
    WorkoutTrackerTheme {
        AddExerciseLayout(
            muscleGroups = previewMuscleGroups,
            onViewExercises = {}
        )
    }
}
