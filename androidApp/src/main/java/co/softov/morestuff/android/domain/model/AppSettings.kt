package co.softov.morestuff.android.domain.model

/*object Defaults {
    const val DEFAULT_SNOOZE_LIMIT = 3
    const val DEFAULT_SMART_REMINDER_ENABLED = false
}

sealed class Setting<T>(open val value: T) {
    data class SnoozeLimit(override val value: Int = DEFAULT_SNOOZE_LIMIT) : Setting<Int>(value)
    data class SmartReminderEnabled(
        override val value: Boolean = DEFAULT_SMART_REMINDER_ENABLED
    ) : Setting<Boolean>(value)
}

data class AppSettings(
    val snoozeLimit: Setting.SnoozeLimit = Setting.SnoozeLimit(),
    val smartReminderEnabled: Setting.SmartReminderEnabled = Setting.SmartReminderEnabled(),
)*/


//NEW TESTING


object Defaults {
    const val DEFAULT_SNOOZE_LIMIT = 3
    const val DEFAULT_SMART_REMINDER_ENABLED = false
}
data class AppSettings(

    val snoozeLimit: Int = Defaults.DEFAULT_SNOOZE_LIMIT,
    val smartReminderEnabled: Boolean = Defaults.DEFAULT_SMART_REMINDER_ENABLED
)















