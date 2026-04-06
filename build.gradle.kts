plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
}

// Force all Kotlin stdlib artifacts to the same version to prevent old
// kotlin-stdlib-common:1.x from being used (pulled in by some transitive deps),
// which would shadow Kotlin 1.9+ APIs (e.g. String.format) on Native/iOS.
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
                useVersion(libs.versions.kotlin.get())
                because("Align all kotlin-stdlib artifacts with the project's Kotlin version")
            }
        }
    }
}