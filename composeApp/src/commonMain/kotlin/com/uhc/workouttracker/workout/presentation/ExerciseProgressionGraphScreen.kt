package com.uhc.workouttracker.workout.presentation

import com.uhc.workouttracker.core.util.format1d
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uhc.workouttracker.core.theme.Theme
import com.uhc.workouttracker.core.theme.dimensions
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.workout.domain.model.WeightLog
import kotlin.math.floor
import kotlin.math.hypot
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExerciseProgressionGraphScreen(
    exerciseId: Long,
    exerciseName: String
) {
    val navController = LocalNavController.current
    val viewModel: ExerciseProgressionGraphViewModel = koinViewModel()
    val weightLogs by viewModel.weightLogs.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(exerciseId) {
        viewModel.loadWeightLogs(exerciseId)
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect { message ->
            message?.let { snackbarHostState.showSnackbar(it) }
        }
    }

    ExerciseProgressionGraphLayout(
        exerciseName = exerciseName,
        weightLogs = weightLogs,
        snackbarHostState = snackbarHostState,
        onBack = { navController?.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExerciseProgressionGraphLayout(
    exerciseName: String = "",
    weightLogs: List<WeightLog> = emptyList(),
    snackbarHostState: SnackbarHostState? = null,
    onBack: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exerciseName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(Theme.dimensions.spacing.small)
        ) {
            if (weightLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Theme.dimensions.spacing.large),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data yet — log this exercise to see your progress",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "Progress over time",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = Theme.dimensions.spacing.medium,
                        vertical = Theme.dimensions.spacing.small
                    )
                )
                LineChart(
                    weightLogs = weightLogs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimensions.spacing.small)
                )
            }
        }
    }
}

