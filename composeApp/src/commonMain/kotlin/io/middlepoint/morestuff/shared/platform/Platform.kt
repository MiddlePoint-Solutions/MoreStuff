package io.middlepoint.morestuff.shared.platform

import io.github.vinceglb.filekit.PlatformFile
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@OptIn(ExperimentalUuidApi::class)
fun generateUUID(): String = Uuid.random().toString()

expect fun formatString(format: String, vararg args: Any): String

expect fun requiresNotificationsPermission(): Boolean

expect val platform: Platform

expect fun createKmpFile(path: String): PlatformFile
