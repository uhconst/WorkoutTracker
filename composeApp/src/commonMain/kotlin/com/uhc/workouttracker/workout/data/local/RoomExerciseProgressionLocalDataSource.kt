package com.uhc.workouttracker.workout.data.local

import com.uhc.workouttracker.core.db.dao.ExerciseProgressionDao
import com.uhc.workouttracker.core.db.entity.ExerciseProgressionEntity
import com.uhc.workouttracker.workout.domain.model.ProgressionReadiness
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomExerciseProgressionLocalDataSource(
    private val dao: ExerciseProgressionDao
) : ExerciseProgressionLocalDataSource {

    override fun observeAll(): Flow<Map<Long, ProgressionReadiness>> =
        dao.observeAll().map { list ->
            println("[Progression] Room emitting ${list.size} rows: ${list.map { "${it.exerciseId}=${it.readiness}" }}")
            list.associate { it.exerciseId to it.readiness.toProgressionReadiness() }
        }

    override suspend fun upsert(exerciseId: Long, readiness: ProgressionReadiness) {
        println("[Progression] Room upsert: exerciseId=$exerciseId readiness=$readiness")
        dao.upsert(ExerciseProgressionEntity(exerciseId = exerciseId, readiness = readiness.name))
        println("[Progression] Room upsert complete")
    }

    override suspend fun reset(exerciseId: Long) {
        println("[Progression] Room reset: exerciseId=$exerciseId")
        dao.resetToOnTrack(exerciseId)
    }

    private fun String.toProgressionReadiness(): ProgressionReadiness =
        ProgressionReadiness.entries.firstOrNull { it.name == this } ?: ProgressionReadiness.ON_TRACK
}
