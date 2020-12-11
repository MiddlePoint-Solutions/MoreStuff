pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        mavenCentral()
    }
//    resolutionStrategy {
//        eachPlugin {
//            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
//                useModule("com.android.tools.build:gradle:4.1.0-rc03")
//            }
//        }
//    }
}
rootProject.name = "MoreStuff"


include(":androidApp")
include(":shared")

