package com.uhc.workouttracker.wear.data.repository

import kotlinx.coroutines.flow.Flow

interface WearSessionRepository {
    /** One-shot read — returns null if nothing is stored or on error. */
    suspend fun readSession(): Pair<String, String>?
    /** Emits non-null whenever the phone pushes a session, null when it's deleted. */
    fun observeSession(): Flow<Pair<String, String>?>
}
