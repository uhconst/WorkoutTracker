package com.uhc.workouttracker.di

import com.uhc.workouttracker.authentication.data.AuthApi
import com.uhc.workouttracker.authentication.data.AuthApiImpl
import com.uhc.workouttracker.authentication.presentation.LoginViewModel
import com.uhc.workouttracker.muscle.domain.DeleteMuscleGroupUseCase
import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import com.uhc.workouttracker.muscle.domain.SetMuscleGroupUseCase
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel
import com.uhc.workouttracker.workout.domain.GetExerciseByIdUseCase
import com.uhc.workouttracker.workout.domain.GetExercisesUseCase
import com.uhc.workouttracker.workout.domain.GetWeightLogsUseCase
import com.uhc.workouttracker.workout.domain.SaveExerciseUseCase
import com.uhc.workouttracker.workout.domain.UpdateExerciseUseCase
import com.uhc.workouttracker.workout.presentation.AddExerciseViewModel
import com.uhc.workouttracker.workout.presentation.ExerciseListViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<AuthApi> { AuthApiImpl(get()) }
}
val domainModule = module {
    factory { DeleteMuscleGroupUseCase(get()) }
    factory { GetMuscleGroupsUseCase(get()) }
    factory { SetMuscleGroupUseCase(get()) }
    factory { GetExercisesUseCase(get()) }
    factory { GetExerciseByIdUseCase(get()) }
    factory { GetWeightLogsUseCase(get()) }
    factory { UpdateExerciseUseCase(get()) }
    factory { SaveExerciseUseCase(get()) }
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
