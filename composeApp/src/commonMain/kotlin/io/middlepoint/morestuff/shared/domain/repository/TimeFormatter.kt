package io.middlepoint.morestuff.shared.domain.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface TimeFormatter {
  fun formatTime(timeString: String?, pattern: String): String?
  fun formatTimeDayMonthInDeviceLanguage(timeString: String?): String?
  fun formatDisplayTime(timeString: String): String?
  fun formatDisplayTime(time: LocalTime): String?
  fun formatDisplayDate(time: LocalDate): String
  fun formatToDateTime(timeString: String): String
  fun formatDisplayCompleteTime(timeString: String): String
}

expect fun is24HourFormat(): Boolean