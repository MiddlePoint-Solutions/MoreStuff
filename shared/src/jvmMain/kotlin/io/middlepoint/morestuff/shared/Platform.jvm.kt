package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile
import java.io.File

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(format, *args)
}

actual fun requiresNotificationsPermission(): Boolean {
  return false
}

actual val platform: Platform
  get() = Platform.Desktop

actual fun createKmpFile(path: String): KmpFile = KmpFile(File(path))