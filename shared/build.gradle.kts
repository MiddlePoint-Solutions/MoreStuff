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
        binaries {
            framework {
                baseName = "RssReader"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //Logger
                implementation("io.github.aakira:napier:1.5.0")
                //Key-Value storage
                implementation("com.russhwolf:multiplatform-settings:0.7.7")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }

        val iosMain by getting
    }
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 23
        targetSdk = 30
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

val packForXcode by tasks.creating(Sync::class) {
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)
    val targetDir = File(buildDir, "xcode-frameworks")

    group = "build"
    dependsOn(framework.linkTask)
    inputs.property("mode", mode)

    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)
