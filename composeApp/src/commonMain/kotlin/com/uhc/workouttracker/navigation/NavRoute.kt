package com.uhc.workouttracker.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    data object AuthenticationDestination: NavRoute()
    @Serializable
    data object WorkoutListDestination: NavRoute()
    @Serializable
    data object MuscleGroupsDestination: NavRoute()
    @Serializable
    data class AddExerciseDestination(val exerciseId: Long? = null): NavRoute()
}