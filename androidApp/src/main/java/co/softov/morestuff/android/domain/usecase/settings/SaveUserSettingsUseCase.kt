package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings


interface SaveUserSettings {
    suspend operator fun invoke(settings: AppSettings)
}


class SaveUserSettingsImpl(
    private val appSettings: AppSettings
): SaveUserSettings {
    override suspend fun invoke(settings: AppSettings) {
        appSettings.snoozeLimit = settings.snoozeLimit
    }
}




