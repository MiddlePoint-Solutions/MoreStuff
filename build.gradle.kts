buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.0")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.21")
        classpath ("io.insert-koin:koin-gradle-plugin:2.2.3")
    }
}
group = "co.softov.morestuff"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}