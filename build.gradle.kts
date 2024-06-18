//plugins {
//    alias(libs.plugins.androidApplicationPlugin) apply false
//    alias(libs.plugins.kotlinPlugin) apply false
//    alias(libs.plugins.parcelizePlugin) apply false
//    alias(libs.plugins.sqldelightPlugin) apply false
//    alias(libs.plugins.googleServicesPlugin) apply false
//    alias(libs.plugins.crashlyticsPlugin) apply false
//    alias(libs.plugins.moleculePlugin) apply false
//    alias(libs.plugins.kotlinSerialization) apply false
//    alias(libs.plugins.aboutLibrariesPlugin) apply false
//    id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0") apply false
//}

import com.diffplug.gradle.spotless.SpotlessExtension

group = "io.middlepoint.morestuff"
version = "0.1.0-SNAPSHOT"

plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.kotlin.cocoapods).apply(false)
  alias(libs.plugins.kotlin.parcelize).apply(false)
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.android.library).apply(false)
  alias(libs.plugins.compose).apply(false)
  alias(libs.plugins.compose.compiler).apply(false)
  alias(libs.plugins.buildConfig).apply(false)
  alias(libs.plugins.kotlinx.serialization).apply(false)
  alias(libs.plugins.sqldelight).apply(false)
  alias(libs.plugins.spotless).apply(false)
  alias(libs.plugins.aboutLibrariesPlugin).apply(false)
}

allprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
  configure<SpotlessExtension> {
    kotlin {
      ktfmt(libs.versions.ktfmt.get()).googleStyle()
      target("src/**/*.kt")
      targetExclude("${layout.buildDirectory}/**/*.kt")
    }
    kotlinGradle {
      ktfmt(libs.versions.ktfmt.get()).googleStyle()
      target("*.kts")
      targetExclude("${layout.buildDirectory}/**/*.kts")
      toggleOffOn()
    }
    format("xml") {
      target("src/**/*.xml")
      targetExclude("**/build/", ".idea/")
      trimTrailingWhitespace()
      endWithNewline()
    }
  }
}
