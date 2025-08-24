package io.middlepoint.morestuff.shared.platform

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.utils.toPath
import org.w3c.files.File

actual fun formatString(format: String, vararg args: Any): String {
  //    TODO("Not yet implemented")
    return format
}

actual fun requiresNotificationsPermission(): Boolean {
//    TODO("Not yet implemented")
  return true
}

actual val platform: Platform
  get() = Platform.Web

actual fun createKmpFile(path: String): PlatformFile = PlatformFile(File(JsArray(), "test")) //TODO()