package com.uhc.workouttracker.wear.di

import com.uhc.workouttracker.wear.data.repository.WearExerciseRepository
import com.uhc.workouttracker.wear.data.repository.WearExerciseRepositoryImpl
import com.uhc.workouttracker.wear.data.repository.WearSessionRepository
import com.uhc.workouttracker.wear.data.repository.WearSessionRepositoryImpl
import com.uhc.workouttracker.wear.presentation.detail.ExerciseDetailViewModel
import com.uhc.workouttracker.wear.presentation.workouts.WorkoutsViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val wearModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://zlmutumvuenpjowstien.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpsbXV0dW12dWVucGpvd3N0aWVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM2OTA5MzEsImV4cCI6MjA4OTI2NjkzMX0.MAFZl-L4bkL4F7NeHJUE5UnOGxSwRmPqOxgJ51HgptU"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
    single<WearSessionRepository> { WearSessionRepositoryImpl(androidContext(), get()) }
    single<WearExerciseRepository> { WearExerciseRepositoryImpl(get()) }
    viewModelOf(::WorkoutsViewModel)
    viewModelOf(::ExerciseDetailViewModel)
}
