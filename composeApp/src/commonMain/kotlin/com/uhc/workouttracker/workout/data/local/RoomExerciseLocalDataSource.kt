package com.uhc.workouttracker.workout.data.local

import com.uhc.workouttracker.core.db.dao.ExerciseDao
import com.uhc.workouttracker.core.db.mapper.toDomain
import com.uhc.workouttracker.core.db.mapper.toExerciseEntities
import com.uhc.workouttracker.core.db.mapper.toMuscleGroupEntity
import com.uhc.workouttracker.core.db.mapper.toWeightLogEntities
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomExerciseLocalDataSource(
    private val dao: ExerciseDao
) : ExerciseLocalDataSource {

    override fun observeGroupedByMuscle(): Flow<List<MuscleWithExercises>> =
        dao.observeGroupedByMuscle().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Exercise? =
        dao.getById(id)?.toDomain()

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)

    override suspend fun replaceAll(groups: List<MuscleWithExercises>) {
        dao.replaceAll(
            muscleGroups = groups.map { it.toMuscleGroupEntity() },
            exercises = groups.flatMap { it.toExerciseEntities() },
            weightLogs = groups.flatMap { it.toWeightLogEntities() }
        )
    }
}
