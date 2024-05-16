package io.middlepoint.morestuff.android.domain.usecase.settings

import io.middlepoint.morestuff.android.domain.enums.AppSetting
import io.middlepoint.morestuff.android.domain.enums.AppTheme
import io.middlepoint.morestuff.android.domain.repository.UserRepository

interface GetAppThemeUseCase {
    operator fun invoke(): AppTheme
}

class GetAppThemeUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : GetAppThemeUseCase {
    override fun invoke(): AppTheme = getAppSetting(AppSetting.Theme)

}

