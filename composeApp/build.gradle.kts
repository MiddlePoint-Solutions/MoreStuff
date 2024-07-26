import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.aboutLibrariesPlugin)
}

object Env {
    const val Dev = "debug"
    const val Staging = "staging"
    const val Release = "release"
}

buildConfig {
    buildConfigField(
        name = "DEBUG",
        provider { property("buildConfig.debug") as? Boolean ?: false }
    )

    buildConfigField(
        name = "VERSION_NAME",
        provider {
            property("buildConfig.versionName") as? String
                ?: error("buildConfig.versionName undefined!")
        }
    )

    buildConfigField(
        name = "VERSION_CODE",
        provider {
            (property("buildConfig.versionCode") as? String)?.toInt()
                ?: error("buildConfig.versionCode undefined!")
        }
    )
}

kotlin {
    jvmToolchain(20)

    // TODO: once we have support for SqlDelight & Arrow
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "composeApp"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "composeApp.js"
//                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
//                    static = (static ?: mutableListOf()).apply {
//                        // Serve sources to debug inside browser
//                        add(project.projectDir.path)
//                    }
//                }
//            }
//        }
//        binaries.executable()
//    }

    androidTarget()

    // spotless:off
    val iOSBinaryFlags =
        listOf(
            "-linker-option", "-framework", "-linker-option", "Metal",
            "-linker-option", "-framework", "-linker-option", "CoreText",
            "-linker-option", "-framework", "-linker-option", "CoreGraphics",
        )
    // spotless:on

    jvm("desktop")

    iosX64 { binaries.forEach { it.freeCompilerArgs += iOSBinaryFlags } }
    iosArm64 { binaries.forEach { it.freeCompilerArgs += iOSBinaryFlags } }
    iosSimulatorArm64 { binaries.forEach { it.freeCompilerArgs += iOSBinaryFlags } }

    applyDefaultHierarchyTemplate()

    compilerOptions {
        // Common compiler options applied to all Kotlin source sets
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    cocoapods {
        summary = "MoreStuff Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        name = "ComposeApp"

        pod("lottie-ios") {
            version = "4.4.0"
            linkOnly = true
        }

        framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.decompose.router)
            linkerOpts("-lsqlite3")
        }
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(projects.shared)

            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.compose.ui.util)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(compose.materialIconsExtended)
            implementation(libs.bundles.kotlinx)
            implementation(libs.stately.isolate)
            implementation(libs.stately.iso.collections)
            implementation(libs.ktor.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.primitive.adapters)

            api(libs.arrow.core)
            api(libs.kermit)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)
            api(libs.decompose.router)

            // You will probably need to also bring in decompose and essenty
            implementation(libs.decompose)
            implementation(libs.decompose.compose.multiplatform)
            implementation(libs.molecule.runtime)

            implementation(libs.kSoup)
            api(libs.constraintLayout.compose)

            implementation(libs.composeSettings.ui)
            implementation(libs.composeSettings.ui.extended)
            implementation(libs.settingsStoragePreferences)
            implementation(libs.sonner)

            implementation(libs.reorderable)
            implementation(libs.coil.compose)
            implementation(libs.zoomable)
            implementation(libs.kottie)
            implementation(libs.calf.permissions)
            implementation(libs.calf.filepicker)
            implementation(libs.calf.filepicker.coil)

            // About
            implementation(libs.aboutLibrariesCore)
            implementation(libs.aboutLibrariesCompose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockk.common)
            implementation(libs.multiplatform.settings.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core)
            implementation(libs.androidx.exifinterface)

            api(compose.preview)
            api(compose.uiTooling)
            implementation(libs.androidx.security.crypto)
            implementation(libs.sqldelight.driver.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.test)
            api(libs.koin.android)
            implementation(libs.sqliteAndroid)
            implementation(libs.preferenceKtx)
            api(libs.workKtx)

            // Firebase
            implementation(project.dependencies.platform(libs.firebaseBom))
            implementation(libs.firebaseCrashlytics)
            implementation(libs.firebaseAnalytics)
            implementation(libs.googleServices)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlin.test.junit5)
                implementation(libs.mockk)
                implementation(libs.junit.jupiter)
                implementation(libs.junit.platform.commons)
                implementation(libs.koinTest)
                implementation(libs.koinTestJunit)
            }
        }

        iosMain.dependencies {
            implementation(compose.foundation)
            implementation(libs.sqldelight.driver.native)
            implementation(libs.ktor.client.darwin)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.driver.desktop)
            implementation(libs.kotlinx.coroutines.swing)
        }

        // TODO: Enable once we have support from SqlDelight & Arrow
//        wasmJsMain.dependencies {
//        }
    }
}

android {
    namespace = "io.middlepoint.morestuff.android"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "io.middlepoint.morestuff"
        compileSdk = 34
        minSdk = 26
        targetSdk = 34
        versionCode = 32
        versionName = "0.6.2"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create(Env.Staging) {
            storeFile = file("./stage_key")
            storePassword = "StageKey"
            keyAlias = "staging"
            keyPassword = "StageKey"
        }
    }

    buildTypes {

        getByName(Env.Release) {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create(Env.Staging) {
            initWith(getByName(Env.Release))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            signingConfig = signingConfigs.getByName(Env.Staging)
            matchingFallbacks += listOf(Env.Release, Env.Dev)
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
        jniLibs.excludes.add("lib/mips/libsqlite3x.so")
        jniLibs.excludes.add("lib/mips64/libsqlite3x.so")
        jniLibs.excludes.add("lib/armeabi/libsqlite3x.so")
    }

    testOptions { unitTests.all { it.useJUnitPlatform() } }
}

sqldelight {
    databases {
        create("StuffDb") {
            packageName.set("io.middlepoint.morestuff.db")
            dialect(libs.sqldelight.sqlite.dialect)
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}

// AboutLibraries
configure<AboutLibrariesExtension> {
    registerAndroidTasks = false
}

tasks.withType(KotlinCompile::class.java) {
    dependsOn("exportLibraryDefinitions")
}
