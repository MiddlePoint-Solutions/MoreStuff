buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath(libs.androidGradlePlugin)
        classpath(libs.gradlePlugin)
        classpath(libs.googleServices)
        classpath(libs.firebaseCrashlyticsPlugin)
        classpath(libs.sqldelightPlugin)
        classpath(libs.kotlinSerialization)
        classpath(libs.koinPlugin)
        classpath(libs.moleculePlugin)
        classpath(libs.aboutLibrariesPlugin)
    }
}
group = "co.softov.morestuff"
version = "0.1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}