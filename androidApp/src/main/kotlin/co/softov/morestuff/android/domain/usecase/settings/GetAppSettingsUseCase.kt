package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository

interface GetAppSettingsUseCase {
    suspend operator fun invoke(): AppSettings
}

class GetAppSettingsUseCaseImpl(
    private val userRepository: UserRepository,
) : GetAppSettingsUseCase {
    override suspend fun invoke(): AppSettings {
        return userRepository.getAppSettings(AppSettings())
    }

}

