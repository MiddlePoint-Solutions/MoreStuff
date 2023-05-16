@file:Suppress("UnstableApiUsage")

import co.softov.morestuff.buildsrc.Libs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("android")
    id("com.android.application")
    id("kotlin-parcelize")
    id("com.squareup.sqldelight")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("koin")
    id("app.cash.molecule")
}

group = "io.middlepoint.morestuff"
version = "1.0"

object Env {
    const val Dev = "debug"
    const val Release = "release"
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

android {

    namespace = "co.softov.morestuff.android"

    defaultConfig {
        applicationId = "io.middlepoint.morestuff"
        compileSdk = 33
        minSdk = 25
        targetSdk = 33
        versionCode = 9
        versionName = "0.3.6"
    }

    buildTypes {

        getByName(Env.Release) {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
        kotlinCompilerExtensionVersion = "1.4.6"
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}


tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"

        // Generate reports with:
        // ./gradlew assembleRelease -Pmyapp.enableComposeCompilerReports=true

        if (project.findProperty("myapp.enableComposeCompilerReports") == "true") {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
            )
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                        project.buildDir.absolutePath + "/compose_metrics"
            )
        }
    }


}

sqldelight {
    database("StuffDb") {
        packageName = "co.softov.morestuff.db"
        schemaOutputDirectory = file("src/main/sqldelight/databases")
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")

    // Kotlin
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.reflect)
    implementation(Libs.Kotlinx.datetime)

    // Arrow
    implementation(platform(Libs.Arrow.bom))
    implementation(Libs.Arrow.core)

    // Androidx
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.Fragment.fragmentKtx)
    implementation(Libs.AndroidX.Activity.activityKtx)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation(Libs.AndroidX.Lifecycle.runtimeKtx)
    implementation(Libs.AndroidX.Lifecycle.runtimeCompose)
    implementation(Libs.AndroidX.Lifecycle.viewModel)
    implementation(Libs.AndroidX.Lifecycle.viewModelCompose)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.preferenceKtx)
    implementation(Libs.AndroidX.pagingKtx)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.workKtx)

    // Navigation
    implementation("com.github.terrakok:cicerone:7.1")

    // Modal Drawer Layout
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")

    // UI
    implementation(Libs.Google.material)
    // TODO: import when available
//    implementation("com.alexstyl.swipeablecard:swipeablecard:0.2.0")

    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    // Compose
    implementation(platform(Libs.AndroidX.Compose.bom))
    implementation(Libs.AndroidX.Compose.activityCompose)
    implementation(Libs.AndroidX.Compose.runtime)
    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Compose.viewBinding)
    implementation(Libs.AndroidX.Compose.foundation)
    implementation(Libs.AndroidX.Compose.foundationLayout)
    implementation(Libs.AndroidX.Compose.material3)
    implementation(Libs.AndroidX.Compose.materialIcons)
    implementation(Libs.AndroidX.Compose.materialIconsExtended)
    implementation(Libs.AndroidX.Compose.tooling)
    implementation(Libs.AndroidX.Compose.animation)
    implementation(Libs.AndroidX.Compose.paging)

    // Alorma Compose Settings
    implementation("com.github.alorma:compose-settings-ui-m3:0.22.0")
    implementation("com.github.alorma:compose-settings-storage-preferences:0.22.0")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation(Libs.Accompanist.insetsUi)
    implementation(Libs.Accompanist.pager)
    implementation(Libs.Accompanist.pagerIndicators)
    implementation(Libs.Accompanist.permissions)

    // Firebase
    implementation(platform(Libs.Firebase.bom))
    implementation(Libs.Firebase.crashlytics)
    implementation(Libs.Firebase.analytics)

    // SQLDelight
    implementation(Libs.Sqldelight.androidDriver)
    implementation(Libs.Sqldelight.coroutinesJvmExt)
    implementation(Libs.Sqldelight.androidPagingExt)
    implementation(Libs.Sqldelight.androidPaging3Ext)
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
    testImplementation(Libs.Koin.testJunit)

    // Testing
//    testImplementation(Libs.Test.junit)
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
    testImplementation("org.junit.platform:junit-platform-commons:1.5.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")

    //Test flow whit Turbine
    testImplementation ("app.cash.turbine:turbine:0.12.3")

    //Multiplatform settings
    //implementation("com.russhwolf:multiplatform-settings:1.0.0")
    implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")

}