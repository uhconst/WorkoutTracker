package com.uhc.workouttracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.wear.WearSessionSync
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val supabase: SupabaseClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        // Push current session every time the phone app comes to the foreground.
        // This covers the case where the user was already logged in before the
        // watch app was installed and no sessionStatus change was ever fired.
        val session = supabase.auth.currentSessionOrNull()
        if (session != null) {
            Log.d("WearSync", "MainActivity.onResume: session found, pushing to watch")
            WearSessionSync.pushSession(this, session.accessToken, session.refreshToken)
        } else {
            Log.d("WearSync", "MainActivity.onResume: no active session, nothing to push")
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
