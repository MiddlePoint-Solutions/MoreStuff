package co.softov.morestuff.buildsrc

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.4.0"
    const val moleculeGradlePlugin = "app.cash.molecule:molecule-gradle-plugin:0.6.0"
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.2.0"

    object Accompanist {
        private const val version = "0.26.3-beta"
        const val insetsUi = "com.google.accompanist:accompanist-insets-ui:$version"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
        const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
        const val permissions = "com.google.accompanist:accompanist-permissions:$version"
    }

    object Kotlin {
        private const val version = "1.7.20"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    }

    object Arrow {
        private const val version = "1.1.3"
        const val bom = "io.arrow-kt:arrow-stack:$version"
        const val core = "io.arrow-kt:arrow-core"
    }

    object Kotlinx {
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
    }

    object Coroutines {
        private const val version = "1.7.0"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object OkHttp {
        private const val version = "4.9.1"
        const val okhttp = "com.squareup.okhttp3:okhttp:$version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Sqldelight {
        const val version = "1.5.5"
        const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:$version"
        const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
        const val coroutinesJvmExt = "com.squareup.sqldelight:coroutines-extensions-jvm:$version"
        const val androidPagingExt = "com.squareup.sqldelight:android-paging-extensions:$version"
        const val androidPaging3Ext = "com.squareup.sqldelight:android-paging3-extensions:$version"

        object Test {
            const val sqlDriver = "com.squareup.sqldelight:sqlite-driver:$version"
        }
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:28.4.1"
        const val gradlePlugin = "com.google.firebase:firebase-crashlytics-gradle:2.7.1"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
    }

    object Google {
        const val googleServices = "com.google.gms:google-services:4.3.10"
        const val material = "com.google.android.material:material:1.2.1"
    }

    object Koin {
        private const val version = "3.3.0"
        const val gradlePlugin = "io.insert-koin:koin-gradle-plugin:3.2.0"
        const val android = "io.insert-koin:koin-android:$version"
        const val androidCompat = "io.insert-koin:koin-android-compat:$version"
        const val androidxCompose = "io.insert-koin:koin-androidx-compose:$version"

        const val test = "io.insert-koin:koin-test:3.2.2"
        const val testJunit = "io.insert-koin:koin-test-junit5:3.2.2"
    }

    object Test {
        const val junit = "junit:junit:4.13.1"
        const val mokitoKtx = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    }

    object Log {
        const val timber = "com.jakewharton.timber:timber:5.0.0"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val palette = "androidx.palette:palette:1.0.0"
        const val pagingKtx = "androidx.paging:paging-runtime-ktx:3.0.1"
        const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"

        const val workKtx = "androidx.work:work-runtime-ktx:2.7.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"

        const val coreKtx = "androidx.core:core-ktx:1.6.0"

        object Activity {
            const val activityKtx = "androidx.activity:activity-ktx:1.3.1"
        }

        object Fragment {
            const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.6"
        }

        object Constraint {
            const val constraintLayoutCompose = "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"
        }

        object Lifecycle {
            private const val version = "2.5.1"
            const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Compose {
            private const val version = "2022.10.00"
            const val bom = "androidx.compose:compose-bom:$version"

            const val runtime = "androidx.compose.runtime:runtime"
            const val foundation = "androidx.compose.foundation:foundation"
            const val foundationLayout = "androidx.compose.foundation:foundation-layout"
            const val paging = "androidx.paging:paging-compose:1.0.0-alpha16"

            const val ui = "androidx.compose.ui:ui"
            const val viewBinding = "androidx.compose.ui:ui-viewbinding"
            const val animation = "androidx.compose.animation:animation"
            const val material = "androidx.compose.material:material"
            const val material3 = "androidx.compose.material3:material3"

            const val materialIconsExtended = "androidx.compose.material:material-icons-extended"

            const val tooling = "androidx.compose.ui:ui-tooling"

            const val navigation = "androidx.navigation:navigation-compose:2.4.0-alpha08"
            const val activityCompose = "androidx.activity:activity-compose:1.4.0"

            object Lifecycle {
                const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07"
            }

            object Test {
                private const val version = "1.4.0"
                const val core = "androidx.test:core:$version"
                const val rules = "androidx.test:rules:$version"

                object Ext {
                    private const val version = "1.1.2"
                    const val junit = "androidx.test.ext:junit-ktx:$version"
                }

                const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
            }
        }
    }
}
