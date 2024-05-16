package io.middlepoint.morestuff.android.domain.usecase.settings

import io.middlepoint.morestuff.android.domain.enums.AppSetting
import io.middlepoint.morestuff.android.domain.repository.UserRepository

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




