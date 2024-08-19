package io.middlepoint.morestuff.shared

import platform.Foundation.NSString
import platform.Foundation.NSUUID
import platform.Foundation.stringWithFormat

actual fun generateUUID(): String = NSUUID().UUIDString

actual fun formatString(format: String, vararg args: Any): String = stringWithFormat(format, args)

private fun stringWithFormat(format: String, args: Array<out Any>): String {
    // NSString format works with NSObjects via %@, we should change standard format to %@
    val objcFormat = format.replace(Regex("%[\\.|\\d]*[a|b|c|d|e|f|s]"), "%@")
    // bad but objc interop limited :(
    // When calling variadic C functions spread operator is supported only for *arrayOf(...)
    return when (args.size) {
        0 -> NSString.stringWithFormat(objcFormat)
        1 -> NSString.stringWithFormat(objcFormat, args[0])
        2 -> NSString.stringWithFormat(objcFormat, args[0], args[1])
        3 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2])
        4 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2], args[3])
        5 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2], args[3], args[4])
        6 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2], args[3], args[4], args[5])
        7 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
        8 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7])
        9 -> NSString.stringWithFormat(objcFormat, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8])
        else -> throw IllegalArgumentException("can't handle more then 9 arguments now")
    }
}

actual fun requiresNotificationsPermission(): Boolean = true

actual val platform: Platform
  get() = Platform.iOS
