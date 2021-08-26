import co.softov.morestuff.buildsrc.Libs
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

dependencies {
    //implementation(project(":shared"))

    // Kotlin
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.reflect)
    implementation(Libs.Kotlinx.datetime)

    // Androidx
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.Fragment.fragmentKtx)
    implementation(Libs.AndroidX.Activity.activityKtx)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation(Libs.AndroidX.Lifecycle.runtimeKtx)
    implementation(Libs.AndroidX.Lifecycle.extensions)
    implementation(Libs.AndroidX.Lifecycle.viewModel)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.preferenceKtx)
    implementation(Libs.AndroidX.pagingKtx)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.workKtx)

    // Navigation
    implementation("com.github.terrakok:cicerone:6.5")

    // UI
    implementation(Libs.Google.material)
    implementation("com.yuyakaido.android:card-stack-view:2.3.4")

    // Compose
    implementation(Libs.AndroidX.Compose.compiler)
    implementation(Libs.AndroidX.Compose.runtime)
    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Compose.foundation)
    implementation(Libs.AndroidX.Compose.foundationLayout)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.materialIconsExtended)
    implementation(Libs.AndroidX.Compose.tooling)
    implementation(Libs.AndroidX.Compose.animation)
    implementation(Libs.AndroidX.Compose.Lifecycle.viewModelCompose)

    implementation(Libs.Accompanist.insets)
    implementation(Libs.Accompanist.insetsUi)
    implementation(Libs.Accompanist.pager)

    // Firebase
    implementation(Libs.Firebase.crashlytics)
    implementation(Libs.Firebase.analytics)

    // SQLDelight
    implementation(Libs.Sqldelight.androidDriver)
    implementation(Libs.Sqldelight.coroutinesJvmExt)
    implementation(Libs.Sqldelight.androidPagingExt)
    testImplementation(Libs.Sqldelight.Test.sqlDriver)

    testImplementation("org.xerial:sqlite-jdbc:3.8.10.2") {
        // Override the version of sqlite used by sqlite-driver to match Android API 24 (minSdkVersion)
        version {
            strictly("3.8.10.2")
        }
    }

    // Timber logging
    implementation(Libs.Log.timber)

    // Koin Android
    implementation(Libs.Koin.android)
    implementation(Libs.Koin.androidCompat)
    implementation(Libs.Koin.androidxCompose)
    testImplementation(Libs.Koin.test)

    // Testing
    testImplementation(Libs.Test.junit)
    testImplementation(Libs.Test.mokitoKtx)

    coreLibraryDesugaring(Libs.jdkDesugar)
}