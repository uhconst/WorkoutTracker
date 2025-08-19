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
import com.uhc.workouttracker.workout.domain.SetExerciseUseCase
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
    factory { SetExerciseUseCase(get()) }
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
            "https://evblichpfnyvvboqhsht.supabase.co",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV2YmxpY2hwZm55dnZib3Foc2h0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzM4MzYzNDUsImV4cCI6MjA0OTQxMjM0NX0.9hGBigU1xpJnxH3HdAs3-0I8oq_83P7MfArRL73T62I"
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
