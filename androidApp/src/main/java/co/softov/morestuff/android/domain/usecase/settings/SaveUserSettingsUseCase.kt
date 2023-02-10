package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.model.Setting
import co.softov.morestuff.android.domain.repository.UserRepository



interface SaveUserSettings {
    suspend operator fun invoke(settings: AppSettings) : AppSettings
}


class SaveUserSettingsImpl(
    private val userRepository: UserRepository
) : SaveUserSettings {
    override suspend fun invoke(settings: AppSettings): AppSettings {
        userRepository.setSnoozeLimit(settings.snoozeLimit.value)
        return settings
    }

}





