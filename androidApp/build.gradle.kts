import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    id("com.squareup.sqldelight")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
group = "co.softov.morestuff"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "co.softov.morestuff.android"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.1.0"
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
    implementation(project(":shared"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")

    // Androidx
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.activity:activity-ktx:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")

    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")

    // Firebase
    implementation("com.google.firebase:firebase-crashlytics:17.2.2")
    implementation("com.google.firebase:firebase-analytics:17.5.0")

    // SQLDelight
    implementation("com.squareup.sqldelight:android-driver:1.4.3")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.4.3")
    implementation("com.squareup.sqldelight:android-paging-extensions:1.4.3")
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.3")

    // Timber logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Koin Android
    implementation("org.koin:koin-android:2.2.0-rc-1")
    implementation("org.koin:koin-androidx-scope:2.2.0-rc-1")
    implementation("org.koin:koin-androidx-viewmodel:2.2.0-rc-1")
    implementation("org.koin:koin-androidx-ext:2.2.0-rc-1")
}