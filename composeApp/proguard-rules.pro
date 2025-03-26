-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile
-keeppackagenames org.jsoup.nodes

-keep class io.middlepoint.morestuff.shared.domain.model.OpenGraphResult

-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable

-dontwarn java.sql.JDBCType
-dontwarn com.alorma.compose.settings.storage.base.ValueProviderKt

-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

-keep class io.ktor.** { *; }
-keep class io.ktor.utils.io.** { *; }
-keep class io.ktor.utils.io.jvm.nio.** { *; }

-keep class coil3.** { *; }
-keep class coil3.network.ktor2.** { *; }

-dontwarn java.lang.management.**
-keep class java.lang.management.** { *; }

-keep class io.ktor.util.debug.** { *; }