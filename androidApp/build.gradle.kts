import Build_gradle.Versions.accompanist_version
import Build_gradle.Versions.compose_version
import Build_gradle.Versions.koin_version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("com.squareup.sqldelight")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("koin")
}

group = "co.softov.morestuff"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "co.softov.morestuff.android"
        minSdk = 24
        targetSdk = 30
        versionCode = 2
        versionName = "0.2.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.1"
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

sqldelight {
    database("StuffDb") {
        packageName = "co.softov.morestuff.db"
        schemaOutputDirectory = file("src/main/sqldelight/databases")
    }
}

object Versions {

    const val compose_version = "1.0.1"
    const val koin_version = "3.1.2"
    const val accompanist_version = "0.16.1"

}

dependencies {
    implementation(project(":shared"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.20")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")

    // Androidx
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.activity:activity-ktx:1.2.0-beta02")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    implementation("androidx.core:core-ktx:1.5.0-alpha05")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")
    implementation("androidx.work:work-runtime-ktx:2.5.0-beta02")

    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // Navigation
    implementation("com.github.terrakok:cicerone:6.5")

    // UI
    implementation("com.yuyakaido.android:card-stack-view:2.3.4")

    // Compose
    implementation("androidx.compose.compiler:compiler:$compose_version")
    implementation("androidx.compose.runtime:runtime:$compose_version")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.foundation:foundation:$compose_version")
    implementation("androidx.compose.foundation:foundation-layout:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    implementation("androidx.compose.animation:animation:$compose_version")

    implementation("com.google.accompanist:accompanist-insets:$accompanist_version")
    implementation("com.google.accompanist:accompanist-insets-ui:$accompanist_version")


    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")

    // Firebase
    implementation("com.google.firebase:firebase-crashlytics:17.3.0")
    implementation("com.google.firebase:firebase-analytics:18.0.0")

    // SQLDelight
    implementation("com.squareup.sqldelight:android-driver:1.4.4")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.4.4")
    implementation("com.squareup.sqldelight:android-paging-extensions:1.4.4")
    testImplementation("com.squareup.sqldelight:sqlite-driver:1.4.4")

    testImplementation("org.xerial:sqlite-jdbc:3.8.10.2") {
        // Override the version of sqlite used by sqlite-driver to match Android API 24 (minSdkVersion)
        version {
            strictly("3.8.10.2")
        }
    }

    // Timber logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Koin Android
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-android-compat:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")

    // Testing
    testImplementation("junit:junit:4.13.1")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

}