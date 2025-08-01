package com.uhc.workouttracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform