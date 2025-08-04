package com.uhc.workouttracker.di

import com.uhc.workouttracker.authentication.data.AuthApi
import com.uhc.workouttracker.authentication.data.AuthApiImpl
import com.uhc.workouttracker.authentication.presentation.LoginViewModel
import com.uhc.workouttracker.muscle.domain.GetMuscleGroupsUseCase
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel
import com.uhc.workouttracker.workout.domain.GetWorkoutsUseCase
import com.uhc.workouttracker.workout.presentation.ExerciseListViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
//import com.uhc.workouttracker.authentication.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<AuthApi> { AuthApiImpl(get()) }
}
val domainModule = module {
    factory { GetMuscleGroupsUseCase(get()) }
    factory { GetWorkoutsUseCase(get()) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::ExerciseListViewModel)
    viewModelOf(::MuscleGroupsViewModel)
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
/*
val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://id.supabase.co",
            supabaseKey = "apikey"
        ) {
*/
/*            install(Storage) {
                resumable {
                    cache = SettingsResumableCache()
                }
            }*//*

        }
    }
    single {
        get<SupabaseClient>().storage[BUCKET].resumable
    }
}*/
