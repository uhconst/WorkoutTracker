package com.uhc.workouttracker.workout.data.repository

import com.uhc.workouttracker.workout.data.local.ExerciseProgressionLocalDataSource
import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import com.uhc.workouttracker.workout.domain.repository.ExerciseProgressionRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RoomExerciseProgressionRepositoryImpl(
    private val client: SupabaseClient,
    private val local: ExerciseProgressionLocalDataSource
) : ExerciseProgressionRepository {

    override fun observeAll(): Flow<Map<Long, ProgressionReadiness>> = local.observeAll()

    override suspend fun update(exerciseId: Long, readiness: ProgressionReadiness) {
        println("[Progression] update called: exerciseId=$exerciseId readiness=$readiness")
        local.upsert(exerciseId, readiness)
        println("[Progression] local upsert done")
        val userId = client.auth.currentUserOrNull()?.id
        println("[Progression] userId=$userId")
        if (userId == null) {
            println("[Progression] no userId, skipping remote sync")
            return
        }
        runCatching {
            client.from("exercise_progression").upsert(
                ExerciseProgressionDto(userId = userId, exerciseId = exerciseId, readiness = readiness.name)
            )
            println("[Progression] remote upsert done")
        }.onFailure { println("[Progression] remote upsert failed: $it") }
    }

    override suspend fun reset(exerciseId: Long) {
        println("[Progression] reset called: exerciseId=$exerciseId")
        local.reset(exerciseId)
        println("[Progression] local reset done")
        val userId = client.auth.currentUserOrNull()?.id ?: return
        runCatching {
            client.from("exercise_progression").upsert(
                ExerciseProgressionDto(userId = userId, exerciseId = exerciseId, readiness = ProgressionReadiness.ON_TRACK.name)
            )
        }.onFailure { println("[Progression] remote reset failed: $it") }
    }
}

@Serializable
private data class ExerciseProgressionDto(
    @SerialName("user_id") val userId: String,
    @SerialName("exercise_id") val exerciseId: Long,
    val readiness: String
)
