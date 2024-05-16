package io.middlepoint.morestuff.android.domain.usecase.settings

import io.middlepoint.morestuff.android.domain.enums.AppSetting
import io.middlepoint.morestuff.android.domain.enums.AppTheme
import io.middlepoint.morestuff.android.domain.repository.UserRepository

interface CheckFirstTimeUseCase {
    operator fun invoke(): Boolean
}

class CheckFirstTimeUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : CheckFirstTimeUseCase {
    override fun invoke(): Boolean = getAppSetting(AppSetting.FirstTime)

}

