package com.uhc.workouttracker.workout.data.repository

import com.uhc.workouttracker.core.sync.SyncManager
import com.uhc.workouttracker.workout.data.dto.WeightLogDto
import com.uhc.workouttracker.workout.data.local.ExerciseLocalDataSource
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RoomExerciseRepositoryImpl(
    private val client: SupabaseClient,
    private val local: ExerciseLocalDataSource,
    private val syncManager: SyncManager
) : ExerciseRepository {

    override fun observeExercisesGroupedByMuscle(): Flow<List<MuscleWithExercises>> =
        local.observeGroupedByMuscle()

    override suspend fun getExercisesGroupedByMuscle(): List<MuscleWithExercises> {
        syncManager.syncExercises()
        return emptyList()
    }

    override suspend fun getExerciseById(id: Long): Exercise? =
        local.getById(id)

    override suspend fun saveExercise(name: String, muscleGroupId: Long, weight: Double) {
        val exercise = RoomNewExerciseDto(name = name, muscleGroupsId = muscleGroupId)
        val inserted = client.from("exercises")
            .upsert(exercise) { select() }
            .decodeSingle<RoomUpsertedExerciseDto>()

        inserted.id?.let {
            client.from("weight_logs").insert(WeightLogDto(weight = weight.toFloat(), exerciseId = it))
        }
        syncManager.syncExercises()
    }

    override suspend fun updateExercise(id: Long, name: String, muscleGroupId: Long, weight: Double) {
        val exercise = RoomExerciseUpsertDto(id = id, name = name, muscleGroupsId = muscleGroupId)
        val updated = client.from("exercises")
            .upsert(exercise) { select() }
            .decodeSingle<RoomUpsertedExerciseDto>()

        updated.id?.let {
            client.from("weight_logs").insert(WeightLogDto(weight = weight.toFloat(), exerciseId = it))
        }
        syncManager.syncExercises()
    }

    override suspend fun deleteExercise(id: Long) {
        client.from("exercises").delete { filter { eq("id", id) } }
        local.deleteById(id)
    }
}

@Serializable
private data class RoomNewExerciseDto(
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null
)

@Serializable
private data class RoomExerciseUpsertDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null
)

@Serializable
private data class RoomUpsertedExerciseDto(val id: Long? = null)
