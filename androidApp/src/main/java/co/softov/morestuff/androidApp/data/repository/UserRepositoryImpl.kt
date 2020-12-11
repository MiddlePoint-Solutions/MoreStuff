package co.softov.morestuff.androidApp.data.repository

import android.content.SharedPreferences
import co.softov.morestuff.androidApp.data.Constants.KEY_USER_SNOOZE_LIMIT
import co.softov.morestuff.androidApp.domain.Defaults.DEFAULT_SNOOZE_LIMIT
import co.softov.morestuff.androidApp.domain.model.UserSettings
import co.softov.morestuff.androidApp.domain.repository.UserRepository

class UserRepositoryImpl(
    private val prefs: SharedPreferences
) : UserRepository {

    override suspend fun setSnoozeLimit(limit: Int) {
        prefs.edit().putInt(KEY_USER_SNOOZE_LIMIT, limit).commit()
    }

    override suspend fun getUserSettings(): UserSettings {
        return prefs.run {
            UserSettings(
                snoozeLimit = getInt(KEY_USER_SNOOZE_LIMIT, DEFAULT_SNOOZE_LIMIT)
            )
        }
    }
}