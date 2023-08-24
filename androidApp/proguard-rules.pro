-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile
-keeppackagenames org.jsoup.nodes

-keep class co.softov.morestuff.android.domain.model.OpenGraphResult

-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable

-dontwarn java.sql.JDBCType
