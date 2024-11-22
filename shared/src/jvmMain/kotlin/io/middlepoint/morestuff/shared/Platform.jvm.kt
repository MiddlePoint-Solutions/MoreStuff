package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile
import java.io.File
import java.util.UUID

actual fun generateUUID(): String {
  return UUID.randomUUID().toString()
}

actual fun formatString(format: String, vararg args: Any): String {
  return String.format(format, *args)
}

actual fun requiresNotificationsPermission(): Boolean {
  TODO("Not yet implemented")
}

actual val platform: Platform
  get() = TODO("Not yet implemented")

actual fun createKmpFile(path: String): KmpFile = KmpFile(File(path))