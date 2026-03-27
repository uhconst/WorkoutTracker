package com.uhc.workouttracker.muscle.data.local

import com.uhc.workouttracker.core.db.dao.MuscleGroupDao
import com.uhc.workouttracker.core.db.mapper.toDomain
import com.uhc.workouttracker.core.db.mapper.toEntity
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMuscleGroupLocalDataSource(
    private val dao: MuscleGroupDao
) : MuscleGroupLocalDataSource {

    override fun observeAll(): Flow<List<MuscleGroup>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun replaceAll(groups: List<MuscleGroup>) {
        dao.deleteAll()
        dao.upsertAll(groups.map { it.toEntity() })
    }

    override suspend fun upsert(group: MuscleGroup) {
        dao.upsert(group.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}
