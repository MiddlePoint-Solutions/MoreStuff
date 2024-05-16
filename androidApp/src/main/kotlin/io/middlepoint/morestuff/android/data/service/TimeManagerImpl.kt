package io.middlepoint.morestuff.android.data.service

import io.middlepoint.morestuff.android.domain.enums.RelativeDateDisplay
import io.middlepoint.morestuff.android.domain.service.TimeManager
import kotlinx.datetime.*
import java.time.DayOfWeek
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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

    override fun localDateTimeToUtc(localDateTime: LocalDateTime): Instant {
        return localDateTime.toInstant(currentTimeZone)
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

    override fun tomorrowLocalDateTimeByAdding(hour: Int, minute: Int): LocalDateTime =
        (nowUtcInstant + 1.days).toLocalDateTime(TimeZone.currentSystemDefault())
            .run {
                LocalDateTime(year, month, dayOfMonth, this.hour + hour, this.minute + minute, 0, 0)
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

    override fun todayLocalDateTimeByAdding(hour: Int, minute: Int): LocalDateTime {
        return (nowUtcInstant + hour.hours + minute.minutes).toLocalDateTime(currentTimeZone)
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
        val nextHour: Int
        val nextQuarterHour: Int
        when {
            minutes < 10 -> {
                nextHour = now.hour
                nextQuarterHour = 15
            }

            minutes < 20 -> {
                nextHour = now.hour
                nextQuarterHour = 30
            }

            minutes < 35 -> {
                nextHour = now.hour
                nextQuarterHour = 45
            }

            else -> {
                nextHour = if (now.hour < 23) now.hour + 1 else 0
                nextQuarterHour = 0
            }
        }
        return LocalDateTime(now.year, now.month, now.dayOfMonth, nextHour, nextQuarterHour, 0, 0)
    }

    override fun getRelativeDate(timeString: String): RelativeDateDisplay = when {
        isToday(timeString) -> RelativeDateDisplay.Today
        isTomorrow(timeString) -> RelativeDateDisplay.Tomorrow
        else -> RelativeDateDisplay.Date
    }

    override fun utcMillisToLocalDateTime(utcMillis: Long, hour: Int, minute: Int) =
        localDateTime(
            Instant.fromEpochMilliseconds(utcMillis).toLocalDateTime(currentTimeZone),
            hour,
            minute
        )
}