package com.uhc.workouttracker.core.db.mapper

import com.uhc.workouttracker.core.db.entity.ExerciseEntity
import com.uhc.workouttracker.core.db.entity.MuscleGroupEntity
import com.uhc.workouttracker.core.db.entity.WeightLogEntity
import com.uhc.workouttracker.core.db.relation.ExerciseWithWeightLogs
import com.uhc.workouttracker.core.db.relation.MuscleGroupWithExercises
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.model.WeightLog

fun MuscleGroupEntity.toDomain(): MuscleGroup = MuscleGroup(id = id, name = name)

fun MuscleGroup.toEntity(): MuscleGroupEntity = MuscleGroupEntity(id = id, name = name)

fun WeightLogEntity.toDomain(): WeightLog = WeightLog(id = id, weight = weight, exerciseId = exerciseId)

fun ExerciseWithWeightLogs.toDomain(): Exercise = Exercise(
    id = exercise.id,
    name = exercise.name,
    description = exercise.description,
    muscleGroupId = exercise.muscleGroupId,
    weightLogs = weightLogs.map { it.toDomain() }
)

fun MuscleGroupWithExercises.toDomain(): MuscleWithExercises = MuscleWithExercises(
    id = muscleGroup.id,
    muscleName = muscleGroup.name,
    exercises = exercises.map { it.toDomain() }
)

fun MuscleWithExercises.toMuscleGroupEntity(): MuscleGroupEntity =
    MuscleGroupEntity(id = id, name = muscleName)

fun MuscleWithExercises.toExerciseEntities(): List<ExerciseEntity> =
    exercises.map { exercise ->
        ExerciseEntity(
            id = exercise.id,
            name = exercise.name,
            description = exercise.description,
            muscleGroupId = id
        )
    }

fun MuscleWithExercises.toWeightLogEntities(): List<WeightLogEntity> =
    exercises.flatMap { exercise ->
        exercise.weightLogs.map { log ->
            WeightLogEntity(id = log.id, weight = log.weight, exerciseId = exercise.id)
        }
    }
