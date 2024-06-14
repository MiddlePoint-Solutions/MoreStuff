import java.util.Locale


actual typealias File = java.io.File

actual fun generateUUID(): String = java.util.UUID.randomUUID().toString()

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(Locale.US, format, *args)
}