package co.softov.morestuff.android.data.repository

import android.content.SharedPreferences
import co.softov.morestuff.android.data.Constants.KEY_USER_SMART_REMINDER_ENABLED
import co.softov.morestuff.android.data.Constants.KEY_USER_SNOOZE_LIMIT
import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Setting
import co.softov.morestuff.android.domain.repository.UserRepository

class UserRepositoryImpl(
    private val prefs: SharedPreferences
) : UserRepository {

    override suspend fun setSnoozeLimit(limit: Int) {
        prefs.edit().putInt(KEY_USER_SNOOZE_LIMIT, limit).commit()
    }

    override suspend fun getUserSettings(default: AppSettings): AppSettings =
        with(prefs) {
            AppSettings(
                snoozeLimit = Setting.SnoozeLimit(
                    getInt(KEY_USER_SNOOZE_LIMIT, default.snoozeLimit.value)
                ),
                smartReminderEnabled = Setting.SmartReminderEnabled(
                    getBoolean(KEY_USER_SMART_REMINDER_ENABLED, default.smartReminderEnabled.value)
                )
            )
        }
}