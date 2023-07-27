package co.softov.morestuff.android.domain.util

interface TimeFormatter {
    fun formatTime(timeString: String?, pattern: String): String?
    fun formatTimeOnly(timeString: String?): String?
    fun formatTimeDayAndMonth(timeString: String?): String?
    fun formatToDateTime(timeString: String?): String?

}
