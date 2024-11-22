package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile

enum class Platform {
  Android,
  iOS,
  Desktop,
  Web
}

enum class MediaFolder(val folderName: String) {
  Files("files"),
  Images("images")
}

expect fun generateUUID(): String

expect fun formatString(format: String, vararg args: Any): String

expect fun requiresNotificationsPermission(): Boolean

expect val platform: Platform

expect fun createKmpFile(path: String) : KmpFile
