package com.uhc.workouttracker.muscle.data.repository

import com.uhc.workouttracker.muscle.data.dto.MuscleGroupDto
import com.uhc.workouttracker.muscle.data.mapper.toDomain
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@OptIn(SupabaseExperimental::class)
class MuscleGroupRepositoryImpl(
    private val client: SupabaseClient
) : MuscleGroupRepository {

    override fun observeMuscleGroups(): Flow<List<MuscleGroup>> =
        client.from("muscle_groups")
            .selectAsFlow(MuscleGroupDto::id)
            .map { dtos -> dtos.map { it.toDomain() } }

    override suspend fun addMuscleGroup(name: String) {
        client.from("muscle_groups").insert(NewMuscleGroupDto(name = name))
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
private data class NewMuscleGroupDto(val name: String)
