import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.android.utils.Environment
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.jetbrains.compose.internal.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

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
  //alias(libs.plugins.sentry).apply(false) // Enable when support or noop is added for wasmJs
}

object Env {
  const val DEV = "debug"
  const val STAGING = "staging"
  const val RELEASE = "release"
}

kotlin {
  jvmToolchain(20)
  androidTarget()

  jvm("desktop") {
//    main()
    mainRun {
      mainClass.set("MainKt")
    }
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    outputModuleName = "composeApp"
    browser {
      commonWebpackConfig {
        outputFileName = "composeApp.js"
        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
          static = (static ?: mutableListOf()).apply {
            // Serve sources to debug inside browser
            add(project.rootDir.path)
            add(project.projectDir.path)
          }
        }
      }
    }
    binaries.executable()
  }

  // spotless:off
  val iOSBinaryFlags =
    listOf(
      "-linker-option", "-framework", "-linker-option", "Metal",
      "-linker-option", "-framework", "-linker-option", "CoreText",
      "-linker-option", "-framework", "-linker-option", "CoreGraphics",
    )
  // spotless:on

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

    pod("Sentry") {
      version = "8.49.0"
      linkOnly = true
      extraOpts += listOf("-compiler-option", "-fmodules")
    }

    framework {
      baseName = "ComposeApp"
      isStatic = true
      linkerOpts("-lsqlite3")
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.coroutines)

      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(libs.compose.ui.util)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.compose.material3.adaptive)

      implementation(compose.materialIconsExtended)
      implementation(libs.bundles.kotlinx)
      implementation(libs.stately.isolate)
      implementation(libs.stately.iso.collections)
      implementation(libs.ktor.core)
      //implementation(libs.ktor.client.cio)
      implementation(libs.ktor.client.logging)
      implementation(libs.ktor.client.content.negotiation)
      implementation(libs.ktor.serialization.kotlinx.json)
      implementation(libs.multiplatform.settings)
      implementation(libs.sqldelight.coroutines.extensions)
      implementation(libs.sqldelight.primitive.adapters)

      api(libs.arrow.core)
      api(libs.kermit)

      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.core)
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
      implementation(libs.androidx.navigation.compose)
//      api(libs.decompose.router)

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
      implementation(libs.coil.ktor3)
      implementation(libs.zoomable)
      implementation(libs.calf.permissions)
      implementation(libs.calf.filepicker)
      implementation(libs.calf.filepicker.coil)
      implementation(libs.calf.io)
      implementation(libs.filekit.dialogs)
      implementation(libs.filekit.dialogs.compose)
      implementation(libs.filekit.core)
      implementation(libs.filekit.coil)
      implementation(libs.open.ai)

      // About
      implementation(libs.aboutLibrariesCore)
      implementation(libs.aboutLibrariesCompose)

      // Supabase
      implementation(project.dependencies.platform(libs.supabase.bom))
      implementation(libs.supabase.auth)
      implementation(libs.supabase.postgres)
      implementation(libs.supabase.compose.auth)
      implementation(libs.supabase.compose.auth.ui)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.mockk.common)
      implementation(libs.multiplatform.settings.test)
    }

    val webMain by creating {
      dependsOn(commonMain.get())
    }

    val nonWebMain by creating {
      dependsOn(commonMain.get())
      dependencies {
        implementation(libs.sentry)
      }
    }

    androidMain {
      dependsOn(nonWebMain)
      dependencies {
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
        implementation(libs.androidx.lifecycle.viewmodel)
        api(libs.koin.android)
        implementation(libs.sqliteAndroid)
        implementation(libs.preferenceKtx)
        api(libs.workKtx)

        implementation(libs.googleServices)
        implementation(libs.androidx.splash)
      }
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

    iosMain {
      dependsOn(nonWebMain)
      dependencies {
        implementation(compose.foundation)
        implementation(libs.sqldelight.driver.native)
        implementation(libs.ktor.client.darwin)
      }
    }


    val desktopMain by getting {
      dependsOn(nonWebMain)
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.sqldelight.driver.desktop)
        implementation(libs.ktor.client.java)
        implementation(libs.kotlinx.coroutines.swing)
      }
    }

    wasmJsMain {
      dependsOn(webMain)
      dependencies {
        implementation(libs.sqldelight.driver.web)
        implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
        implementation(npm("sql.js", libs.versions.sqlJs.get()))
        implementation(devNpm("copy-webpack-plugin", libs.versions.webPackPlugin.get()))
      }
      resources.srcDir(layout.buildDirectory.dir("sqlite"))
    }
  }
}

