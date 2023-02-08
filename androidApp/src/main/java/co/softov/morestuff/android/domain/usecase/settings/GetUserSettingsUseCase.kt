package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository


interface GetUserSettings {
    suspend operator fun invoke(): AppSettings
}

class GetUserSettingsUseCaseImpl(
    private val userRepository: UserRepository
) : GetUserSettings {
    override suspend fun invoke(): AppSettings {
        return userRepository.getUserSettings(AppSettings())
    }

}

