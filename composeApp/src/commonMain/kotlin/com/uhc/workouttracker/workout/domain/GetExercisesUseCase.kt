package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.Exercise
import com.uhc.workouttracker.workout.data.MuscleGroupsWithExercises
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow

class GetExercisesUseCase(
    private val client: SupabaseClient
) {
        @OptIn(SupabaseExperimental::class)
        operator fun invoke(): Flow<List<Exercise>> =
            client.from("exercises").selectAsFlow(Exercise::id)

    @OptIn(SupabaseExperimental::class)
    suspend /*operator*/ fun invoke2(): List<MuscleGroupsWithExercises> {
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
//            .decodeSingle<MuscleGroupsWithExercises>()
    }
}