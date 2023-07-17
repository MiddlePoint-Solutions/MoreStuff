package co.softov.morestuff.android.data.repository

import co.softov.morestuff.android.data.Constants
import co.softov.morestuff.android.data.key
import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.model.AppSettings
import co.softov.morestuff.android.domain.repository.UserRepository
import com.russhwolf.settings.Settings


class UserRepositoryImpl(
    private val settings: Settings
) : UserRepository {

    override suspend fun getAppSettings(default: AppSettings): AppSettings {
        return AppSettings(
            isFirstTime = getSetting(AppSetting.FirstTime, default.isFirstTime),
            appTheme = AppTheme.valueOf(getSetting(AppSetting.AppTheme, default.appTheme.name)),
            snoozeLimit = getSetting(AppSetting.SnoozeLimit, default.snoozeLimit),
        )
    }

    override suspend fun <T> saveAppSetting(setting: AppSetting, settingValue: T) {
        when (setting) {
            AppSetting.FirstTime -> settings.putBoolean(setting.key, settingValue as Boolean)
            AppSetting.AppTheme -> settings.putString(setting.key, settingValue as String)
            AppSetting.SnoozeLimit -> settings.putInt(setting.key, settingValue as Int)
        }
    }

    override fun getAppTheme(): AppTheme =
        AppTheme.valueOf(getSetting(AppSetting.AppTheme, AppTheme.System.name))

    private inline fun <reified T> getSetting(setting: AppSetting, defaultValue: T): T =
        when (setting) {
            AppSetting.FirstTime -> settings.getBoolean(setting.key, defaultValue as Boolean)
            AppSetting.AppTheme -> settings.getString(setting.key, defaultValue as String)
            AppSetting.SnoozeLimit -> settings.getInt(setting.key, defaultValue as Int)
        } as T

}































