package com.uhc.workouttracker.di

import com.uhc.workouttracker.authentication.data.repository.AuthRepositoryImpl
import com.uhc.workouttracker.authentication.domain.repository.AuthRepository
import com.uhc.workouttracker.authentication.presentation.LoginViewModel
import com.uhc.workouttracker.core.db.WorkoutTrackerDatabase
import com.uhc.workouttracker.core.sync.ExerciseSyncManager
import com.uhc.workouttracker.core.sync.SyncManager
import com.uhc.workouttracker.muscle.data.local.MuscleGroupLocalDataSource
import com.uhc.workouttracker.muscle.data.local.RoomMuscleGroupLocalDataSource
import com.uhc.workouttracker.muscle.data.repository.RoomMuscleGroupRepositoryImpl
import com.uhc.workouttracker.muscle.domain.repository.MuscleGroupRepository
import com.uhc.workouttracker.muscle.presentation.MuscleGroupsViewModel
import com.uhc.workouttracker.workout.data.local.ExerciseLocalDataSource
import com.uhc.workouttracker.workout.data.local.RoomExerciseLocalDataSource
import com.uhc.workouttracker.workout.data.repository.RoomExerciseRepositoryImpl
import com.uhc.workouttracker.workout.domain.repository.ExerciseRepository
import com.uhc.workouttracker.workout.presentation.AddExerciseViewModel
import com.uhc.workouttracker.workout.presentation.ExerciseListViewModel
import com.uhc.workouttracker.workout.presentation.ExerciseProgressionGraphViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single { get<WorkoutTrackerDatabase>().muscleGroupDao() }
    single { get<WorkoutTrackerDatabase>().exerciseDao() }
    single<MuscleGroupLocalDataSource> { RoomMuscleGroupLocalDataSource(get()) }
    single<ExerciseLocalDataSource> { RoomExerciseLocalDataSource(get()) }
    single<SyncManager> { ExerciseSyncManager(get(), get(), get(), get()) }
    single<MuscleGroupRepository> { RoomMuscleGroupRepositoryImpl(get(), get()) }
    single<ExerciseRepository> { RoomExerciseRepositoryImpl(get(), get(), get()) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::ExerciseListViewModel)
    viewModelOf(::MuscleGroupsViewModel)
    viewModelOf(::AddExerciseViewModel)
    viewModelOf(::ExerciseProgressionGraphViewModel)
}

val supabaseModule = module {
    single {
        createSupabaseClient(supabaseUrl, supabaseAnonKey) {
            requestTimeout = 30.seconds
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
