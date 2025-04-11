package io.middlepoint.morestuff.shared.domain.usecase.settings

import io.middlepoint.morestuff.shared.domain.enums.AppSetting

interface GetApiKeyUseCase {
    operator fun invoke(): String
}

class GetApiKeyUseCaseImpl(
    private val getAppSetting: GetAppSettingUseCase
) : GetApiKeyUseCase {
    override fun invoke(): String = getAppSetting(AppSetting.ApiKey)
}