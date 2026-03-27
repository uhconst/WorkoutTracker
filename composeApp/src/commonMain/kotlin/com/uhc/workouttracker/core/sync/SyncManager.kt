package com.uhc.workouttracker.core.sync

interface SyncManager {
    suspend fun syncExercises()
}
