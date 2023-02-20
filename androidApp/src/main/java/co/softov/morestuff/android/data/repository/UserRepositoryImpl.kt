package co.softov.morestuff.android.data.repository


/*class UserRepositoryImpl(
    private val prefs: SharedPreferences
) : UserRepository {

    override suspend fun setSnoozeLimit(limit: Int) {
        prefs.edit().putInt(SNOOZE_LIMIT, limit).commit()
    }

    override suspend fun getUserSettings(default: AppSettings): AppSettings =
        with(prefs) {
            AppSettings(
                snoozeLimit = Setting.SnoozeLimit(
                    getInt(SNOOZE_LIMIT, default.snoozeLimit.value)
                ),
                smartReminderEnabled = Setting.SmartReminderEnabled(
                    getBoolean(SMART_REMINDER_ENABLED, default.smartReminderEnabled.value)
                )
            )
        }
}*/



//NEW TESTING


import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import com.russhwolf.settings.Settings


class UserRepositoryImpl(
    private val settings: Settings
) : UserRepository {

    override suspend fun setSnoozeLimit(limit: Int) {
        settings.putInt("snoozeLimit", limit)
    }

    override suspend fun getUserSettings(default: AppSettings): AppSettings {
        return AppSettings().copy(
            snoozeLimit = settings.getInt("snoozeLimit", default.snoozeLimit),
            smartReminderEnabled = settings.getBoolean("smartReminderEnabled", default.smartReminderEnabled)
            )
    }
}































