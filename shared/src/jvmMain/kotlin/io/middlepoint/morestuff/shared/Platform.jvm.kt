package io.middlepoint.morestuff.shared

import io.github.vinceglb.filekit.PlatformFile
import java.io.File

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(format, *args)
}

actual fun requiresNotificationsPermission(): Boolean {
  // TODO: this needs to be handled
  return false
}

actual val platform: Platform
  get() = Platform.Desktop

actual fun createKmpFile(path: String): PlatformFile = PlatformFile(File(path))