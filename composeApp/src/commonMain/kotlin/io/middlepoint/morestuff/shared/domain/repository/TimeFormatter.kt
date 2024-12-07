package io.middlepoint.morestuff.shared.domain.repository

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface TimeFormatter {

  val is24HourFormat: Boolean

  fun formatTime(timeString: String?, pattern: String): String?
  fun formatTimeDayMonthInDeviceLanguage(timeString: String?): String?
  fun formatDisplayTime(timeString: String): String?
  fun formatDisplayTime(time: LocalTime): String?
  fun formatDisplayDate(timeString: String?): String?
  fun formatDisplayDate(time: LocalDate): String
  fun formatTimeDayMonthHour(timeString: String?): String?
  fun formatToDateTime(timeString: String?): String?
  fun formatTimeWithDayMonthYear(timeString: String?): String?

}
