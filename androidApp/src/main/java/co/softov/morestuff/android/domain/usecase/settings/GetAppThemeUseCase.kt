package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository

interface GetAppThemeUseCase {
    operator fun invoke(): AppTheme
}

class GetAppThemeUseCaseImpl(
    private val userRepository: UserRepository
) : GetAppThemeUseCase {
    override fun invoke(): AppTheme = userRepository.getAppTheme()

}

