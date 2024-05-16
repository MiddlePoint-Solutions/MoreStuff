package io.middlepoint.morestuff.android.domain.repository

import io.middlepoint.morestuff.android.domain.enums.AppSetting
import io.middlepoint.morestuff.android.domain.enums.AppTheme
import io.middlepoint.morestuff.android.domain.redux.state.AppSettings

interface UserRepository {

    suspend fun <T> saveAppSetting(setting: AppSetting<T>, settingValue: T)

    suspend fun getAppSettings(default: AppSettings): AppSettings

    fun <T, R> getAppSetting(setting: AppSetting<T>): R

    fun getAppTheme(): AppTheme

}