package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile

actual fun formatString(format: String, vararg args: Any): String {
    TODO("Not yet implemented")
}

actual fun requiresNotificationsPermission(): Boolean {
    TODO("Not yet implemented")
}

actual val platform: Platform
  get() = Platform.Web

actual fun createKmpFile(path: String): KmpFile {
  TODO("Not yet implemented")
}