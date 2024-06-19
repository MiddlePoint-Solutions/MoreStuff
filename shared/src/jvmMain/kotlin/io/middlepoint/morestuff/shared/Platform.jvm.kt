package io.middlepoint.morestuff.shared

import java.util.UUID

actual class File actual constructor(path: String) {
    actual fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun delete(): Boolean {
        TODO("Not yet implemented")
    }
}

actual fun generateUUID(): String {
    return UUID.randomUUID().toString()
}

actual fun formatString(format: String, vararg args: Any): String {
    return String.format(format, *args)
}

actual fun requiresNotificationsPermission(): Boolean {
    TODO("Not yet implemented")
}