package io.middlepoint.morestuff.shared.data.repository

import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

class TimeFormatterImpl : TimeFormatter {
  val loggerTime = Logger.withTag("TimeFormatterImpl")
  override val is24HourFormat: Boolean
    get() = true

  private val displayTimeFormat = LocalTime.Format { hour(); char(':'); minute(); }

  override fun formatTime(timeString: String?, pattern: String): String? {
    return timeString?.let {
      try {
        val localDateTime = try {
          loggerTime.i("Original time: $it")
          Instant.parse(it).toLocalDateTime(TimeZone.currentSystemDefault())
        } catch (e: Exception) {
          LocalDateTime.parse(it)
        }
        customFormatLocalDateTime(localDateTime, pattern)
      } catch (e: Exception) {
        null
      }
    }
  }

  override fun formatTimeDayMonthInDeviceLanguage(timeString: String?): String? {
    return timeString?.let {
      try {
        val instant = Instant.parse(it)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.month.name.lowercase()} ${localDateTime.dayOfMonth}"
      } catch (e: Exception) {
        null
      }
    }
  }

  override fun formatDisplayTime(timeString: String): String? {
    return formatTime(timeString, "HH:mm${addAmPm()}")
  }

  override fun formatDisplayTime(time: LocalTime): String? {
    return "${displayTimeFormat.format(time)}${addAmPm()}"
  }

  override fun formatDisplayDate(timeString: String?): String? {
//        return formatTime(timeString, "EEEE, MM, d")
    return formatTime(timeString, "dd/MM/yyyy")
  }

  override fun formatDisplayDate(time: LocalDate): String {
    return time.format(LocalDate.Formats.ISO)
  }

  override fun formatTimeDayMonthHour(timeString: String?): String? {
    return formatTime(timeString, "EEEE d MMMM, HH:mm")
  }

  override fun formatToDateTime(timeString: String?): String? {
    return formatTime(timeString, "dd/MM/yyyy HH:mm${addAmPm()}")
  }

  private fun addAmPm(): String {
    return if (is24HourFormat) "" else " a"
  }

  override fun formatTimeWithDayMonthYear(timeString: String?): String? {
    return timeString?.let {
      try {
        val instant = Instant.parse(it)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        customFormatWithOrdinal(localDateTime)
      } catch (e: Exception) {
        null
      }
    }
  }


  private fun customFormatLocalDateTime(localDateTime: LocalDateTime, pattern: String): String {
    val components = mapOf(
      "yyyy" to localDateTime.year.toString(),
      "MM" to localDateTime.month.name.lowercase().replaceFirstChar { it.titlecase() },
      "dd" to localDateTime.dayOfMonth.toString().padStart(2, '0'),
      "d" to localDateTime.dayOfMonth.toString(),
      "HH" to localDateTime.hour.toString().padStart(2, '0'),
      "mm" to localDateTime.minute.toString().padStart(2, '0'),
      "EEEE" to localDateTime.dayOfWeek.name.lowercase().replaceFirstChar { it.titlecase() }
    )

    var formattedString = pattern
    components.forEach { (key, value) ->
      formattedString = formattedString.replace(key, value)
    }
    return formattedString
  }

  private fun customFormatWithOrdinal(dateTime: LocalDateTime): String {
    val dayOfMonth = dateTime.dayOfMonth
    val month = dateTime.month.name.lowercase()
      .replaceFirstChar { it.titlecase() }
    val year = dateTime.year

    return "$dayOfMonth${getDayOfMonthSuffix(dayOfMonth)} $month, $year"
  }

  private fun getDayOfMonthSuffix(n: Int): String {
    if (n in 11..13) {
      return "th"
    }
    return when (n % 10) {
      1 -> "st"
      2 -> "nd"
      3 -> "rd"
      else -> "th"
    }
  }
}
