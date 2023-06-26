import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
}

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {

    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //Logger
                implementation("io.github.aakira:napier:1.5.0")
                //Key-Value storage
                //implementation("com.russhwolf:multiplatform-settings:1.0.0")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")

                val decompose = "2.1.0-compose-experimental-alpha-02"

                // Decompose-router
//                implementation("io.github.xxfast:decompose-router:0.2.1")
//                implementation("com.arkivanov.decompose:decompose:$decompose")
//                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$decompose")
//                implementation("com.arkivanov.essenty:parcelable:1.1.0")
            }
        }

        val androidMain by getting
        val iosMain by getting
    }
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "co.softov.morestuff.shared.android"
}
dependencies {

}
