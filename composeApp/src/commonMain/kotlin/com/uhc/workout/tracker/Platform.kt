package com.uhc.workout.tracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform