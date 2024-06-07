import com.android.build.gradle.options.parseBoolean

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.cocoapods)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.kotlin.parcelize)
}

buildConfig {
  buildConfigField(
    name = "DEBUG",
    provider { property("buildConfig.debug") as? Boolean ?: false }
  )
}

kotlin {
  jvmToolchain(20)

  androidTarget()

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

  cocoapods {
    summary = "Some description for the Shared Module"
    homepage = "Link to the Shared Module homepage"
    version = "1.0"
    ios.deploymentTarget = "16.0"
    podfile = project.file("../iosApp/Podfile")

    framework {
      baseName = "shared"
      isStatic = true
      linkerOpts("-lsqlite3")
//      export(libs.decompose.router)
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(compose.components.resources)
      implementation(libs.bundles.compose)
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
      implementation(libs.store5)
      implementation(libs.sqldelight.coroutines.extensions)
      implementation(libs.sqldelight.primitive.adapters)

      api(libs.arrow.core)
      api(libs.kermit)
      api(libs.koin.core)
      api(libs.koin.compose)
      api(libs.decompose.router)

      // You will probably need to also bring in decompose and essenty
      implementation(libs.decompose)
      implementation(libs.decompose.compose.multiplatform)
//      implementation(libs.essenty)
      implementation(libs.molecule.runtime)

      implementation(libs.kSoup)
      api(libs.constraintLayout.compose)

      api(libs.composeSettings.ui)
      api(libs.composeSettings.ui.extended)
      implementation(libs.settingsStoragePreferences)
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
//      api(compose.preview)
//      api(compose.uiTooling)
      implementation(libs.androidx.security.crypto)
      implementation(libs.sqldelight.driver.android)
      implementation(libs.ktor.client.okhttp)
      implementation(libs.androidx.test)
      implementation(libs.koin.android)
      implementation(libs.sqliteAndroid)
      implementation(libs.androidx.exifinterface)
      implementation(libs.preferenceKtx)
      api(libs.workKtx)
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
      implementation(libs.sqldelight.driver.native)
      implementation(libs.ktor.client.darwin)
    }
  }
}

android {
  namespace = "io.middlepoint.morestuff.android"
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  sourceSets["main"].manifest.srcFile("src/main/AndroidManifest.xml")
  sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
  sourceSets["main"].res.srcDirs("src/main/res", "src/commonMain/resources")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig { minSdk = libs.versions.android.sdk.min.get().toInt() }

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
