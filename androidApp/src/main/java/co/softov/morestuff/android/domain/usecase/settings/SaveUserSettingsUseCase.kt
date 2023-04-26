package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository

interface SaveUserSettingsUseCase {
    suspend operator fun invoke(settings: AppSettings): AppSettings
}

class SaveUserSettingsUseCaseImpl(
    private val userRepository: UserRepository
) : SaveUserSettingsUseCase {
    override suspend fun invoke(settings: AppSettings): AppSettings {
        userRepository.setSnoozeLimit(settings.snoozeLimit)
        return settings
    }
}




