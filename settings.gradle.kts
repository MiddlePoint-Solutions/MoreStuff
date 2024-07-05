rootProject.name = "MoreStuff"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

include(":composeApp")
include(":shared")
//include(":app:android")
include(":iosApp")
