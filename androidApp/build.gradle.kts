@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidApplicationPlugin)
    alias(libs.plugins.kotlinPlugin)
    alias(libs.plugins.parcelizePlugin)
    alias(libs.plugins.sqldelightPlugin)
    alias(libs.plugins.googleServicesPlugin)
    alias(libs.plugins.crashlyticsPlugin)
    alias(libs.plugins.moleculePlugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.aboutLibrariesPlugin)
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
        compileSdk = 34
        minSdk = 26
        targetSdk = 34
        versionCode = 21
        versionName = "0.5.0"
        vectorDrawables {
            useSupportLibrary = true
        }
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
    databases {
        create("StuffDb") {
            packageName.set("co.softov.morestuff.db")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            verifyMigrations.set(true)
            dialect("app.cash.sqldelight:sqlite-3-18-dialect:2.0.0")
        }
    }
}
dependencies {
    implementation(project(":shared"))

    // AndroidX and Compose
    implementation(libs.bundles.android)
    implementation(platform(libs.androidxComposeBom))
    implementation(libs.bundles.compose)

    // Testing
    testImplementation(libs.bundles.testing)

    // Networking
    implementation(libs.bundles.networking)

    // SQL Delight
    implementation(libs.bundles.sqldelight)

    // Firebase
    implementation(platform(libs.firebaseBom))
    implementation(libs.bundles.firebase)

    // Utilities
    implementation(libs.bundles.utils)

    // About Libraries
    implementation(libs.bundles.aboutLibraries)


    debugImplementation(libs.uiTestManifest)
}
