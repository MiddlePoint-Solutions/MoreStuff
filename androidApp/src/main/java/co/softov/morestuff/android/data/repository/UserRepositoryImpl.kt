package co.softov.morestuff.android.data.repository

import android.content.SharedPreferences
import co.softov.morestuff.android.data.Constants.KEY_USER_SMART_REMINDER_ENABLED
import co.softov.morestuff.android.data.Constants.KEY_USER_SNOOZE_LIMIT
import co.softov.morestuff.android.domain.Defaults.DEFAULT_SMART_REMINDER_ENABLED
import co.softov.morestuff.android.domain.Defaults.DEFAULT_SNOOZE_LIMIT
import co.softov.morestuff.android.domain.model.UserSettings
import co.softov.morestuff.android.domain.repository.UserRepository

class UserRepositoryImpl(
    private val prefs: SharedPreferences
) : UserRepository {

    override suspend fun setSnoozeLimit(limit: Int) {
        prefs.edit().putInt(KEY_USER_SNOOZE_LIMIT, limit).commit()
    }

    override suspend fun getUserSettings(): UserSettings = with(prefs) {
        UserSettings(
            snoozeLimit = getInt(KEY_USER_SNOOZE_LIMIT, DEFAULT_SNOOZE_LIMIT),
            smartReminderEnabled = getBoolean(
                KEY_USER_SMART_REMINDER_ENABLED,
                DEFAULT_SMART_REMINDER_ENABLED
            )
        )
    }
}