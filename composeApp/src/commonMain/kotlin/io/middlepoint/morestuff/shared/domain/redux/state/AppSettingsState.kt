package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.DevSettings
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.FirstTime
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.ReviewTime
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.SnoozeLimit
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.Theme
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction.InitSettings
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction.OnBoardingComplete
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction.SetAppTheme
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction.SetSnoozeLimit
import io.middlepoint.morestuff.shared.domain.redux.store.Action

data class AppSettings(
    val devSettings: Boolean = DevSettings.defaultValue,
    val isFirstTime: Boolean = FirstTime.defaultValue,
    val appTheme: AppTheme = AppTheme.valueOf(Theme.defaultValue),
    val snoozeLimit: Int = SnoozeLimit.defaultValue,
    val reviewTime: Pair<Int,Int> = ReviewTime.defaultValue,
    val enableReviewHint: Boolean = AppSetting.ShowHintArrowPriority.defaultValue,
    val voiceInputLanguage: Language = Language.valueOf(AppSetting.VoiceInputLanguage.defaultValue)
)

fun AppState.reduceSettingState(action: Action): AppState {
    return when (action) {
        is SettingAction -> copy(settings = settings.reduce(action))
        else -> this
    }
}

fun AppSettings.reduce(action: SettingAction): AppSettings {
    return when (action) {
        is InitSettings -> action.settings
        is SetSnoozeLimit -> copy(snoozeLimit = action.amount)
        is SetAppTheme -> copy(appTheme = action.theme)
        OnBoardingComplete -> copy(isFirstTime = false)
        is SettingAction.EnableDevSettings -> copy(devSettings = action.enable)
        is SettingAction.SetReviewTimeAction -> copy(reviewTime = action.hour to action.minute)
        is SettingAction.EnableReviewHint -> copy(enableReviewHint = action.enable)
        is SettingAction.SetVoiceLanguage -> copy(voiceInputLanguage = action.language)
    }
}