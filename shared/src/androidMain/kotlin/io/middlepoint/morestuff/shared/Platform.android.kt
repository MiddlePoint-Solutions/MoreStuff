package io.middlepoint.morestuff.shared

import java.util.Locale


actual typealias File = java.io.File

actual fun generateUUID(): String = java.util.UUID.randomUUID().toString()

actual typealias Uri = android.net.Uri