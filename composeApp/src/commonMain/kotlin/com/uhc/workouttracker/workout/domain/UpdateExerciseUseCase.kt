package com.uhc.workouttracker.workout.domain

import com.uhc.workouttracker.workout.data.WeightLogs
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class UpdateExerciseUseCase(
    private val client: SupabaseClient
) {
    @OptIn(SupabaseExperimental::class)
    suspend operator fun invoke(
        id: Long?,
        name: String,
        muscleGroupId: Long,
        weight: Double
    ) {
        println("SetExerciseUseCase: id=$id, name=$name, muscleGroupId=$muscleGroupId, weight=$weight")
        val exercise = Exercises(
            id = id,
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
data class Exercises(
    val id: Long?,
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null
)