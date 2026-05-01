package com.uhc.workouttracker.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.uhc.workouttracker.core.db.entity.ExerciseEntity
import com.uhc.workouttracker.core.db.entity.MuscleGroupEntity
import com.uhc.workouttracker.core.db.entity.WeightLogEntity
import com.uhc.workouttracker.core.db.relation.ExerciseWithWeightLogs
import com.uhc.workouttracker.core.db.relation.MuscleGroupWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Transaction
    @Query("SELECT * FROM muscle_groups ORDER BY name ASC")
    fun observeGroupedByMuscle(): Flow<List<MuscleGroupWithExercises>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): ExerciseWithWeightLogs?

    @Upsert
    suspend fun upsertMuscleGroups(groups: List<MuscleGroupEntity>)

    @Upsert
    suspend fun upsertExercises(exercises: List<ExerciseEntity>)

    @Upsert
    suspend fun upsertWeightLogs(logs: List<WeightLogEntity>)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM weight_logs")
    suspend fun deleteAllWeightLogs()

    @Query("DELETE FROM exercises")
    suspend fun deleteAllExercises()

    @Query("DELETE FROM muscle_groups")
    suspend fun deleteAllMuscleGroups()

    @Transaction
    suspend fun replaceAll(
        muscleGroups: List<MuscleGroupEntity>,
        exercises: List<ExerciseEntity>,
        weightLogs: List<WeightLogEntity>
    ) {
        deleteAllWeightLogs()
        deleteAllExercises()
        deleteAllMuscleGroups()
        upsertMuscleGroups(muscleGroups)
        upsertExercises(exercises)
        upsertWeightLogs(weightLogs)
    }
}
