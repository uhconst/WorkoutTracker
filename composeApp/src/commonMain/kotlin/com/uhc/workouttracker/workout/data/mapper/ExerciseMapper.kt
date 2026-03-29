package com.uhc.workouttracker.workout.data.mapper

import com.uhc.workouttracker.workout.data.dto.ExerciseDto
import com.uhc.workouttracker.workout.data.dto.MuscleWithExercisesDto
import com.uhc.workouttracker.workout.data.dto.WeightLogDto
import com.uhc.workouttracker.workout.domain.model.Exercise
import com.uhc.workouttracker.workout.domain.model.MuscleWithExercises
import com.uhc.workouttracker.workout.domain.model.WeightLog

fun WeightLogDto.toDomain() = WeightLog(id = id, weight = weight, exerciseId = exerciseId, date = createdAt)

fun ExerciseDto.toDomain() = Exercise(
    id = id,
    name = name,
    description = description,
    muscleGroupId = muscleGroupsId,
    weightLogs = weightLogs?.map { it.toDomain() } ?: emptyList()
)

fun MuscleWithExercisesDto.toDomain() = MuscleWithExercises(
    id = id,
    muscleName = muscleName,
    exercises = exercises?.map { it.toDomain() } ?: emptyList()
)
