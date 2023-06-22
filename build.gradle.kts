import java.net.URI

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(co.softov.morestuff.buildsrc.Libs.androidGradlePlugin)
        classpath(co.softov.morestuff.buildsrc.Libs.Kotlin.gradlePlugin)
        classpath(co.softov.morestuff.buildsrc.Libs.Google.googleServices)
        classpath(co.softov.morestuff.buildsrc.Libs.Firebase.gradlePlugin)
        classpath(co.softov.morestuff.buildsrc.Libs.Sqldelight.gradlePlugin)
        classpath(co.softov.morestuff.buildsrc.Libs.Kotlin.serialization)
        classpath(co.softov.morestuff.buildsrc.Libs.Koin.gradlePlugin)
        classpath(co.softov.morestuff.buildsrc.Libs.moleculeGradlePlugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
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