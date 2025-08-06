package com.uhc.workouttracker.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute(val value: String) {
    @Serializable
    data object AuthenticationDestination: NavRoute("authentication")
    @Serializable
    data object WorkoutListDestination: NavRoute("workout-list")
    @Serializable
    data object MuscleGroupsDestination: NavRoute("muscles-group")
}