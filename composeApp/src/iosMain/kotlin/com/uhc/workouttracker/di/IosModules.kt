package com.uhc.workouttracker.di

import com.uhc.workouttracker.muscle.data.repository.MuscleGroupRepositoryImpl
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.data.repository.ExerciseRepositoryImpl
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import org.koin.dsl.module

val iosDataModule = module {
    single<MuscleGroupRepository> { MuscleGroupRepositoryImpl(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get()) }
}
