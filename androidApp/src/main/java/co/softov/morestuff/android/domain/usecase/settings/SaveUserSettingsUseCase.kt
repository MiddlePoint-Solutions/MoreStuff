package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings


interface SaveUserSettings {
    suspend fun invoke(settings: AppSettings)
}

class SaveUserSettingsImpl(
    private var appSettings: AppSettings
): SaveUserSettings {
    override suspend fun invoke(settings: AppSettings) {
       appSettings = appSettings.copy(snoozeLimit = settings.snoozeLimit)

    }
}

