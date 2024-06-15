import android.os.Build
import java.util.Locale


actual typealias File = java.io.File

actual fun generateUUID(): String = java.util.UUID.randomUUID().toString()

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(Locale.US, format, *args)
}

actual fun requiresNotificationsPermission(): Boolean =
  Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU