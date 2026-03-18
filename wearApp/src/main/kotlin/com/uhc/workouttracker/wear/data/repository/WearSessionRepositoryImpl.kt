package com.uhc.workouttracker.wear.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlin.coroutines.resume
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

class WearSessionRepositoryImpl(private val context: Context) : WearSessionRepository {

    companion object {
        const val SESSION_PATH = "/supabase_session"
        private const val TAG = "WearSession"
    }

    override suspend fun readSession(): Pair<String, String>? = try {
        suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "readSession: querying DataClient for $SESSION_PATH")
            Wearable.getDataClient(context)
                .getDataItems()
                .addOnSuccessListener { items ->
                    Log.d(TAG, "readSession: getDataItems succeeded, count=${items.count}")
                    items.forEach { item ->
                        Log.d(TAG, "readSession: found item uri=${item.uri}")
                    }
                    val item = items.find { it.uri.path == SESSION_PATH }
                    if (item == null) {
                        Log.w(TAG, "readSession: no item found at path $SESSION_PATH")
                    }
                    val result = item?.let {
                        val dataMap = DataMapItem.fromDataItem(it).dataMap
                        val accessToken = dataMap.getString("access_token")
                        val refreshToken = dataMap.getString("refresh_token")
                        Log.d(TAG, "readSession: accessToken=${accessToken?.take(10)}… refreshToken=${if (refreshToken != null) "present" else "null"}")
                        if (accessToken != null && refreshToken != null) accessToken to refreshToken
                        else null
                    }
                    items.release()
                    continuation.resume(result)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "readSession: getDataItems FAILED — ${e.message}", e)
                    continuation.resume(null)
                }
        }
    } catch (e: Exception) {
        Log.e(TAG, "readSession: unexpected exception — ${e.message}", e)
        null
    }

    override fun observeSession(): Flow<Pair<String, String>?> = callbackFlow {
        Log.d(TAG, "observeSession: registering DataClient listener")
        val listener = DataClient.OnDataChangedListener { dataEvents ->
            Log.d(TAG, "observeSession: onDataChanged — ${dataEvents.count} event(s)")
            for (event in dataEvents) {
                val path = event.dataItem.uri.path
                Log.d(TAG, "observeSession: event type=${event.type} path=$path")
                if (path == SESSION_PATH) {
                    when (event.type) {
                        DataEvent.TYPE_CHANGED -> {
                            val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                            val accessToken = dataMap.getString("access_token")
                            val refreshToken = dataMap.getString("refresh_token")
                            Log.d(TAG, "observeSession: session arrived — token prefix=${accessToken?.take(10)}…")
                            trySend(
                                if (accessToken != null && refreshToken != null)
                                    accessToken to refreshToken
                                else null
                            )
                        }
                        DataEvent.TYPE_DELETED -> {
                            Log.d(TAG, "observeSession: session deleted")
                            trySend(null)
                        }
                    }
                }
            }
        }
        Wearable.getDataClient(context).addListener(listener)
        awaitClose {
            Log.d(TAG, "observeSession: removing listener")
            Wearable.getDataClient(context).removeListener(listener)
        }
    }
}
