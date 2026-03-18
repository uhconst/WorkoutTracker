package com.uhc.workouttracker

import android.app.Application
import android.util.Log
import com.uhc.workouttracker.di.initKoin
import com.uhc.workouttracker.wear.WearSessionSync
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class WorkoutTrackerApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initKoin()
        launchWearSessionSync()
    }

    private fun launchWearSessionSync() {
        applicationScope.launch {
            Log.d("WearSync", "Application: starting sessionStatus observer")
            val supabase = GlobalContext.get().get<SupabaseClient>()
            supabase.auth.sessionStatus.collect { status ->
                Log.d("WearSync", "Application: sessionStatus changed → $status")
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val session = supabase.auth.currentSessionOrNull()
                        if (session != null) {
                            Log.d("WearSync", "Application: authenticated, pushing session")
                            WearSessionSync.pushSession(
                                this@WorkoutTrackerApplication,
                                session.accessToken,
                                session.refreshToken
                            )
                        } else {
                            Log.w("WearSync", "Application: Authenticated but currentSessionOrNull() is null")
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        Log.d("WearSync", "Application: not authenticated, clearing session")
                        WearSessionSync.clearSession(this@WorkoutTrackerApplication)
                    }
                    else -> Log.d("WearSync", "Application: intermediate status, no action")
                }
            }
        }
    }
}
