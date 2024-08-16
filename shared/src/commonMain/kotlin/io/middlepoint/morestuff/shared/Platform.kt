package io.middlepoint.morestuff.shared

enum class Platform {
  Android,
  iOS,
  Desktop,
  Web
}

expect fun generateUUID(): String

expect fun formatString(format: String, vararg args: Any): String

expect fun requiresNotificationsPermission(): Boolean

expect val platform: Platform