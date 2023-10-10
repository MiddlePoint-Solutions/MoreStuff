package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import co.softov.morestuff.android.domain.usecase.schedule.UpdateReviewNotificationScheduleUseCase

interface GetAppSettingsUseCase {
    suspend operator fun invoke(): AppSettings
}

class GetAppSettingsUseCaseImpl(
    private val userRepository: UserRepository,
    private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase
) : GetAppSettingsUseCase {
    override suspend fun invoke(): AppSettings {
        val appSettings = userRepository.getAppSettings(AppSettings())
        updateReviewNotificationScheduleUseCase.invoke(appSettings.reviewTime.first, appSettings.reviewTime.second, replaceExisting = false)
        return appSettings
    }

}

