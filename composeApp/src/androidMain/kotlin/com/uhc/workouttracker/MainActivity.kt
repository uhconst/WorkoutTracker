package com.uhc.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.uhc.workouttracker.navigation.LocalNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val navController: NavHostController = rememberNavController()

            CompositionLocalProvider(LocalNavController provides navController) {
                App(navController)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val navController: NavHostController = rememberNavController()
    App(navController)
}