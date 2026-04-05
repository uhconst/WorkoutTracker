package com.uhc.workouttracker.core.sync

import com.uhc.workouttracker.muscle.data.dto.MuscleGroupDto
import com.uhc.workouttracker.muscle.data.local.MuscleGroupLocalDataSource
import com.uhc.workouttracker.muscle.data.mapper.toDomain
import com.uhc.workouttracker.workout.data.dto.MuscleWithExercisesDto
import com.uhc.workouttracker.workout.data.local.ExerciseLocalDataSource
import com.uhc.workouttracker.workout.data.mapper.toDomain
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(SupabaseExperimental::class)
class ExerciseSyncManager(
    private val client: SupabaseClient,
    private val muscleGroupLocal: MuscleGroupLocalDataSource,
    private val exerciseLocal: ExerciseLocalDataSource,
    private val applicationScope: CoroutineScope
) : SyncManager {

    init {
        applicationScope.launch {
            var retryDelayMs = 5_000L
            while (isActive) {
                runCatching {
                    client.from("muscle_groups")
                        .selectAsFlow(MuscleGroupDto::id)
                        .collect { dtos ->
                            muscleGroupLocal.replaceAll(dtos.map { it.toDomain() })
                        }
                }.onSuccess {
                    retryDelayMs = 5_000L  // reset backoff after a clean disconnect
                }.onFailure {
                    delay(retryDelayMs)
                    retryDelayMs = min(retryDelayMs * 2, 60_000L)
                }
            }
        }
    }

    override suspend fun syncExercises() {
        val columns = Columns.raw(
            """
    id,
    name,
    exercises(
        id,
        name,
        weight_logs(
            id,
            created_at,
            weight
        )
    )
""".trimIndent()
        )
        val groups = client.from("muscle_groups")
            .select(columns = columns)
            .decodeList<MuscleWithExercisesDto>()
            .map { it.toDomain() }
        exerciseLocal.replaceAll(groups)
    }
}
