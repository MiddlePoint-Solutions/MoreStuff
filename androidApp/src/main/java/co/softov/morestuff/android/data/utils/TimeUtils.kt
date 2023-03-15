package co.softov.morestuff.android.data.utils

import kotlinx.datetime.*
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object TimeUtils {

    val nowUtcInstant: Instant get() = Clock.System.now()
    val nowUtcInstantString: String = nowUtcInstant.toString()

    val nowUtcMillis: Long get() = Clock.System.now().toEpochMilliseconds()

    val nowLocalDateTime: LocalDateTime
        get() = nowUtcInstant.toLocalDateTime(currentTimeZone)

    val currentTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

    val nowLocalDateTimeString: String get() = nowLocalDateTime.toString()

    fun getCreateTime(): String = nowUtcInstant.toString()

    val localDateTime1HourBack: String
        get() = (nowUtcInstant - 1.hours)
            .toLocalDateTime(currentTimeZone)
            .toString()

    fun utcStringToLocalDateTime(time: String): LocalDateTime {
        return time.toInstant().toLocalDateTime(currentTimeZone)
    }

    fun isToday(localTime: String): Boolean {
        return localTime.toLocalDateTime().date == nowLocalDateTime.date
    }

    fun isTomorrow(localTime: String): Boolean {
        return localTime.toLocalDateTime().date == tomorrowLocalDateTime().date
    }

    fun isLater(localTime: String): Boolean {
        return localTime.toLocalDateTime().date.toEpochDays() > tomorrowLocalDateTime().date.toEpochDays()
    }

    private fun stringToInstant(timeUtc: String): Instant = timeUtc.toInstant()

    fun todayUtcString(hour: Int = 0, minute: Int = 0): String =
        todayLocalDateTime(hour, minute).toInstant(currentTimeZone).toString()

    fun tomorrowUtcString(hour: Int = 0, minute: Int = 0): String =
        tomorrowLocalDateTime(hour, minute).toInstant(currentTimeZone).toString()

    fun tomorrowLocalDateTimeString(hour: Int = 0, minute: Int = 0): String =
        tomorrowLocalDateTime(hour, minute).toString()

    fun tomorrowLocalDateTime(hour: Int = 0, minute: Int = 0): LocalDateTime =
        (nowUtcInstant + 1.days).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    fun todayLocalDateTimeString(hour: Int = 0, minute: Int = 0): String =
        todayLocalDateTime(hour, minute).toString()

    fun todayLocalDateTime(hour: Int = 0, minute: Int = 0): LocalDateTime =
        (nowUtcInstant).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    fun todayLocalDateTimeByAdding(hour: Int = 0, minute: Int = 0): LocalDateTime =
        (nowUtcInstant + hour.hours + minute.minutes).toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).run {
            LocalDateTime(year, month, dayOfMonth, this.hour, this.minute, 0, 0)
        }

    fun weekendLocalDateTime(): LocalDateTime {
        val daysUntilWeekend = when (nowLocalDateTime.dayOfWeek) {
            DayOfWeek.MONDAY -> 5
            DayOfWeek.TUESDAY -> 4
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 2
            DayOfWeek.FRIDAY -> 1
            DayOfWeek.SATURDAY -> 7
            DayOfWeek.SUNDAY -> 6
        }

        return (nowLocalDateTime.toInstant(currentTimeZone) + daysUntilWeekend.days).toLocalDateTime(
            currentTimeZone
        ).run {
            LocalDateTime(year, month, dayOfMonth, 10, 0, 0, 0)
        }
    }


    val todayTimeStringPair: Pair<String, String>
        get() = todayLocalDateTime(0, 0).toString() to todayLocalDateTime(23, 59).toString()

    val tomorrowTimeStringPair: Pair<String, String>
        get() = tomorrowLocalDateTime(0, 0).toString() to tomorrowLocalDateTime(23, 59).toString()

    fun formatTime(timeString: String?, pattern: String): String? {
        return timeString?.let {
            val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
            val localDateTime = ZonedDateTime.parse(it, formatter)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
            val timeFormatter = DateTimeFormatter.ofPattern(pattern)
            localDateTime.format(timeFormatter)
        }
    }
}


