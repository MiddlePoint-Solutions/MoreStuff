package co.softov.morestuff.android.data.service

import co.softov.morestuff.android.domain.enums.RelativeDateDisplay
import co.softov.morestuff.android.domain.service.TimeManager
import kotlinx.datetime.*
import java.time.DayOfWeek
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class TimeManagerImpl : TimeManager {
    override val nowUtcInstant: Instant get() = Clock.System.now()

    override val nowUtcInstantString: String get() = nowUtcInstant.toString()

    override val nowUtcMillis: Long get() = Clock.System.now().toEpochMilliseconds()

    override val nowLocalDateTime: LocalDateTime
        get() = nowUtcInstant.toLocalDateTime(currentTimeZone)

    override val currentTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

    override val nowLocalDateTimeString: String get() = nowLocalDateTime.toString()

    override val todayTimeStringPair: Pair<String, String>
        get() = localDateTime(nowLocalDateTime, 0, 0).toString() to
                localDateTime(nowLocalDateTime, 23, 59).toString()

    override val tomorrowTimeStringPair: Pair<String, String>
        get() = tomorrowLocalDateTime(0, 0).toString() to
                tomorrowLocalDateTime(23, 59).toString()

    override fun getCreateTime(): String = nowUtcInstant.toString()


    override val localDateTime1HourBack: String
        get() = (nowUtcInstant - 1.hours)
            .toLocalDateTime(currentTimeZone)
            .toString()

    override fun utcStringToLocalDateTime(time: String): LocalDateTime {
        return time.toInstant().toLocalDateTime(currentTimeZone)
    }

    override fun localDateTimeStringToUtc(time: String): Instant {
        return time.toLocalDateTime().toInstant(currentTimeZone)
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
        localDateTime(nowLocalDateTime, hour, minute).toInstant(currentTimeZone).toString()

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
        localDateTime(nowLocalDateTime, hour, minute).toString()

    override fun localDateTime(
        localDateTime: LocalDateTime,
        hour: Int,
        minute: Int
    ): LocalDateTime =
        localDateTime.run {
            LocalDateTime(year, month, dayOfMonth, hour, minute, second, 0)
        }

    override fun todayLocalDateTimeByAdding(hour: Int, minute: Int): LocalDateTime =
        nowLocalDateTime.run {
            LocalDateTime(year, month, dayOfMonth, this.hour + hour, this.minute + minute, 0, 0)
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

    override fun getDefaultPlanTime(): LocalDateTime {
        val now = nowLocalDateTime
        val minutes = now.minute
        val nextQuarterHour: Int = when {
            minutes < 10 -> 15
            minutes < 25 -> 30
            minutes < 40 -> 45
            else -> 0
        }
        return if (nextQuarterHour == 0) {
            LocalDateTime(now.year, now.month, now.dayOfMonth, now.hour + 1, 0, 0, 0)
        } else {
            LocalDateTime(now.year, now.month, now.dayOfMonth, now.hour, nextQuarterHour, 0, 0)
        }
    }

    override fun getRelativeDate(timeString: String): RelativeDateDisplay = when {
        isToday(timeString) -> RelativeDateDisplay.Today
        isTomorrow(timeString) -> RelativeDateDisplay.Tomorrow
        else -> RelativeDateDisplay.Date
    }

    override fun epochMillisToLocalDateTime(epochMillis: Long, hour: Int, minute: Int) =
        Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(currentTimeZone).run {
            localDateTime(this, hour, minute)
        }
}