package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.repository.UserRepository

interface GetAppThemeUseCase {
    operator fun invoke(): AppTheme
}

class GetAppThemeUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : GetAppThemeUseCase {
    override fun invoke(): AppTheme = getAppSetting(AppSetting.Theme)

}

