package io.middlepoint.morestuff.shared

interface TimeFormatter {

    val is24HourFormat: Boolean

    fun formatTime(timeString: String?, pattern: String): String?
    fun formatTimeDayMonthInDeviceLanguage(timeString: String?): String?
    fun formatTimeOnly(timeString: String?): String?
    fun formatTimeDayAndMonth(timeString: String?): String?
    fun formatTimeDayMonthHour(timeString: String?): String?
    fun formatToDateTime(timeString: String?): String?
    fun formatTimeWithDayMonthYear(timeString: String?): String?

}

expect class TimeFormatterImpl: TimeFormatter
