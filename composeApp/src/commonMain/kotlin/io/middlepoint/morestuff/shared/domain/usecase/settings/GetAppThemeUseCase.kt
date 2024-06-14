package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.repository.UserRepository

interface GetAppThemeUseCase {
    operator fun invoke(): AppTheme
}

class GetAppThemeUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : GetAppThemeUseCase {
    override fun invoke(): AppTheme = getAppSetting(AppSetting.Theme)

}

