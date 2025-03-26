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

-dontwarn io.ktor.utils.io.jvm.nio.WritingKt