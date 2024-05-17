package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.repository.UserRepository

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

