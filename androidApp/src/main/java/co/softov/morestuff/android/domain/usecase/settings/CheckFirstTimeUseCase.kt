package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.repository.UserRepository

interface CheckFirstTimeUseCase {
    operator fun invoke(): Boolean
}

class CheckFirstTimeUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : CheckFirstTimeUseCase {
    override fun invoke(): Boolean = getAppSetting(AppSetting.FirstTime)

}

