package co.softov.morestuff.android.domain.util

interface TimeFormatter {

    val is24HourFormat: Boolean

    fun formatTime(timeString: String?, pattern: String): String?
    fun formatTimeOnly(timeString: String?): String?
    fun formatTimeDayAndMonth(timeString: String?): String?
    fun formatTimeDayMonthHour(timeString: String?): String?
    fun formatToDateTime(timeString: String?): String?
    fun formatTimeWithDayMonthYear(timeString: String?): String?

}
