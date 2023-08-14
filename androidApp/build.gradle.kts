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
        versionCode = 18
        versionName = "0.4.7"
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
        }
    }
}

dependencies {
    implementation(project(":shared"))
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
    implementation(libs.sqldelightPrimitiveAdapters)
    implementation(libs.sqldelightAndroidPagingExt)
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
    implementation(libs.activityCompose)
    implementation(libs.konfettiCompose)
    implementation(libs.preferenceKtx)

    implementation(libs.aboutLibrariesCore)
    implementation(libs.aboutLibrariesCompose)

    testImplementation(libs.testCore)
    testImplementation(libs.testRules)
    testImplementation(libs.espressoCore)
    testImplementation(libs.koinTest)
    testImplementation(libs.koinTestJunit)
    testImplementation(libs.junit)
    testImplementation(libs.extJUnit)
}
