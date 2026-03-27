package com.uhc.workouttracker.muscle.data.repository

import com.uhc.workouttracker.muscle.data.dto.MuscleGroupDto
import com.uhc.workouttracker.muscle.data.local.MuscleGroupLocalDataSource
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

class RoomMuscleGroupRepositoryImpl(
    private val client: SupabaseClient,
    private val local: MuscleGroupLocalDataSource
) : MuscleGroupRepository {

    override fun observeMuscleGroups(): Flow<List<MuscleGroup>> = local.observeAll()

    override suspend fun addMuscleGroup(name: String) {
        client.from("muscle_groups").insert(RoomNewMuscleGroupDto(name = name))
        // Realtime echo → ExerciseSyncManager → Room → Flow emits automatically
    }

    override suspend fun updateMuscleGroup(muscleGroup: MuscleGroup) {
        client.from("muscle_groups").upsert(MuscleGroupDto(id = muscleGroup.id, name = muscleGroup.name))
    }

    override suspend fun deleteMuscleGroup(id: Long) {
        client.from("muscle_groups").delete {
            filter { MuscleGroupDto::id eq id }
        }
    }
}

@Serializable
private data class RoomNewMuscleGroupDto(val name: String)
