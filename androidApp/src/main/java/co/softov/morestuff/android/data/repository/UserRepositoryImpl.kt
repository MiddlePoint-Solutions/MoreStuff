package co.softov.morestuff.android.data.repository

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Constants
import co.softov.morestuff.android.domain.repository.UserRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set


class UserRepositoryImpl(
    private val settings: Settings
) : UserRepository {

    override suspend fun isFirstTime(): Boolean =
        settings.getBoolean(Constants.KEY_FIRST_TIME, true)
            .also {
                settings[Constants.KEY_FIRST_TIME] = false
            }

    override suspend fun setSnoozeLimit(limit: Int) {
        settings.putInt("snoozeLimit", limit)
    }

    override suspend fun getUserSettings(default: AppSettings): AppSettings {
        return AppSettings().copy(
            snoozeLimit = settings.getInt("snoozeLimit", default.snoozeLimit),
            smartReminderEnabled = settings.getBoolean(
                "smartReminderEnabled",
                default.smartReminderEnabled
            )
        )
    }

}































