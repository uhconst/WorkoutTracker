package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.WeightLogs
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow

class GetWeightLogsUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    operator fun invoke(): Flow<List<WeightLogs>> =
        client.from("weight_logs").selectAsFlow(WeightLogs::id)
}
