package com.uhc.workouttracker.di

import com.uhc.workouttracker.authentication.data.repository.AuthRepositoryImpl
import com.uhc.workouttracker.authentication.domain.repository.AuthRepository
import com.uhc.workouttracker.authentication.presentation.LoginViewModel
import com.uhc.workouttracker.muscle.data.repository.MuscleGroupRepositoryImpl
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel
import com.uhc.workouttracker.workout.data.repository.ExerciseRepositoryImpl
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import com.uhc.workouttracker.workout.presentation.AddExerciseViewModel
import com.uhc.workouttracker.workout.presentation.ExerciseListViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<MuscleGroupRepository> { MuscleGroupRepositoryImpl(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::ExerciseListViewModel)
    viewModelOf(::MuscleGroupsViewModel)
    viewModelOf(::AddExerciseViewModel)
}

val supabaseModule = module {
    single {
        createSupabaseClient(
            "https://zlmutumvuenpjowstien.supabase.co",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpsbXV0dW12dWVucGpvd3N0aWVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzM2OTA5MzEsImV4cCI6MjA4OTI2NjkzMX0.MAFZl-L4bkL4F7NeHJUE5UnOGxSwRmPqOxgJ51HgptU"
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
