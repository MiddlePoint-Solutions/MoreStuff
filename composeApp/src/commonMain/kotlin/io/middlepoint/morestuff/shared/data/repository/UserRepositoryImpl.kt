package io.middlepoint.morestuff.shared.data.repository

import com.russhwolf.settings.Settings
import io.middlepoint.morestuff.shared.data.settingKey
import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.*
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.redux.state.AppSettings
import io.middlepoint.morestuff.shared.domain.repository.UserRepository


class UserRepositoryImpl(
    private val settings: Settings,
) : UserRepository {

    override suspend fun getAppSettings(default: AppSettings) = with(default) {
        AppSettings(
            devSettings = getSetting(DevSettings, devSettings),
            isFirstTime = getSetting(FirstTime, isFirstTime),
            appTheme = AppTheme.valueOf(getSetting(Theme, appTheme.name)),
            snoozeLimit = getSetting(SnoozeLimit, snoozeLimit),
            //reviewTime = getSetting(ReviewTime, reviewTime),
            enableReviewHint = getSetting(ShowHintArrowPriority, enableReviewHint),
            voiceInputLanguage = getSetting(VoiceInputLanguage, voiceInputLanguage)
        )
    }


    override suspend fun <T> saveAppSetting(setting: AppSetting<T>, settingValue: T) {
        when (setting) {
            FirstTime -> settings.putBoolean(setting.settingKey, settingValue as Boolean)
            Theme -> settings.putString(setting.settingKey, settingValue as String)
            SnoozeLimit -> settings.putInt(setting.settingKey, settingValue as Int)
            DevSettings -> settings.putBoolean(setting.settingKey, settingValue as Boolean)
            ReviewTime -> settings.putString(
                setting.settingKey,
                (settingValue as Pair<Int, Int>).let { "${it.first};${it.second}" }
            )
            ShowHintArrowPriority -> settings.putBoolean(setting.settingKey, settingValue as Boolean)
            is VoiceInputLanguage -> settings.putString(setting.settingKey, settingValue as String)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T, R> getAppSetting(setting: AppSetting<T>): R =
        when (setting) {
            FirstTime -> getSetting(setting, setting.defaultValue as Boolean) as R
            Theme -> AppTheme.valueOf(getSetting(Theme, AppTheme.System.name)) as R
            SnoozeLimit -> getSetting(setting, setting.defaultValue as Int) as R
            DevSettings -> getSetting(setting, setting.defaultValue as Boolean) as R
            ReviewTime -> getSetting(setting, setting.defaultValue as Pair<Int, Int>) as R
            ShowHintArrowPriority -> getSetting(setting, setting.defaultValue as Boolean) as R
            VoiceInputLanguage -> Language.valueOf(settings.getString(setting.settingKey, (setting.defaultValue as Language).name)) as R
        }

    override fun getAppTheme(): AppTheme =
        AppTheme.valueOf(getSetting(Theme, AppTheme.System.name))

    private inline fun <reified T> getSetting(setting: AppSetting<*>, defaultValue: T): T =
        when (setting) {
            FirstTime -> settings.getBoolean(setting.settingKey, defaultValue as Boolean) as T
            Theme -> settings.getString(setting.settingKey, defaultValue as String) as T
            SnoozeLimit -> settings.getInt(setting.settingKey, defaultValue as Int) as T
            DevSettings -> settings.getBoolean(setting.settingKey, defaultValue as Boolean) as T
            is ReviewTime -> {
                val timeStr = settings.getString(setting.settingKey, setting.defaultValue.toString())
                timeStr.toPairInt() ?: setting.defaultValue as T
            }
            ShowHintArrowPriority -> settings.getBoolean(setting.settingKey, defaultValue as Boolean) as T
            VoiceInputLanguage -> {
                val languageName = settings.getString(setting.settingKey, (defaultValue as Language).name)
                Language.valueOf(languageName)
            }
        } as T

}

fun String.toPairInt(): Pair<Int, Int>? {
    return this.split(";").let {
        if (it.size == 2) Pair(it[0].toInt(), it[1].toInt()) else null
    }
}































