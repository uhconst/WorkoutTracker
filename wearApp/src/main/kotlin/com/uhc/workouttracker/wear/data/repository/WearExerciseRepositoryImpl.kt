package com.uhc.workouttracker.wear.data.repository

import com.uhc.workouttracker.wear.data.dto.MuscleWithExercisesDto
import com.uhc.workouttracker.wear.domain.model.Exercise
import com.uhc.workouttracker.wear.domain.model.MuscleWithExercises
import com.uhc.workouttracker.wear.domain.model.WeightLog
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

@OptIn(SupabaseExperimental::class)
class WearExerciseRepositoryImpl(
    private val client: SupabaseClient
) : WearExerciseRepository {

    override suspend fun getExercisesGroupedByMuscle(): List<MuscleWithExercises> {
        val columns = Columns.raw(
            """
            id,
            name,
            exercises(
                id,
                name,
                weight_logs(
                    id,
                    weight
                )
            )
            """.trimIndent()
        )
        return client.from("muscle_groups")
            .select(columns = columns)
            .decodeList<MuscleWithExercisesDto>()
            .map { it.toDomain() }
    }

    private fun MuscleWithExercisesDto.toDomain() = MuscleWithExercises(
        id = id,
        muscleName = muscleName,
        exercises = exercises?.map { ex ->
            Exercise(
                id = ex.id,
                name = ex.name,
                muscleGroupId = ex.muscleGroupsId,
                weightLogs = ex.weightLogs?.map { wl -> WeightLog(id = wl.id, weight = wl.weight) }
                    ?: emptyList()
            )
        } ?: emptyList()
    )
}
