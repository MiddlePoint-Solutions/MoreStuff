package co.softov.morestuff.android.domain.usecase.time

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


interface TimeFormatter {
    fun formatTime(timeString: String?, pattern: String): String?
    fun formatTimeOnly(timeString: String?): String?
}


class TimeFormatterImpl : TimeFormatter {

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
        return formatTime(timeString, "HH:mm")
    }
}