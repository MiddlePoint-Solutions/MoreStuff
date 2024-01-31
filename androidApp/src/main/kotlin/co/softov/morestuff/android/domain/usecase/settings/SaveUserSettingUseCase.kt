package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.repository.UserRepository

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




