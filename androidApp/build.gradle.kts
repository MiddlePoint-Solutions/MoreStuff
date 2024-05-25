@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    alias(libs.plugins.androidApplicationPlugin)
//    alias(libs.plugins.kotlinPlugin)
//    alias(libs.plugins.sqldelightPlugin)
//    alias(libs.plugins.googleServicesPlugin)
//    alias(libs.plugins.crashlyticsPlugin)
//    alias(libs.plugins.moleculePlugin)
//    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.aboutLibrariesPlugin)
}

object Env {
    const val Dev = "debug"
    const val Staging = "staging"
    const val Release = "release"
}

kotlin {
    jvmToolchain(20)
    androidTarget()
}

android {

    namespace = "io.middlepoint.morestuff.android"

    defaultConfig {
        applicationId = "io.middlepoint.morestuff"
        compileSdk = 34
        minSdk = 26
        targetSdk = 34
        versionCode = 32
        versionName = "0.6.2"
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
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
        jniLibs.excludes.add("lib/mips/libsqlite3x.so")
        jniLibs.excludes.add("lib/mips64/libsqlite3x.so")
        jniLibs.excludes.add("lib/armeabi/libsqlite3x.so")
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

dependencies {
    implementation(project(":shared"))

//    implementation(platform(libs.androidxComposeBom))
//    implementation(platform(libs.arrowBom))
    implementation(platform(libs.koinBom))
    implementation(platform(libs.firebaseBom))

    // Android
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxDrawerlayout)
    implementation(libs.appcompat)
    implementation(libs.palette)
    implementation(libs.pagingKtx)
    implementation(libs.coreKtx)
    implementation(libs.activityKtx)
    implementation(libs.fragmentKtx)
    implementation(libs.runtimeKtx)
    implementation(libs.viewModel)
    implementation(libs.lottie)

//    Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.runtimeCompose)
    implementation(libs.viewModelCompose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.pagingCompose)
    implementation(libs.compose.ui)
    implementation(libs.composeViewBinding)
    implementation(libs.composeAnimation)
    implementation(libs.compose.material3)
    implementation(libs.composeMaterial3WindowSizeClass)
    implementation(libs.composeMaterialIcons)
    implementation(libs.composeMaterialIconsExtended)
    implementation(libs.composeTooling)
    implementation(libs.constraintLayoutCompose)
    implementation(libs.uiToolingPreview)
    implementation(libs.uiTooling)
    implementation(libs.androidx.exifinterface)

    // Testing
    testImplementation(libs.mockk)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.commons)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.testCore)
    testImplementation(libs.testRules)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttpLogging)
    implementation(libs.coilCompose)
    implementation(libs.kotlinx.serialization.json)

    // SQLdelight
//    implementation(libs.sqldelight.driver.android)
//    implementation(libs.sqldelight.coroutines.extensions)
//    implementation(libs.sqldelight.primitive.adapters)
//    implementation(libs.sqldelightAndroidPagingExt)
//    implementation(libs.sqliteAndroid)

    // Firebase
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseAnalytics)
    implementation(libs.googleServices)

    // Utils
    implementation(libs.reorderable)
    implementation(libs.preferenceKtx)
    implementation(libs.settingsUiM3)
    implementation(libs.settingsStoragePreferences)
    implementation(libs.decompose)
    implementation(libs.decompose.compose.multiplatform)
    implementation(libs.parcelable)
    implementation(libs.accompanistPermissions)
    implementation(libs.accompanistSystemUiController)
    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinReflect)
    implementation(libs.kotlinx.datetime)
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)
    implementation(libs.multiplatformSettingsNoArg)
    implementation(libs.koin.android)
    implementation(libs.koinAndroidxCompose)
    implementation(libs.timber)
    implementation(libs.telephoto)

    // About Libraries
    implementation(libs.aboutLibrariesCore)
    implementation(libs.aboutLibrariesCompose)


    debugImplementation(libs.uiTestManifest)
}
