package com.uhc.workouttracker.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.uhc.workouttracker.core.db.WorkoutTrackerDatabase
import kotlinx.coroutines.newSingleThreadContext
import org.koin.dsl.module

val iosDatabaseModule = module {
    single<WorkoutTrackerDatabase> {
        Room.databaseBuilder<WorkoutTrackerDatabase>(name = "workout_tracker.db")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(newSingleThreadContext("WorkoutTrackerDB"))
            .build()
    }
}
