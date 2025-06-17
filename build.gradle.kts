import com.diffplug.gradle.spotless.SpotlessExtension

group = "io.middlepoint.morestuff"

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
  alias(libs.plugins.sentry).apply(false)
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
