package co.softov.morestuff.android.domain.repository

import co.softov.morestuff.android.domain.enums.AppSetting
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.redux.state.AppSettings

interface UserRepository {

    suspend fun <T> saveAppSetting(setting: AppSetting<T>, settingValue: T)

    suspend fun getAppSettings(default: AppSettings): AppSettings

    fun <T> getAppSetting(setting: AppSetting<T>): T

    fun getAppTheme(): AppTheme

}