package co.softov.morestuff.android.data.utils

import kotlinx.datetime.*
import java.time.DayOfWeek
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object TimeUtils {

    val nowUtcInstant: Instant get() = Clock.System.now()

    val nowUtcMillis: Long get() = Clock.System.now().toEpochMilliseconds()

    val nowLocalDateTime: LocalDateTime
        get() = nowUtcInstant.toLocalDateTime(currentTimeZone)

    val currentTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

    val nowLocalDateTimeString: String get() = nowLocalDateTime.toString()

    val localDateTime1HourBack: String
        get() = (nowUtcInstant - 1.hours)
            .toLocalDateTime(currentTimeZone)
            .toString()

    fun tomorrowLocalDateTime(hour: Int, minute: Int = 0): LocalDateTime =
        (nowUtcInstant + 1.days).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    fun todayLocalDateTime(hour: Int, minute: Int = 0): LocalDateTime =
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

}


