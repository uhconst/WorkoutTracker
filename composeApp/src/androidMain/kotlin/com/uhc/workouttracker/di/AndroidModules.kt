package com.uhc.workouttracker.di

import androidx.room.Room
import com.uhc.workouttracker.core.db.WorkoutTrackerDatabase
import com.uhc.workouttracker.core.sync.ExerciseSyncManager
import com.uhc.workouttracker.core.sync.SyncManager
import com.uhc.workouttracker.muscle.data.local.MuscleGroupLocalDataSource
import com.uhc.workouttracker.muscle.data.local.RoomMuscleGroupLocalDataSource
import com.uhc.workouttracker.muscle.data.repository.RoomMuscleGroupRepositoryImpl
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.workout.data.local.ExerciseLocalDataSource
import com.uhc.workouttracker.workout.data.local.RoomExerciseLocalDataSource
import com.uhc.workouttracker.workout.data.repository.RoomExerciseRepositoryImpl
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDataModule = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<WorkoutTrackerDatabase> {
        Room.databaseBuilder(
            androidContext(),
            WorkoutTrackerDatabase::class.java,
            "workout_tracker.db"
        ).build()
    }
    single { get<WorkoutTrackerDatabase>().muscleGroupDao() }
    single { get<WorkoutTrackerDatabase>().exerciseDao() }
    single<MuscleGroupLocalDataSource> { RoomMuscleGroupLocalDataSource(get()) }
    single<ExerciseLocalDataSource> { RoomExerciseLocalDataSource(get()) }
    single<SyncManager> { ExerciseSyncManager(get(), get(), get(), get()) }
    single<MuscleGroupRepository> { RoomMuscleGroupRepositoryImpl(get(), get()) }
    single<ExerciseRepository> { RoomExerciseRepositoryImpl(get(), get(), get()) }
}
