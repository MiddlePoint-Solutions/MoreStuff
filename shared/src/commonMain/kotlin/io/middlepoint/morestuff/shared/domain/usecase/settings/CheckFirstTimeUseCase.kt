package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.repository.UserRepository

interface CheckFirstTimeUseCase {
    operator fun invoke(): Boolean
}

class CheckFirstTimeUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : CheckFirstTimeUseCase {
    override fun invoke(): Boolean = getAppSetting(AppSetting.FirstTime)

}

