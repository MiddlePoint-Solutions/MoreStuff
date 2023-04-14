package co.softov.morestuff.android.domain.model



object Defaults {
    const val DEFAULT_SNOOZE_LIMIT = 3
    const val DEFAULT_REMINDER_OVERLOAD_LIMIT = 3
    const val DEFAULT_SMART_REMINDER_ENABLED = false
}
data class AppSettings(
    val snoozeLimit: Int = Defaults.DEFAULT_SNOOZE_LIMIT,
    val smartReminderEnabled: Boolean = Defaults.DEFAULT_SMART_REMINDER_ENABLED
)















