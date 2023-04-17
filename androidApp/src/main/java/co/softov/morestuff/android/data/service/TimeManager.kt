package co.softov.morestuff.android.data.service

import kotlinx.datetime.*
import java.time.DayOfWeek
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

interface TimeManager {
    /**
    Current UTC time as an Instant object.
     */
    val nowUtcInstant: Instant

    /**
    Current UTC time as a string.
     */
    val nowUtcInstantString: String

    /**
    Current UTC time in milliseconds.
     */
    val nowUtcMillis: Long

    /**
    Current local date and time.
     */
    val nowLocalDateTime: LocalDateTime

    /**
    Current time zone.
     */
    val currentTimeZone: TimeZone

    /**
    Current local date and time as a string.
     */
    val nowLocalDateTimeString: String

    /**
    A string representing the local date and time one hour back from the current UTC time.
     */
    val localDateTime1HourBack: String

    /**
    A pair of strings representing the start and end time of today.
     */
    val todayTimeStringPair: Pair<String, String>

    /**
    A pair of strings representing the start and end time of tomorrow.
     */
    val tomorrowTimeStringPair: Pair<String, String>

    /**
    Returns the time the TimeManager instance was created as a string.
     */
    fun getCreateTime(): String

    /**
    Converts a UTC time string to a LocalDateTime object.
     */
    fun utcStringToLocalDateTime(time: String): LocalDateTime

    /**
    Checks if the given local time string is today.
     */
    fun isToday(localTime: String): Boolean

    /**
    Checks if the given local time string is tomorrow.
     */
    fun isTomorrow(localTime: String): Boolean

    /**
    Checks if the given local time string is later than tomorrow.
     */
    fun isLater(localTime: String): Boolean

    /**
    Returns a UTC string representing the start of today with the given hour and minute.
     */
    fun todayUtcString(hour: Int = 0, minute: Int = 0): String

    /**
    Returns a UTC string representing the start of tomorrow with the given hour and minute.
     */
    fun tomorrowUtcString(hour: Int = 0, minute: Int = 0): String

    /**
    Returns a string representing the start of tomorrow in the local time zone with the given hour and minute.
     */
    fun tomorrowLocalDateTimeString(hour: Int = 0, minute: Int = 0): String

    /**
    Returns a LocalDateTime object representing the start of tomorrow in the local time zone with the given hour and minute.
     */
    fun tomorrowLocalDateTime(hour: Int = 0, minute: Int = 0): LocalDateTime

    /**
    Returns a string representing the start of today in the local time zone with the given hour and minute.
     */
    fun todayLocalDateTimeString(hour: Int = 0, minute: Int = 0): String

    /**
    Returns a LocalDateTime object representing the start of today in the local time zone with the given hour and minute.
     */
    fun todayLocalDateTime(hour: Int = 0, minute: Int = 0): LocalDateTime

    /**
    Returns a LocalDateTime object representing the current time with the given hour and minute added.
     */
    fun todayLocalDateTimeByAdding(hour: Int = 0, minute: Int = 0): LocalDateTime

    /**
    Returns a LocalDateTime object representing 10:00 AM on the next weekend day (Saturday or Sunday).
     */
    fun weekendLocalDateTime(): LocalDateTime

    /**
    Returns a pair of strings representing the start and end time of today.
     */
    fun getTodayTimeRange(): Pair<String, String>

}

class TimeManagerImpl : TimeManager {
    override val nowUtcInstant: Instant get() = Clock.System.now()

    override val nowUtcInstantString: String get() = nowUtcInstant.toString()

    override val nowUtcMillis: Long get() = Clock.System.now().toEpochMilliseconds()

    override val nowLocalDateTime: LocalDateTime
        get() = nowUtcInstant.toLocalDateTime(currentTimeZone)

    override val currentTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

    override val nowLocalDateTimeString: String get() = nowLocalDateTime.toString()

    override val todayTimeStringPair: Pair<String, String>
        get() = todayLocalDateTime(0, 0).toString() to todayLocalDateTime(23, 59).toString()

    override val tomorrowTimeStringPair: Pair<String, String>
        get() = tomorrowLocalDateTime(0, 0).toString() to tomorrowLocalDateTime(23, 59).toString()

    override fun getCreateTime(): String = nowUtcInstant.toString()


    override val localDateTime1HourBack: String
        get() = (nowUtcInstant - 1.hours)
            .toLocalDateTime(currentTimeZone)
            .toString()

    override fun utcStringToLocalDateTime(time: String): LocalDateTime {
        return time.toInstant().toLocalDateTime(currentTimeZone)
    }

    override fun isToday(localTime: String): Boolean {
        return localTime.toLocalDateTime().date == nowLocalDateTime.date
    }

    override fun isTomorrow(localTime: String): Boolean {
        return localTime.toLocalDateTime().date == tomorrowLocalDateTime().date
    }

    override fun isLater(localTime: String): Boolean {
        return localTime.toLocalDateTime().date.toEpochDays() > tomorrowLocalDateTime().date.toEpochDays()
    }

    override fun todayUtcString(hour: Int, minute: Int): String =
        todayLocalDateTime(hour, minute).toInstant(currentTimeZone).toString()

    override fun tomorrowUtcString(hour: Int, minute: Int): String =
        tomorrowLocalDateTime(hour, minute).toInstant(currentTimeZone).toString()

    override fun tomorrowLocalDateTimeString(hour: Int, minute: Int): String =
        tomorrowLocalDateTime(hour, minute).toString()


    override fun tomorrowLocalDateTime(hour: Int, minute: Int): LocalDateTime =
        (nowUtcInstant + 1.days).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    override fun todayLocalDateTimeString(hour: Int, minute: Int): String =
        todayLocalDateTime(hour, minute).toString()

    override fun todayLocalDateTime(hour: Int, minute: Int): LocalDateTime =
        (nowUtcInstant).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, hour, minute, 0, 0)
            }

    override fun todayLocalDateTimeByAdding(hour: Int, minute: Int): LocalDateTime =
        (nowUtcInstant + hour.hours + minute.minutes).toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).run {
            LocalDateTime(year, month, dayOfMonth, this.hour, this.minute, 0, 0)
        }

    override fun weekendLocalDateTime(): LocalDateTime {
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

    override fun getTodayTimeRange(): Pair<String, String> = todayTimeStringPair


}