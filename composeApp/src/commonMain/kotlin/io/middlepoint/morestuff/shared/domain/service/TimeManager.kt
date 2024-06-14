package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.enums.RelativeDateDisplay
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

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
     *
     */


    /**
    Returns the time the TimeManager instance was created as a string.
     */
    fun getCreateTime(): String

    /**
    Converts a LocalDateTime to UTC Instant.
     */
    fun localDateTimeToUtc(localDateTime: LocalDateTime): Instant

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
    Returns a LocalDateTime object representing the start of tomorrow in the local time zone with the given hour and minute.
     */
    fun tomorrowLocalDateTimeByAdding(hour: Int = 0, minute: Int = 0): LocalDateTime

    /**
    Returns a string representing the start of today in the local time zone with the given hour and minute.
     */
    fun todayLocalDateTimeString(hour: Int = 0, minute: Int = 0): String

    /**
    Returns a LocalDateTime object representing the start of today in the local time zone with the given hour and minute.
     */
    fun localDateTime(localDateTime: LocalDateTime, hour: Int = 0, minute: Int = 0): LocalDateTime

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

    fun getDefaultPlanTime(): LocalDateTime

    fun getRelativeDate(timeString: String): RelativeDateDisplay

    fun utcMillisToLocalDateTime(utcMillis: Long, hour: Int = 0, minute: Int = 0): LocalDateTime
}