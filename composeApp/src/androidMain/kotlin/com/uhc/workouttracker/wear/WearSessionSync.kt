package com.uhc.workouttracker.wear

import android.content.Context
import android.net.Uri
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

object WearSessionSync {
    const val SESSION_PATH = "/supabase_session"

    fun pushSession(context: Context, accessToken: String, refreshToken: String) {
        val dataMap = PutDataMapRequest.create(SESSION_PATH).apply {
            dataMap.putString("access_token", accessToken)
            dataMap.putString("refresh_token", refreshToken)
        }
        Wearable.getDataClient(context)
            .putDataItem(dataMap.asPutDataRequest().setUrgent())
    }

    fun clearSession(context: Context) {
        Wearable.getDataClient(context).deleteDataItems(
            Uri.parse("wear://*$SESSION_PATH")
        )
    }
}
