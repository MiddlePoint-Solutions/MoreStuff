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
    const val Staging = "staging"
    const val Release = "release"
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

android {

    namespace = "co.softov.morestuff.android"

    defaultConfig {
        applicationId = "io.middlepoint.morestuff"
        compileSdk = 34
        minSdk = 26
        targetSdk = 34
        versionCode = 24
        versionName = "0.5.3"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create(Env.Staging) {
            storeFile = file("./stage_key")
            storePassword = "StageKey"
            keyAlias = "staging"
            keyPassword = "StageKey"
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create(Env.Staging) {
            initWith(getByName(Env.Release))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            signingConfig = signingConfigs.getByName(Env.Staging)
            matchingFallbacks += listOf(Env.Release, Env.Dev)
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
        kotlinCompilerExtensionVersion = "1.5.4"
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

    implementation(platform(libs.androidxComposeBom))
    implementation(platform(libs.arrowBom))
    implementation(platform(libs.koinBom))
    implementation(platform(libs.firebaseBom))

    implementation(libs.bundles.android)
    implementation(libs.bundles.compose)
    testImplementation(libs.bundles.testing)
    implementation(libs.bundles.networking)
    implementation(libs.bundles.sqldelight)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.utils)
    implementation(libs.bundles.aboutLibraries)

    debugImplementation(libs.uiTestManifest)
}
