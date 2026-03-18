package com.uhc.workouttracker.wear

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

object WearSessionSync {
    const val SESSION_PATH = "/supabase_session"
    private const val TAG = "WearSync"

    fun pushSession(context: Context, accessToken: String, refreshToken: String) {
        Log.d(TAG, "pushSession: pushing session to watch (token prefix=${accessToken.take(10)}…)")
        val dataMap = PutDataMapRequest.create(SESSION_PATH).apply {
            dataMap.putString("access_token", accessToken)
            dataMap.putString("refresh_token", refreshToken)
        }
        Wearable.getDataClient(context)
            .putDataItem(dataMap.asPutDataRequest().setUrgent())
            .addOnSuccessListener {
                Log.d(TAG, "pushSession: success — data item URI=${it.uri}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "pushSession: FAILED — ${e.message}", e)
            }
    }

    fun clearSession(context: Context) {
        Log.d(TAG, "clearSession: removing session from watch")
        Wearable.getDataClient(context)
            .deleteDataItems(Uri.parse("wear://*$SESSION_PATH"))
            .addOnSuccessListener { count ->
                Log.d(TAG, "clearSession: deleted $count item(s)")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "clearSession: FAILED — ${e.message}", e)
            }
    }
}
