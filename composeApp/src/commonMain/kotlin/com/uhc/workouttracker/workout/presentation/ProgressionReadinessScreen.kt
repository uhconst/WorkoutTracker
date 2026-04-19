package com.uhc.workouttracker.workout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProgressionReadinessScreen(
    exerciseId: Long,
    exerciseName: String
) {
    val navController = LocalNavController.current
    val viewModel: ProgressionReadinessViewModel = koinViewModel()
    val current by viewModel.current.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(exerciseId) {
        viewModel.load(exerciseId)
    }

    LaunchedEffect(Unit) {
        viewModel.saved.collect {
            navController?.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ProgressionReadinessLayout(
        exerciseName = exerciseName,
        current = current,
        onSelect = viewModel::select,
        onBack = { navController?.popBackStack() },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProgressionReadinessLayout(
    exerciseName: String = "",
    current: ProgressionReadiness = ProgressionReadiness.ON_TRACK,
    onSelect: (ProgressionReadiness) -> Unit = {},
    onBack: () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progression Readiness") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Theme.dimensions.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.medium)
        ) {
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "How did this exercise feel?",
                style = MaterialTheme.typography.bodyLarge
            )

            ProgressionReadiness.entries.forEach { readiness ->
                ProgressionOptionCard(
                    readiness = readiness,
                    isSelected = current == readiness,
                    onClick = { onSelect(readiness) }
                )
            }
        }
    }
}

@Composable
private fun ProgressionOptionCard(
    readiness: ProgressionReadiness,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector
    val iconTint: Color
    val label: String
    val description: String

    when (readiness) {
        ProgressionReadiness.ON_TRACK -> {
            icon = Icons.Default.CheckCircle
            iconTint = MaterialTheme.colorScheme.primary
            label = "On Track"
            description = "Weight feels right, no change needed"
        }
        ProgressionReadiness.INCREASE_WEIGHT -> {
            icon = Icons.Default.ArrowUpward
            iconTint = Color(0xFF2E7D32)
            label = "Increase Weight"
            description = "Felt easy, ready to bump the weight up"
        }
        ProgressionReadiness.REDUCE_WEIGHT -> {
            icon = Icons.Default.ArrowDownward
            iconTint = Color(0xFFC62828)
            label = "Reduce Weight"
            description = "Felt too heavy, consider lowering the weight"
        }
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder()
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.dimensions.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.medium)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProgressionReadinessPreview() {
    WorkoutTrackerTheme {
        ProgressionReadinessLayout(
            exerciseName = "Bench Press",
            current = ProgressionReadiness.INCREASE_WEIGHT
        )
    }
}
