package io.middlepoint.morestuff.shared.data.repository

import io.middlepoint.morestuff.shared.TimeUtils
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames.Companion.ENGLISH_FULL
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

class TimeFormatterImpl(
  private val timeUtils: TimeUtils
) : TimeFormatter {

  internal val displayTimeFormat = LocalTime.Format {
    amPmHour(Padding.NONE); char(':'); minute(); char(' '); amPmMarker("AM", "PM")
  }

  internal val displayTimeFormat24Hours = LocalTime.Format {
    hour(); char(':'); minute()
  }

  internal val displayDayMonth = LocalDateTime.Format {
    monthName(ENGLISH_FULL); char(' '); dayOfMonth(Padding.NONE)
  }

  override fun formatDisplayDayMonth(timeString: String, timeZone: TimeZone): String =
    Instant.parse(timeString)
      .toLocalDateTime(timeZone)
      .format(displayDayMonth)

  override fun formatDisplayTime(timeString: String, timeZone: TimeZone): String =
    Instant.parse(timeString)
      .toLocalDateTime(timeZone).time
      .let(::formatDisplayTime)

  override fun formatDisplayTime(time: LocalTime, timeZone: TimeZone): String =
    if (timeUtils.is24HourFormat()) {
      displayTimeFormat24Hours.format(time)
    } else {
      displayTimeFormat.format(time)
    }

  override fun formatDisplayDate(time: LocalDate): String =
    time.format(LocalDate.Formats.ISO)

  override fun formatToDateTime(timeString: String, timeZone: TimeZone): String =
    Instant.parse(timeString).toLocalDateTime(timeZone).toString()

  // TODO: the timeString used here is LocalDateTime instead of Instant
  override fun formatDisplayCompleteTime(timeString: String): String =
    LocalDateTime.parse(timeString).toString()

}