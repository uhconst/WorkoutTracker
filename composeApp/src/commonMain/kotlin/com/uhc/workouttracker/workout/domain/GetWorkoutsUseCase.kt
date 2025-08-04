package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.Exercises
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow

class GetWorkoutsUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    operator fun invoke(): Flow<List<Exercises>> =
        client.from("exercises").selectAsFlow(Exercises::id)
}