package com.uhc.workouttracker.workout.data.repository

import com.uhc.workouttracker.workout.data.dto.ExerciseDto
import com.uhc.workouttracker.workout.data.dto.MuscleWithExercisesDto
import com.uhc.workouttracker.workout.data.dto.WeightLogDto
import com.uhc.workouttracker.workout.data.mapper.toDomain
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(SupabaseExperimental::class)
class ExerciseRepositoryImpl(
    private val client: SupabaseClient
) : ExerciseRepository {

    override suspend fun getExercisesGroupedByMuscle(): List<MuscleWithExercises> {
        val columns = Columns.raw(
            """
    id,
    name,
    exercises(
        id,
        name,
        weight_logs(
            id,
            created_at,
            weight
        )
    )
""".trimIndent()
        )
        return client.from("muscle_groups")
            .select(columns = columns)
            .decodeList<MuscleWithExercisesDto>()
            .map { it.toDomain() }
    }

    override suspend fun getExerciseById(id: Long): Exercise? {
        val columns = Columns.raw(
            """
                        id,
                        name,
                        muscle_groups_id,
                        weight_logs(
                            id,
                            created_at,
                            weight
                        )
                    """.trimIndent()
        )
        return runCatching {
            client.from("exercises")
                .select(columns = columns) {
                    filter { ExerciseDto::id eq id }
                }
                .decodeSingle<ExerciseDto>()
                .toDomain()
        }.getOrNull()
    }

    override suspend fun saveExercise(name: String, muscleGroupId: Long, weight: Double) {
        val exercise = NewExerciseDto(name = name, muscleGroupsId = muscleGroupId)
        val inserted = client.from("exercises")
            .upsert(exercise) { select() }
            .decodeSingle<UpsertedExerciseDto>()

        inserted.id?.let {
            client.from("weight_logs").insert(WeightLogDto(weight = weight.toFloat(), exerciseId = it))
        }
    }

    override suspend fun updateExercise(id: Long, name: String, muscleGroupId: Long, weight: Double) {
        val exercise = ExerciseUpsertDto(id = id, name = name, muscleGroupsId = muscleGroupId)
        val updated = client.from("exercises")
            .upsert(exercise) { select() }
            .decodeSingle<UpsertedExerciseDto>()

        updated.id?.let {
            client.from("weight_logs").insert(WeightLogDto(weight = weight.toFloat(), exerciseId = it))
        }
    }
}

@Serializable
private data class NewExerciseDto(
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null
)

@Serializable
private data class ExerciseUpsertDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    @SerialName("muscle_groups_id")
    val muscleGroupsId: Long? = null
)

@Serializable
private data class UpsertedExerciseDto(val id: Long? = null)
