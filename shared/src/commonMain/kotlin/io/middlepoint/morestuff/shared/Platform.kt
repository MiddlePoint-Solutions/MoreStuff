package io.middlepoint.morestuff.shared

import org.koin.core.module.Module

enum class Platform {
  Android,
  iOS,
  Desktop,
  Web
}

expect class File(path: String) {
  fun exists(): Boolean
  fun delete(): Boolean
}

expect fun generateUUID(): String

expect fun formatString(format: String, vararg args: Any): String

expect fun requiresNotificationsPermission(): Boolean

expect val sharedModule: Module

expect val platform: Platform