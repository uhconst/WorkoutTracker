package com.uhc.workouttracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.navigation.NavRoute
import com.uhc.workouttracker.navigation.TicketMasterNavHost
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

data class NavigationItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val route: String? = null,
    val onClick: () -> Unit = {}
)

@Composable
@Preview //todo delete
fun App() {
    WorkoutTrackerTheme {
        Surface {
            val navController: NavHostController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var selectedItemIndex by remember { mutableStateOf(0) }

            val navigationItems = listOf(
                NavigationItem(
                    title = "View Workouts",
                    icon = {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = "View Workouts"
                        )
                    },
                    route = NavRoute.WorkoutListDestination.value,
                    onClick = {
                        navController.navigate(NavRoute.WorkoutListDestination.value) {
                            popUpTo(NavRoute.WorkoutListDestination.value) { inclusive = true }
                        }
                    }
                ),
                NavigationItem(
                    title = "Add Muscle Group",
                    icon = {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = "Add Muscle Group"
                        )
                    },
                    onClick = {
                        navController.navigate(NavRoute.MuscleGroupsDestination.value) {
                            popUpTo(NavRoute.MuscleGroupsDestination.value) { inclusive = true }
                        }
                    }
                ),
                NavigationItem(
                    title = "Add Exercise",
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Exercise") },
                    onClick = {
                        navController.navigate(NavRoute.AddExerciseDestination.value) {
                            popUpTo(NavRoute.WorkoutListDestination.value) { inclusive = true }
                        }
                    }
                ),
                NavigationItem(
                    title = "Settings",
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Settings") },
                    onClick = {
                        // Navigate to Settings screen when implemented
                    }
                ),
                NavigationItem(
                    title = "Logout",
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") },
                    route = NavRoute.AuthenticationDestination.value,
                    onClick = {
                        navController.navigate(NavRoute.AuthenticationDestination.value) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            )

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            "Workout Tracker",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        navigationItems.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                label = { Text(item.title) },
                                selected = index == selectedItemIndex,
                                onClick = {
                                    selectedItemIndex = index
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    item.onClick()
                                },
                                icon = item.icon,
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            ) {
                TicketMasterNavHost(
                    navController = navController,
                    drawerState = drawerState
                )
            }
        }
    }
}