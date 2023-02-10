package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository


interface GetUserSettingsUseCase {
    suspend operator fun invoke(): AppSettings
}

class GetUserSettingsUseCaseUseCaseImpl(
    private val userRepository: UserRepository
) : GetUserSettingsUseCase {
    override suspend fun invoke(): AppSettings {
        return userRepository.getUserSettings(AppSettings())
    }

}

