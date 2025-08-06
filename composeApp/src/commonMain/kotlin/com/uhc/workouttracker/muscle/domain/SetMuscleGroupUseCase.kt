package com.uhc.workouttracker.muscle.domain

import com.uhc.workouttracker.muscle.data.MuscleGroup
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from

class SetMuscleGroupUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    suspend operator fun invoke(muscleGroup: MuscleGroup) {
        client.from("muscle_groups").insert(muscleGroup)
    }
}