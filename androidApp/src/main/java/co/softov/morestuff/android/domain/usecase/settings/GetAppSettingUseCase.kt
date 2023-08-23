package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.repository.UserRepository

interface GetAppSettingUseCase {
    operator fun<T> invoke(setting: AppSetting<T>): T
}

class GetAppSettingUseCaseImpl(
    private val userRepository: UserRepository
) : GetAppSettingUseCase {
    override fun<T> invoke(setting: AppSetting<T>): T {
        return userRepository.getAppSetting(setting)
    }

}

