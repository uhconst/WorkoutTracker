package com.uhc.workouttracker.muscle.data.mapper

import com.uhc.workouttracker.muscle.data.dto.MuscleGroupDto
import com.uhc.workouttracker.muscle.domain.model.MuscleGroup

fun MuscleGroupDto.toDomain() = MuscleGroup(id = id, name = name)