val projectVersionName =
  providers.gradleProperty("VERSION_NAME").orNull
    ?: project.findProperty("app.morestuff.versionName") as? String
    ?: error("versionName undefined!")

val projectVersionCode =
  providers.gradleProperty("VERSION_CODE").orNull?.toInt()
    ?: (project.findProperty("app.morestuff.versionCode") as? String)?.toInt()
    ?: error("versionCode undefined!")

buildConfig {

  val localProperties = gradleLocalProperties(rootDir, providers)

  buildConfigField(name = "VERSION_NAME", value = projectVersionName)
  buildConfigField(name = "VERSION_CODE", value = projectVersionCode)

  buildConfigField(
    name = "DEBUG",
    value = provider {
      localProperties.getPropertyOrNull("DEBUG")?.toBoolean() ?: false
    }
  )

  // Supabase configs

  buildConfigField(
    name = "SUPABASE_URL",
    value = provider {
      getPropertyOrThrow(localProperties, "SUPABASE_URL")
    }
  )

  buildConfigField(
    name = "SUPABASE_KEY",
    value = provider {
      getPropertyOrThrow(localProperties, "SUPABASE_KEY")
    }
  )

  buildConfigField(
    name = "GOOGLE_SERVER_CLIENT_ID",
    value = provider {
      getPropertyOrThrow(localProperties, "GOOGLE_SERVER_CLIENT_ID")
    }
  )

  buildConfigField(
    name = "SENTRY_DSN",
    value = provider {
      getPropertyOrThrow(localProperties, "SENTRY_DSN")
    }
  )

  // Deeplinks

  buildConfigField(
    name = "APP_SCHEME",
    value = provider { property("app.morestuff.scheme") as String }
  )

  buildConfigField(
    name = "APP_HOST_LOGIN",
    value = provider { property("app.morestuff.host.login") as String }
  )

}


android {
  namespace = "io.middlepoint.morestuff.android"
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = "io.middlepoint.morestuff"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
    versionCode = projectVersionCode
    versionName = projectVersionName
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  applicationVariants.all {
    val variant = this
    variant.outputs
      .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
      .forEach { output ->
        output.outputFileName = "MoreStuff-${variant.versionName}.apk"
      }
  }

  androidResources {
    generateLocaleConfig = true
  }

  signingConfigs {
    getByName(Env.DEV) {
      storeFile = file("./debug.keystore")
      storePassword = "android"
      keyAlias = "AndroidDebugKey"
      keyPassword = "android"
    }

    create(Env.STAGING) {
      storeFile = file("./staging.keystore")
      storePassword = "StageKey"
      keyAlias = "staging"
      keyPassword = "StageKey"
    }
  }

  buildTypes {

    getByName(Env.RELEASE) {
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

    create(Env.STAGING) {
      initWith(getByName(Env.RELEASE))
      applicationIdSuffix = ".staging"
      versionNameSuffix = "-staging"
      signingConfig = signingConfigs.getByName(Env.STAGING)
      matchingFallbacks += listOf(Env.RELEASE, Env.DEV)
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
      generateAsync.set(true)
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

// See https://sqlite.org/download.html for the latest wasm build version
val sqlite = 3500000

val sqliteDownload = tasks.register("sqliteDownload", Download::class.java) {
  src("https://sqlite.org/2025/sqlite-wasm-$sqlite.zip")
  dest(layout.buildDirectory.dir("tmp"))
  onlyIfModified(true)
}

val sqliteUnzip = tasks.register("sqliteUnzip", Copy::class.java) {
  dependsOn(sqliteDownload)
  from(zipTree(layout.buildDirectory.dir("tmp/sqlite-wasm-$sqlite.zip"))) {
    include("sqlite-wasm-$sqlite/jswasm/**")
    exclude("**/*worker1*")

    eachFile {
      relativePath = RelativePath(true, *relativePath.segments.drop(2).toTypedArray())
    }
  }
  into(layout.buildDirectory.dir("sqlite"))
  includeEmptyDirs = false
}

tasks.named("wasmJsProcessResources").configure {
  dependsOn(sqliteUnzip)
}

// Extensions

fun Properties.getPropertyOrNull(key: String): String? =
  getProperty(key)?.takeIf { it.isNotBlank() }

fun getPropertyOrThrow(properties: Properties, key: String): String =
  properties.getPropertyOrNull(key)
    ?: System.getenv(key)?.takeIf { it.isNotBlank() }
    ?: providers.environmentVariable(key).orNull
    ?: error("$key not found!")


