package io.middlepoint.morestuff.android.domain.usecase.settings

import io.middlepoint.morestuff.android.domain.redux.state.AppSettings
import io.middlepoint.morestuff.android.domain.repository.UserRepository

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

