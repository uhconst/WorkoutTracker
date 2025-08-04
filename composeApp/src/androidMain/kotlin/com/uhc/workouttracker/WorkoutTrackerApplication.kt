package com.uhc.workouttracker

import android.app.Application
import com.uhc.workouttracker.di.initKoin

class WorkoutTrackerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}