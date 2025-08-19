package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.Exercise
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class GetExerciseByIdUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    suspend operator fun invoke(id: Long): Exercise {
        val columns = Columns.raw(
            """
                        id,
                        name,
                        weight_logs(
                            id,
                            created_at,
                            weight
                        )
                    """.trimIndent()
        )
        return client.from("exercises").delete {
            select(columns = columns)
            filter {
                Exercise::id eq id
            }
        }.decodeSingle<Exercise>()
    }
}