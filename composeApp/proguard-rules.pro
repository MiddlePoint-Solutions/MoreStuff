# Tus reglas actuales
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

-keep class io.** { *; }
-keep class okio.** { *; }
-keep class kotlinx.coroutines.** { *; }

-keep class io.ktor.client.network.sockets.** { *; }
-keep class io.ktor.client.plugins.* { *; }
-keep class io.ktor.util.* { *;}
-keep class io.ktor.utils.io.* { *; }
-keep class java.lang.management.* { *; }


-keepattributes *Annotation*

-keep class kotlin.Metadata { *; }

-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.text.RegexOption { *; }

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal
-dontwarn java.lang.ClassValue
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-dontnote kotlinx.serialization.**


-dontwarn kotlinx.serialization.internal.ClassValueReferences


-keep class ch.qos.** { *; }
-dontwarn ch.qos.**
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**
-dontwarn ch.qos.logback.core.net.*


-dontwarn io.netty.**


-dontwarn com.google.common.collect.**
-dontwarn com.google.common.base.Converter
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.util.concurrent.**
-dontwarn com.google.common.eventbus.Subscriber
-dontwarn com.google.common.eventbus.SubscriberRegistry
-dontwarn com.google.common.hash.**
-dontwarn javax.lang.model.element.Modifier
-dontwarn sun.misc.Unsafe


-keep class com.fasterxml.jackson.** { *; }
-keep class com.fasterxml.jackson.databind.** { *; }
-keep class com.fasterxml.jackson.module.kotlin.** { *; }
-keep class com.fasterxml.jackson.databind.cfg.** { *; }


-keep class java.lang.Throwable {
  *** addSuppressed(...);
}