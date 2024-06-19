import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
          static = (static ?: mutableListOf()).apply {
            // Serve sources to debug inside browser
            add(project.projectDir.path)
          }
        }
      }
    }
  }

  androidTarget()

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  jvm()

  compilerOptions {
    // Common compiler options applied to all Kotlin source sets
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinx.coroutines)
      implementation(libs.kermit)
      implementation(libs.koin.core)
    }

    androidMain.dependencies {
      implementation(libs.androidx.core)
      implementation(libs.androidx.exifinterface)
    }
  }
}

android {
  namespace = "io.middlepoint.morestuff.shared"
  compileSdk = libs.versions.android.sdk.compile.get().toInt()
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  defaultConfig {
    minSdk = libs.versions.android.sdk.min.get().toInt()
  }

}
