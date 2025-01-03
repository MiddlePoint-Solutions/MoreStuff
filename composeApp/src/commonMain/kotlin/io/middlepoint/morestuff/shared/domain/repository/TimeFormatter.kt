package io.middlepoint.morestuff.shared.domain.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

interface TimeFormatter {

  fun formatDisplayDayMonth(
    timeString: String,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
  ): String

  fun formatDisplayTime(
    timeString: String,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
  ): String

  fun formatDisplayTime(
    time: LocalTime,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
  ): String

  fun formatDisplayDate(time: LocalDate): String
  fun formatToDateTime(
    timeString: String,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
  ): String

  fun formatDisplayCompleteTime(timeString: String): String
}