@Composable
private fun LineChart(
    weightLogs: List<WeightLog>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val textMeasurer = rememberTextMeasurer()
    val yLabelStyle = TextStyle(color = onSurfaceVariantColor, fontSize = 10.sp)
    val xLabelStyle = TextStyle(color = onSurfaceVariantColor.copy(alpha = 0.7f), fontSize = 10.sp)

    val pointSpacingDp = 60.dp
    val chartHeightDp = 220.dp
    val yAxisWidthDp = 52.dp
    val paddingTopDp = 20.dp
    val paddingBottomDp = 44.dp
    val paddingStartDp = 8.dp
    val paddingEndDp = 16.dp

    val maxWeight = weightLogs.maxOf { it.weight }
    val (yMax, ticks) = remember(maxWeight) { computeYAxis(maxWeight) }

    BoxWithConstraints(modifier = modifier.height(chartHeightDp)) {
        val chartAreaWidth: Dp = maxWidth - yAxisWidthDp
        val requiredWidth: Dp = pointSpacingDp * weightLogs.size + paddingStartDp + paddingEndDp
        val canvasWidth: Dp = if (requiredWidth > chartAreaWidth) requiredWidth else chartAreaWidth
        val scrollState = rememberScrollState()

        Row(modifier = Modifier.fillMaxSize()) {

            // Fixed Y-axis column
            Canvas(
                modifier = Modifier
                    .width(yAxisWidthDp)
                    .height(chartHeightDp)
            ) {
                val bpx = paddingBottomDp.toPx()
                val drawH = size.height - paddingTopDp.toPx() - bpx

                ticks.forEach { tick ->
                    val y = size.height - bpx - (tick / yMax) * drawH
                    val label = "${tick.format1d()} kg"
                    val measured: TextLayoutResult = textMeasurer.measure(label, yLabelStyle)
                    val tx = size.width - measured.size.width - 6.dp.toPx()
                    val ty = y - measured.size.height / 2f
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        topLeft = Offset(tx, ty),
                        style = yLabelStyle
                    )
                }
            }

            // Scrollable chart column
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(chartHeightDp)
                    .horizontalScroll(scrollState)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(canvasWidth)
                        .height(chartHeightDp)
                        .pointerInput(weightLogs, yMax) {
                            detectTapGestures { offset ->
                                val ppx = pointSpacingDp.toPx()
                                val spx = paddingStartDp.toPx()
                                val tpx = paddingTopDp.toPx()
                                val bpx = paddingBottomDp.toPx()
                                val drawH = size.height - tpx - bpx
                                var found = -1
                                weightLogs.forEachIndexed { i, log ->
                                    val cx = spx + i * ppx
                                    val cy = size.height - bpx - (log.weight / yMax) * drawH
                                    if (hypot(offset.x - cx, offset.y - cy) < 40f) found = i
                                }
                                selectedIndex = if (found == -1 || found == selectedIndex) null else found
                            }
                        }
                ) {
                    val ppx = pointSpacingDp.toPx()
                    val spx = paddingStartDp.toPx()
                    val bpx = paddingBottomDp.toPx()
                    val drawH = size.height - paddingTopDp.toPx() - bpx

                    fun cx(i: Int) = spx + i * ppx
                    fun cy(w: Float) = size.height - bpx - (w / yMax) * drawH

                    // Horizontal gridlines
                    ticks.forEach { tick ->
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, cy(tick)),
                            end = Offset(size.width, cy(tick)),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Connecting lines
                    for (i in 0 until weightLogs.size - 1) {
                        drawLine(
                            color = primaryColor.copy(alpha = 0.7f),
                            start = Offset(cx(i), cy(weightLogs[i].weight)),
                            end = Offset(cx(i + 1), cy(weightLogs[i + 1].weight)),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Dots
                    weightLogs.forEachIndexed { i, log ->
                        val x = cx(i)
                        val y = cy(log.weight)
                        val isLast = i == weightLogs.size - 1
                        val isSelected = i == selectedIndex

                        if (isLast || isSelected) {
                            drawCircle(
                                color = primaryColor.copy(alpha = 0.25f),
                                radius = 14.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                        drawCircle(
                            color = if (isLast) primaryColor else dotColor.copy(alpha = 0.8f),
                            radius = if (isLast) 7.dp.toPx() else 5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    // X-axis date labels
                    weightLogs.forEachIndexed { i, log ->
                        val label = shortDate(log.date)
                        if (label.isEmpty()) return@forEachIndexed
                        val measured = textMeasurer.measure(label, xLabelStyle)
                        val tx = cx(i) - measured.size.width / 2f
                        val ty = size.height - measured.size.height - 4.dp.toPx()
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = Offset(tx, ty),
                            style = xLabelStyle
                        )
                    }
                }

                // Tooltip (unchanged)
                selectedIndex?.let { idx ->
                    val log = weightLogs[idx]
                    val drawH: Dp = chartHeightDp - paddingTopDp - paddingBottomDp
                    val normalizedY = log.weight / yMax
                    val pointYDp: Dp = chartHeightDp - paddingBottomDp - drawH * normalizedY
                    val tooltipYDp: Dp = (pointYDp - 72.dp).coerceAtLeast(4.dp)
                    val tooltipXDp: Dp = (paddingStartDp + pointSpacingDp * idx - 56.dp).coerceAtLeast(4.dp)

                    Card(
                        modifier = Modifier.offset(x = tooltipXDp, y = tooltipYDp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${log.weight.format1d()} kg",
                                style = MaterialTheme.typography.labelLarge,
                                color = primaryColor
                            )
                            if (log.date.isNotEmpty()) {
                                Text(
                                    text = formatDate(log.date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onSurfaceColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Returns (yMax, ticks) where yMax is always strictly above max and ticks always start at 0.
// yMax = (floor(max/step) + 1) * step guarantees one full nice-step of headroom above the data.
private fun computeYAxis(max: Float, count: Int = 4): Pair<Float, List<Float>> {
    val range = max.coerceAtLeast(0.1f)
    val roughStep = range / count
    val niceSteps = listOf(0.5f, 1f, 2f, 2.5f, 5f, 10f, 25f, 50f, 100f, 250f, 500f)
    val step = niceSteps.firstOrNull { it >= roughStep } ?: roughStep
    val yMax = (floor((max / step).toDouble()).toFloat() + 1f) * step
    val ticks = mutableListOf<Float>()
    var tick = 0f
    while (tick <= yMax + step * 0.001f) {
        ticks.add(tick)
        tick += step
    }
    return Pair(yMax, ticks)
}


private fun shortDate(isoDate: String): String {
    if (isoDate.length < 10) return ""
    val parts = isoDate.substring(0, 10).split("-")
    if (parts.size < 3) return ""
    val month = parts[1].toIntOrNull() ?: return ""
    val day = parts[2].trimStart('0').ifEmpty { "0" }
    val monthName = when (month) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
        9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> return ""
    }
    return "$day $monthName"
}

private fun formatDate(isoDate: String): String {
    if (isoDate.length < 10) return isoDate
    val parts = isoDate.substring(0, 10).split("-")
    if (parts.size < 3) return isoDate
    val year = parts[0]
    val month = parts[1].toIntOrNull() ?: return isoDate
    val day = parts[2].trimStart('0').ifEmpty { "0" }
    val monthName = when (month) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
        9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> return isoDate
    }
    return "$day $monthName $year"
}
