package co.softov.morestuff.android.data.repository

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































