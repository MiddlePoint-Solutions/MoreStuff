package co.softov.morestuff.android.data.repository

import co.softov.morestuff.android.data.key
import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.redux.state.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import com.russhwolf.settings.Settings


class UserRepositoryImpl(
    private val settings: Settings
) : UserRepository {

    override suspend fun getAppSettings(default: AppSettings) = with(default) {
        AppSettings(
            isFirstTime = getSetting(AppSetting.FirstTime, isFirstTime),
            appTheme = AppTheme.valueOf(getSetting(AppSetting.AppTheme, appTheme.name)),
            snoozeLimit = getSetting(AppSetting.SnoozeLimit, snoozeLimit),
            enableConfetti = getSetting(AppSetting.Confetti, enableConfetti)
        )
    }


    override suspend fun <T> saveAppSetting(setting: AppSetting, settingValue: T) {
        when (setting) {
            AppSetting.FirstTime -> settings.putBoolean(setting.key, settingValue as Boolean)
            AppSetting.AppTheme -> settings.putString(setting.key, settingValue as String)
            AppSetting.SnoozeLimit -> settings.putInt(setting.key, settingValue as Int)
            AppSetting.Confetti -> settings.putBoolean(setting.key, settingValue as Boolean)
        }
    }

    override fun getAppTheme(): AppTheme =
        AppTheme.valueOf(getSetting(AppSetting.AppTheme, AppTheme.System.name))

    private inline fun <reified T> getSetting(setting: AppSetting, defaultValue: T): T =
        when (setting) {
            AppSetting.FirstTime -> settings.getBoolean(setting.key, defaultValue as Boolean)
            AppSetting.AppTheme -> settings.getString(setting.key, defaultValue as String)
            AppSetting.SnoozeLimit -> settings.getInt(setting.key, defaultValue as Int)
            AppSetting.Confetti -> settings.getBoolean(setting.key, defaultValue as Boolean)
        } as T

}































