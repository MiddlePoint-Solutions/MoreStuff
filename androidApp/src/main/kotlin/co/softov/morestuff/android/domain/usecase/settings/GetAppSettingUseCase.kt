package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.repository.UserRepository

interface GetAppSettingUseCase {
    operator fun<T, R> invoke(setting: AppSetting<T>): R
}

class GetAppSettingUseCaseImpl(
    private val userRepository: UserRepository
) : GetAppSettingUseCase {
    override fun<T, R> invoke(setting: AppSetting<T>): R {
        return userRepository.getAppSetting(setting)
    }

}

