package com.uhc.workouttracker.di

import com.uhc.workouttracker.authentication.data.AuthApi
import com.uhc.workouttracker.authentication.data.AuthApiImpl
import com.uhc.workouttracker.authentication.presentation.LoginViewModel
//import com.uhc.workouttracker.authentication.presentation.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<AuthApi> { AuthApiImpl(/*get()*/) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
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
