package io.middlepoint.morestuff.shared.data.repository

import androidx.compose.ui.text.intl.Locale
import io.middlepoint.morestuff.shared.TimeUtils
import io.middlepoint.morestuff.shared.domain.enums.MonthNamesLocalized
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime


class TimeFormatterImpl(
  private val timeUtils: TimeUtils
) : TimeFormatter {

  private val displayTime: DateTimeFormatBuilder.WithTime.() -> Unit = {
    if (timeUtils.is24HourFormat()) {
      hour(); char(':'); minute()
    } else {
      amPmHour(Padding.NONE); char(':'); minute(); char(' '); amPmMarker("AM", "PM")

    }
  }

  private val displayTimeFormat = LocalTime.Format { displayTime() }
  private val getUserLocale = Locale.current.language
  private val monthNames: MonthNames = MonthNamesLocalized.fromLanguageCode(getUserLocale)


  private val displayDayMonth = LocalDateTime.Format {
    monthName(monthNames)
    char(' ')
    dayOfMonth(Padding.NONE)
  }



  private val displayCompleteTimeFormat = LocalDateTime.Format {
    dayOfMonth(Padding.NONE);
    char(' ');
    monthName(monthNames);
    char(' ');
    year(padding = Padding.NONE)
    char(' ')
    displayTime()
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
    displayTimeFormat.format(time)

  override fun formatDisplayDate(time: LocalDate): String =
    time.format(LocalDate.Formats.ISO)

  override fun formatToDateTime(timeString: String, timeZone: TimeZone): String =
    Instant.parse(timeString).toLocalDateTime(timeZone).toString()

  override fun formatDisplayCompleteTime(timeString: String, timeZone: TimeZone): String =
    timeString.let { time ->
      if (time.endsWith('z', ignoreCase = true)) {
        Instant.parse(time).toLocalDateTime(timeZone)
      } else {
        LocalDateTime.parse(time)
      }
    }.format(displayCompleteTimeFormat)

}


