package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.WeightLogs
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SaveExerciseUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    suspend operator fun invoke(
        name: String,
        muscleGroupId: Long,
        weight: Double
    ) {
        val exercise = NewExercises(
            name = name,
            muscleGroupsId = muscleGroupId
        )
        val insertedExercise = client
            .from("exercises")
            .upsert(exercise) {
                select()
            }
            .decodeSingle<Exercises>()

        insertedExercise.id?.let {
            val weightLogs = WeightLogs(
                weight = weight.toFloat(),
                exerciseId = it
            )
            client
                .from("weight_logs")
                .insert(weightLogs)
        }
    }
}

@Serializable
@SerialName("exercises")
private data class NewExercises(
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null
)