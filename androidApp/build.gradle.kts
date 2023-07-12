@file:Suppress("UnstableApiUsage")

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
    id("org.jetbrains.kotlin.plugin.serialization")
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
        versionCode = 15
        versionName = "0.4.4"
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
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    packagingOptions {
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
    database("StuffDb") {
        packageName = "co.softov.morestuff.db"
        schemaOutputDirectory = file("src/main/sqldelight/databases")

        verifyMigrations = true
    }
}

dependencies {
    implementation(project(":shared"))
    coreLibraryDesugaring(libs.jdkDesugar)
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxLifecycleRuntimeKtx)
    implementation(libs.androidxActivityCompose)
    implementation(platform(libs.androidxComposeBom))
    implementation(libs.androidxLegacySupportV4)
    implementation(libs.androidxDrawerlayout)
    implementation(libs.reorderable)
    implementation(libs.settingsUiM3)
    implementation(libs.settingsStoragePreferences)
    implementation(libs.uiToolingPreview)
    implementation(libs.uiTooling)
    implementation(libs.sqliteJdbc)
    implementation(libs.androidAppStartup)

    testImplementation(libs.mockk)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.junitPlatformCommons)
    testImplementation(libs.kotlinxCoroutinesTest)
    testImplementation(libs.turbine)
    implementation(libs.multiplatformSettingsNoArg)
    implementation(libs.jsoup)
    implementation(libs.coilCompose)
    implementation(libs.kotlinxSerializationJson)
    implementation(libs.kotlinSerialization)
    debugImplementation(libs.uiTestManifest)
    implementation(libs.decompose)
    implementation(libs.extensionsComposeJetpack)
    implementation(libs.parcelable)
    implementation(libs.accompanistInsetsUi)
    implementation(libs.accompanistPermissions)
    implementation(libs.accompanistSystemUiController)
    implementation(libs.kotlinStdlib)
    implementation(libs.kotlinSerialization)
    implementation(libs.kotlinReflect)
    implementation(platform(libs.arrowBom))
    implementation(libs.arrowCore)
    implementation(libs.kotlinxDatetime)
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)
    testImplementation(libs.coroutinesTest)
    implementation(libs.okhttp)
    implementation(libs.okhttpLogging)
    implementation(libs.sqldelightAndroidDriver)
    implementation(libs.sqldelightCoroutinesJvmExt)
    implementation(libs.sqldelightAndroidPagingExt)
    implementation(libs.sqldelightAndroidPaging3Ext)
    testImplementation(libs.sqldelightTestSqlDriver)
    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseAnalytics)
    implementation(libs.googleServices)
    implementation(libs.googleMaterial)
    implementation(libs.koinAndroid)
    implementation(libs.koinAndroidCompat)
    implementation(libs.koinAndroidxCompose)

    implementation(libs.timber)
    implementation(libs.appcompat)
    implementation(libs.palette)
    implementation(libs.pagingKtx)
    implementation(libs.preferenceKtx)
    implementation(libs.workKtx)
    implementation(libs.constraintLayout)
    implementation(libs.coreKtx)
    implementation(libs.activityKtx)
    implementation(libs.fragmentKtx)
    implementation(libs.constraintLayoutCompose)
    implementation(libs.runtimeKtx)
    implementation(libs.runtimeCompose)
    implementation(libs.viewModel)
    implementation(libs.viewModelCompose)
    implementation(libs.composeRuntime)
    implementation(libs.composeFoundation)
    implementation(libs.composeFoundationLayout)
    implementation(libs.pagingCompose)
    implementation(libs.composeUi)
    implementation(libs.composeViewBinding)
    implementation(libs.composeAnimation)
    implementation(libs.composeMaterial3)
    implementation(libs.constraintLayoutCompose)
    implementation(libs.composeMaterialIcons)
    implementation(libs.composeMaterialIconsExtended)
    implementation(libs.composeTooling)
    implementation(libs.navigationCompose)
    implementation(libs.activityCompose)
    implementation(libs.lottiCompose)
    testImplementation(libs.testCore)
    testImplementation(libs.testRules)
    testImplementation(libs.espressoCore)
    testImplementation(libs.koinTest)
    testImplementation(libs.koinTestJunit)
    testImplementation(libs.junit)
    testImplementation(libs.extJUnit)

}
