# ============================================================
# kotlinx.serialization
# ============================================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

# Keep @Serializable classes and their companions so the generated serializers survive shrinking
-keep,includedescriptorclasses class com.uhc.workouttracker.**$$serializer { *; }
-keepclassmembers class com.uhc.workouttracker.** {
    *** Companion;
}
-keepclasseswithmembers class com.uhc.workouttracker.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================================
# Room
# ============================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keepclassmembers @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# ============================================================
# Koin
# ============================================================
-keep class org.koin.** { *; }
-keepnames class org.koin.**
-dontwarn org.koin.**

# ============================================================
# Supabase / Ktor
# ============================================================
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-dontwarn io.github.jan.supabase.**
-dontwarn io.ktor.**

# Keep Ktor engine internals used via reflection
-keepclassmembers class io.ktor.** { *; }

# ============================================================
# Kotlin coroutines
# ============================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ============================================================
# General Android / OkHttp
# ============================================================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
