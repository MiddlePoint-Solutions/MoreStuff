package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.model.Defaults.DEFAULT_SMART_REMINDER_ENABLED
import co.softov.morestuff.android.domain.model.Defaults.DEFAULT_SNOOZE_LIMIT
import co.softov.morestuff.android.domain.model.Setting.SmartReminderEnabled
import co.softov.morestuff.android.domain.model.Setting.SnoozeLimit

object Defaults {
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
    var snoozeLimit: SnoozeLimit = SnoozeLimit(),
    val smartReminderEnabled: SmartReminderEnabled = SmartReminderEnabled(),
)

