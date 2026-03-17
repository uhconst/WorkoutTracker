package com.uhc.workouttracker.wear.data.repository

interface WearSessionRepository {
    suspend fun readSession(): Pair<String, String>?
}
