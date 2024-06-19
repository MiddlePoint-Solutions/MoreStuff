package io.middlepoint.morestuff.shared

actual class File actual constructor(path: String) {
    actual fun exists(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun delete(): Boolean {
        TODO("Not yet implemented")
    }
}

actual fun generateUUID(): String {
    TODO("Not yet implemented")
}

actual fun formatString(format: String, vararg args: Any): String {
    TODO("Not yet implemented")
}

actual fun requiresNotificationsPermission(): Boolean {
    TODO("Not yet implemented")
}