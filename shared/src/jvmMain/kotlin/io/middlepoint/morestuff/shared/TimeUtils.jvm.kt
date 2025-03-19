package io.middlepoint.morestuff.shared

import java.text.DateFormat

class TimeUtilsImpl : TimeUtils {
  override fun is24HourFormat(): Boolean {
    // Get the time format pattern from the current locale's short time format
    val shortTimePattern = (DateFormat.getTimeInstance(DateFormat.SHORT) as java.text.SimpleDateFormat).toPattern()

    // If the pattern contains 'a' or 'h', it's using 12-hour format
    // If it contains 'H' or 'k', it's using 24-hour format
    return !shortTimePattern.contains('a') && !shortTimePattern.contains('h')
  }
}