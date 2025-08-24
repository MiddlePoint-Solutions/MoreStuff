package io.middlepoint.morestuff.shared

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class TimeUtilsImpl : TimeUtils {

  override fun is24HourFormat(): Boolean {
    // 1. Get the system locale
    val locale = Locale.getDefault()

    // 2. Build a localized SHORT‐style time formatter
    val formatter = DateTimeFormatter
      .ofLocalizedTime(FormatStyle.SHORT)
      .withLocale(locale)

    // 3. Format 13:00
    val formatted = LocalTime.of(13, 0).format(formatter)

    // 4. If it contains “13”, it’s 24-hour; otherwise e.g. “1:00 PM”
    return formatted.contains("13")
  }

}