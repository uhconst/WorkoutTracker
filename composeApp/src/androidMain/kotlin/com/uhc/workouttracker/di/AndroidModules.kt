package com.uhc.workouttracker.di

import androidx.room.Room
import com.uhc.workouttracker.core.db.WorkoutTrackerDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDatabaseModule = module {
    single<WorkoutTrackerDatabase> {
        Room.databaseBuilder<WorkoutTrackerDatabase>(
            context = androidContext(),
            name = "workout_tracker.db"
        ).addMigrations(WorkoutTrackerDatabase.MIGRATION_1_2).build()
    }
}
