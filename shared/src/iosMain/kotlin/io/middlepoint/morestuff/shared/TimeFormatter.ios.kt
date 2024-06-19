package io.middlepoint.morestuff.shared

// TODO: use kotlinx datetime for formatting?
class TimeFormatterImpl : TimeFormatter {
    override val is24HourFormat: Boolean
        get() = true // TODO:

    override fun formatTime(timeString: String?, pattern: String): String? {
        return "" // TODO:
    }

    override fun formatTimeDayMonthInDeviceLanguage(timeString: String?): String? {
        return "" // TODO:
    }

    override fun formatTimeOnly(timeString: String?): String? {
        return "" // TODO:
    }

    override fun formatTimeDayAndMonth(timeString: String?): String? {
        return "" // TODO:
    }

    override fun formatTimeDayMonthHour(timeString: String?): String? {
        return "" // TODO:
    }

    override fun formatToDateTime(timeString: String?): String? {
        return "TODO"
    }

    override fun formatTimeWithDayMonthYear(timeString: String?): String? {
        return "" // TODO:
    }
}