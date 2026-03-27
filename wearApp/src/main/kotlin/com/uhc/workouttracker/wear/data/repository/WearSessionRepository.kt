package com.uhc.workouttracker.wear.data.repository

import kotlinx.coroutines.flow.Flow

interface WearSessionRepository {
    /** One-shot read — returns null if nothing is stored or on error. */
    suspend fun readSession(): Pair<String, String>?
    /** Emits non-null whenever the phone pushes a session, null when it's deleted. */
    fun observeSession(): Flow<Pair<String, String>?>
    /**
     * Reads tokens from the Data Layer, imports them into the Supabase client,
     * and refreshes only if the token is already expired (expiresIn == 0L).
     * Returns false if no session is stored or if auth fails (e.g. offline + expired).
     */
    suspend fun importAndValidateSession(): Boolean
}
