package com.uhc.workouttracker

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.navigation.TicketMasterNavHost
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview //todo delete
fun App() {
    WorkoutTrackerTheme {
        Surface {
            val navController: NavHostController = rememberNavController()
            TicketMasterNavHost(navController = navController)
        }

/*        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }*/
    }

/*    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == NavRoute.Home.value,
                    onClick = { navController.navigate(NavRoute.Home.value) },
                    label = { Text(stringResource(R.string.home_bottom_nav)) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = currentRoute == NavRoute.About.value,
                    onClick = { navController.navigate(NavRoute.About.value) },
                    label = { Text(stringResource(R.string.about_bottom_nav)) },
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        WorkoutTrackerNavHost(Modifier.padding(innerPadding), navController)
    }*/
}