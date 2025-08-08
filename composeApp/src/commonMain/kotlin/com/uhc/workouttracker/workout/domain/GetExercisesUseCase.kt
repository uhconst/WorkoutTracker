package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class GetExercisesUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    suspend operator fun invoke(): List<MuscleGroupsWithExercises> {
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
        return client.from("muscle_groups")
            .select(columns = columns)
            .decodeList<MuscleGroupsWithExercises>()
    }
}