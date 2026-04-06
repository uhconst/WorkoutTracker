package com.uhc.workouttracker.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.uhc.workouttracker.core.db.WorkoutTrackerDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val iosDatabaseModule = module {
    single<WorkoutTrackerDatabase> {
        Room.databaseBuilder<WorkoutTrackerDatabase>(name = "workout_tracker.db")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default.limitedParallelism(1))
            .addMigrations(WorkoutTrackerDatabase.MIGRATION_1_2)
            .build()
    }
}
