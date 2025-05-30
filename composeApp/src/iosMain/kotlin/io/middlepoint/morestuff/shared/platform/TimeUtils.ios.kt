package io.middlepoint.morestuff.shared.platform

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle

class TimeUtilsImpl : TimeUtils {
  override fun is24HourFormat(): Boolean {
    val locale = NSLocale.currentLocale
    val formatter = NSDateFormatter().apply {
      this.locale = locale
      this.dateStyle = NSDateFormatterNoStyle
      this.timeStyle = NSDateFormatterShortStyle
    }
    val dateFormat = formatter.dateFormat ?: ""

    return !dateFormat.contains("a")
  }
}