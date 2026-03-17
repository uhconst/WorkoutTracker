package com.uhc.workouttracker.wear.data.repository

import android.content.Context
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class WearSessionRepositoryImpl(private val context: Context) : WearSessionRepository {

    override suspend fun readSession(): Pair<String, String>? =
        suspendCancellableCoroutine { continuation ->
            Wearable.getDataClient(context)
                .getDataItems()
                .addOnSuccessListener { items ->
                    val item = items.find { it.uri.path == "/supabase_session" }
                    val result = item?.let {
                        val dataMap = DataMapItem.fromDataItem(it).dataMap
                        val accessToken = dataMap.getString("access_token")
                        val refreshToken = dataMap.getString("refresh_token")
                        if (accessToken != null && refreshToken != null) {
                            accessToken to refreshToken
                        } else null
                    }
                    items.release()
                    continuation.resume(result)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
}
