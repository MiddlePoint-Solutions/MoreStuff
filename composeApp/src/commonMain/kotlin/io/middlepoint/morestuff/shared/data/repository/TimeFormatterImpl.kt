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

  private val loggerTime = Logger.withTag("TimeFormatterImpl")
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

  override fun formatDisplayDate(time: LocalDate): String {
    return time.format(LocalDate.Formats.ISO)
  }

  override fun formatToDateTime(timeString: String): String? {
//    return formatTime(timeString, "dd/MM/yyyy HH:mm${addAmPm()}")
    return Instant.parse(timeString).toLocalDateTime(TimeZone.currentSystemDefault()).toString()
  }

  override fun formatDisplayCompleteTime(timeString: String): String {
    return LocalDateTime.parse(timeString).toString()
  }

  private fun addAmPm(): String {
    return if (is24HourFormat) "" else " a"
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

}