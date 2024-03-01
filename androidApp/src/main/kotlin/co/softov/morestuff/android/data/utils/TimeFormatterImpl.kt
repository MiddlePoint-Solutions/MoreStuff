package co.softov.morestuff.android.data.utils

import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import androidx.compose.ui.res.stringResource
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.util.TimeFormatter
import kotlinx.datetime.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimeFormatterImpl(
    private val context: Context,
) : TimeFormatter {

    override val is24HourFormat
        get() = is24HourFormat(context)

    override fun formatTime(timeString: String?, pattern: String): String? {
        return timeString?.let {
            val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
            val localDateTime = ZonedDateTime.parse(it, formatter)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
            val timeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            localDateTime.format(timeFormatter)
        }
    }
    override fun formatTimeDayMonthInDeviceLanguage(timeString: String?): String? {
        return formatTime(timeString, "MMMM d")
    }

    override fun formatTimeOnly(timeString: String?): String? {
        return formatTime(timeString, "HH:mm${addAmPm()}")
    }

    override fun formatTimeDayAndMonth(timeString: String?): String? {
        return formatTime(timeString, "EEEE, MMMM d")
    }

    override fun formatTimeDayMonthHour(timeString: String?): String? {
        return formatTime(timeString, "EEEE d MMMM, HH:mm")
    }


    override fun formatToDateTime(timeString: String?): String? {
        return formatTime(timeString, "dd/MM/yyyy HH:mm${addAmPm()}")
    }

    private fun addAmPm() = if (is24HourFormat) "" else "a"

    override fun formatTimeWithDayMonthYear(timeString: String?): String? {
        return timeString?.let {
            val localDateTime = LocalDateTime.parse(it)
            formatWithOrdinal(localDateTime)
        }
    }

    private fun formatWithOrdinal(dateTime: LocalDateTime): String {
        val dayOfMonth = dateTime.dayOfMonth
        val month = dateTime.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
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