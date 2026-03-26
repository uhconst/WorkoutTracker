package com.uhc.workouttracker.wear.presentation.notpaired

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.uhc.workouttracker.wear.core.haptic.HapticType
import com.uhc.workouttracker.wear.core.haptic.LocalHapticFeedback
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.uhc.workouttracker.wear.theme.WearTheme

@Composable
fun NotPairedScreen(onRetry: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Open WorkoutTracker on your phone to continue",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Chip(
            modifier = Modifier.fillMaxWidth(),
            onClick = { haptic.perform(HapticType.Confirm); onRetry() },
            colors = ChipDefaults.primaryChipColors(),
            label = { Text("Retry") }
        )
    }
}

@Preview(device = "id:wearos_small_round", showBackground = true)
@Composable
private fun NotPairedScreenPreview() {
    WearTheme {
        NotPairedScreen(onRetry = {})
    }
}
