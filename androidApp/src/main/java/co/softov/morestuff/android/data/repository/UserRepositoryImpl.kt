package co.softov.morestuff.android.data.repository

import co.softov.morestuff.android.data.key
import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppSetting.*
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import com.russhwolf.settings.Settings


class UserRepositoryImpl(
    private val settings: Settings
) : UserRepository {

    override suspend fun getAppSettings(default: AppSettings) = with(default) {
        AppSettings(
            isFirstTime = getSetting(FirstTime, isFirstTime),
            appTheme = AppTheme.valueOf(getSetting(Theme, appTheme.name)),
            snoozeLimit = getSetting(SnoozeLimit, snoozeLimit),
            enableConfetti = getSetting(Confetti, enableConfetti)
        )
    }


    override suspend fun <T> saveAppSetting(setting: AppSetting<T>, settingValue: T) {
        when (setting) {
            FirstTime -> settings.putBoolean(setting.key, settingValue as Boolean)
            Theme -> settings.putString(setting.key, settingValue as String)
            SnoozeLimit -> settings.putInt(setting.key, settingValue as Int)
            Confetti -> settings.putBoolean(setting.key, settingValue as Boolean)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getAppSetting(setting: AppSetting<T>): T =
        when (setting) {
            FirstTime -> getSetting(setting, setting.defaultValue as Boolean)
            Theme -> AppTheme.valueOf(getSetting(Theme, AppTheme.System.name))
            SnoozeLimit -> getSetting(setting, setting.defaultValue as Int)
            Confetti -> getSetting(setting, setting.defaultValue as Boolean)
        } as T

    override fun getAppTheme(): AppTheme =
        AppTheme.valueOf(getSetting(Theme, AppTheme.System.name))

    private inline fun <reified T> getSetting(setting: AppSetting<*>, defaultValue: T): T =
        when (setting) {
            FirstTime -> settings.getBoolean(setting.key, defaultValue as Boolean)
            Theme -> settings.getString(setting.key, defaultValue as String)
            SnoozeLimit -> settings.getInt(setting.key, defaultValue as Int)
            Confetti -> settings.getBoolean(setting.key, defaultValue as Boolean)
        } as T

}































