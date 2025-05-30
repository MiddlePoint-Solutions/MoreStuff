package io.middlepoint.morestuff.shared.domain.usecase.settings

import arrow.core.Either
import io.middlepoint.morestuff.shared.platform.AppSettingsHandler


interface OpenAppSettingsUseCase {
    operator fun invoke(): Either<String, Unit>
}

class OpenAppSettingsUseCaseImpl(
    private val appSettingsHandler: AppSettingsHandler
) : OpenAppSettingsUseCase {
    
    override fun invoke(): Either<String, Unit> {
        return try {
            appSettingsHandler.openAppSettings()
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left("Error opening app settings: ${e.message}")
        }
    }
}