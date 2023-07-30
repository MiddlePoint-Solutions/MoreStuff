package co.softov.morestuff.android.data.utils

import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import co.softov.morestuff.android.domain.util.TimeFormatter
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
            val timeFormatter = DateTimeFormatter.ofPattern(pattern)
            localDateTime.format(timeFormatter)
        }
    }

    override fun formatTimeOnly(timeString: String?): String? {
        return formatTime(timeString, "HH:mm${addAmPm()}")
    }

    override fun formatTimeDayAndMonth(timeString: String?): String? {
        return formatTime(timeString, "EEEE, MMMM d")
    }

    override fun formatToDateTime(timeString: String?): String? {
        return formatTime(timeString, "dd/MM/yyyy HH:mm${addAmPm()}")
    }

    private fun addAmPm() = if (is24HourFormat) "a" else ""

}