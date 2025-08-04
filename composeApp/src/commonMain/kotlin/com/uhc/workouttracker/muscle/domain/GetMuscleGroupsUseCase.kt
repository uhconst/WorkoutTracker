package com.uhc.workouttracker.muscle.domain

import com.uhc.workouttracker.muscle.data.MuscleGroup
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow

class GetMuscleGroupsUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    operator fun invoke(): Flow<List<MuscleGroup>> =
        client.from("muscle_groups").selectAsFlow(MuscleGroup::id)/*.decodeSingle<Muscle>()*/

    /*     queries
             .getAllMuscles()
             .executeAsList()
             .map { it.toMuscle() }*/

}
