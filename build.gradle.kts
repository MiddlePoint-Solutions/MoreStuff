plugins {
    alias(libs.plugins.androidApplicationPlugin) apply false
    alias(libs.plugins.kotlinPlugin) apply false
    alias(libs.plugins.parcelizePlugin) apply false
    alias(libs.plugins.sqldelightPlugin) apply false
    alias(libs.plugins.googleServicesPlugin) apply false
    alias(libs.plugins.crashlyticsPlugin) apply false
    alias(libs.plugins.moleculePlugin) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.aboutLibrariesPlugin) apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0") apply false
}

group = "co.softov.morestuff"
version = "0.1.0-SNAPSHOT"

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}