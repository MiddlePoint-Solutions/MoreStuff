package co.softov.morestuff.android.domain.usecase.settings

import co.softov.morestuff.android.domain.model.AppSettings

interface LoadUserSettings {
    suspend fun load(): AppSettings
}

class LoadUserSettingsUseCaseImpl(
    private val saveUserSettings: SaveUserSettings
): LoadUserSettings {
    override suspend fun load(): AppSettings {
        val currentSettings = AppSettings()
        saveUserSettings(currentSettings)
        return currentSettings
    }
}