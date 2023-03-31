package co.softov.morestuff.android.data.service

import kotlinx.datetime.*
import java.time.DayOfWeek
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

interface TimeManager {
    val nowUtcInstant: Instant
    val nowUtcInstantString: String
    val nowUtcMillis: Long
    val nowLocalDateTime: LocalDateTime
    val currentTimeZone: TimeZone
    val nowLocalDateTimeString: String
    val localDateTime1HourBack: String
    val todayTimeStringPair: Pair<String, String>
    val tomorrowTimeStringPair: Pair<String, String>
    fun getCreateTime(): String
    fun utcStringToLocalDateTime(time: String): LocalDateTime
    fun isToday(localTime: String): Boolean
    fun isTomorrow(localTime: String): Boolean
    fun isLater(localTime: String): Boolean
    fun todayUtcString(hour: Int = 0, minute: Int = 0): String
    fun tomorrowUtcString(hour: Int = 0, minute: Int = 0): String
    fun tomorrowLocalDateTimeString(hour: Int = 0, minute: Int = 0): String
    fun tomorrowLocalDateTime(hour: Int = 0, minute: Int = 0): LocalDateTime
    fun todayLocalDateTimeString(hour: Int = 0, minute: Int = 0): String
    fun todayLocalDateTime(hour: Int = 0, minute: Int = 0): LocalDateTime
    fun todayLocalDateTimeByAdding(hour: Int = 0, minute: Int = 0): LocalDateTime
    fun weekendLocalDateTime(): LocalDateTime
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