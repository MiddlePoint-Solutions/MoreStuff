package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.model.AppSettings
import io.middlepoint.morestuff.shared.domain.repository.UserRepository

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

