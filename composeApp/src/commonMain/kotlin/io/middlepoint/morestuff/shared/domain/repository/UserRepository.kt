package io.middlepoint.morestuff.shared.domain.repository

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.model.AppSettings

interface UserRepository {

    suspend fun <T> saveAppSetting(setting: AppSetting<T>, settingValue: T)

    suspend fun getAppSettings(default: AppSettings): AppSettings

    fun <T, R> getAppSetting(setting: AppSetting<T>): R

    fun getAppTheme(): AppTheme

}