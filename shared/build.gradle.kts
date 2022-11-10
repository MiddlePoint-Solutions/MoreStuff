import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
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
                implementation("com.russhwolf:multiplatform-settings:0.9")
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
}
