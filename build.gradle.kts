
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
    }
}
group = "co.softov.morestuff"
version = "0.1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}