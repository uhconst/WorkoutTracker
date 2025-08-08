package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.Exercise
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from

class SetExerciseUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    suspend operator fun invoke(exercise: Exercise) {
        client.from("exercises").insert(exercise)
    }
}