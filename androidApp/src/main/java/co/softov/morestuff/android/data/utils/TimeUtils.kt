package co.softov.morestuff.android.data.utils

import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
object TimeUtils {

    val currentUtcInstant: Instant get() = Clock.System.now()

    val currentUtcMillis: Long get() = Clock.System.now().toEpochMilliseconds()

    val currentLocalDateTime: LocalDateTime
        get() = currentUtcInstant.toLocalDateTime(currentTimeZone)

    val currentTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

    val currentLocalDateTimeString: String get() = currentLocalDateTime.toString()

    val localDateTime1HourBack: String
        get() = (currentUtcInstant - Duration.hours(1))
            .toLocalDateTime(currentTimeZone)
            .toString()

    fun tomorrowLocalDateTime(hour: Int, minute: Int = 0): LocalDateTime =
        (currentUtcInstant + Duration.days(1)).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    fun todayLocalDateTime(hour: Int, minute: Int = 0): LocalDateTime =
        (currentUtcInstant).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    fun todayLocalDateTimeByAdding(hour: Int = 0, minute: Int = 0): LocalDateTime =
        (currentUtcInstant + Duration.hours(hour) + Duration.minutes(minute)).toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).run {
            LocalDateTime(year, month, dayOfMonth, this.hour, this.minute, 0, 0)
        }

    val todayTimeStringPair: Pair<String, String>
        get() = todayLocalDateTime(0, 0).toString() to todayLocalDateTime(23, 59).toString()

    val tomorrowTimeStringPair: Pair<String, String>
        get() = tomorrowLocalDateTime(0, 0).toString() to tomorrowLocalDateTime(23, 59).toString()

}


