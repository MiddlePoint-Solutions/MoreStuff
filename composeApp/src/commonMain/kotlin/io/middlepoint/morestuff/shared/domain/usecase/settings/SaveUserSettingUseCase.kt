package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.repository.UserRepository

interface SaveUserSettingUseCase {
    suspend operator fun <T> invoke(setting: AppSetting<T>, settingValue: T)
}

class SaveUserSettingUseCaseImpl(
    private val userRepository: UserRepository
) : SaveUserSettingUseCase {
    override suspend fun <T> invoke(setting: AppSetting<T>, settingValue: T) {
        userRepository.saveAppSetting(setting, settingValue)
    }
}




