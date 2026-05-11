package com.uhc.workouttracker.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.uhc.workouttracker.core.db.entity.MuscleGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuscleGroupDao {
    @Query("SELECT * FROM muscle_groups ORDER BY name ASC")
    fun observeAll(): Flow<List<MuscleGroupEntity>>

    @Upsert
    suspend fun upsertAll(groups: List<MuscleGroupEntity>)

    @Upsert
    suspend fun upsert(group: MuscleGroupEntity)

    @Query("DELETE FROM muscle_groups WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM muscle_groups")
    suspend fun deleteAll()

    @Query("DELETE FROM muscle_groups WHERE id NOT IN (:ids)")
    suspend fun deleteExcept(ids: List<Long>)

    @Transaction
    suspend fun replaceAll(groups: List<MuscleGroupEntity>) {
        if (groups.isEmpty()) {
            deleteAll()
        } else {
            deleteExcept(groups.map { it.id })
        }
        upsertAll(groups)
    }
}
