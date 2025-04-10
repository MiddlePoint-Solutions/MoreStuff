package io.middlepoint.morestuff.shared.domain.redux.state

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.DevSettings
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.FirstTime
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.ReviewTime
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.SnoozeLimit
import io.middlepoint.morestuff.shared.domain.enums.AppSetting.Theme
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language
import io.middlepoint.morestuff.shared.domain.enums.Status
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction
import io.middlepoint.morestuff.shared.domain.redux.action.SettingAction.*
import io.middlepoint.morestuff.shared.domain.redux.store.Action

data class AppSettingsState(
  val status: Status = Status.Loading,
  val devSettings: Boolean = DevSettings.defaultValue,
  val isFirstTime: Boolean = FirstTime.defaultValue,
  val appTheme: AppTheme = AppTheme.valueOf(Theme.defaultValue),
  val snoozeLimit: Int = SnoozeLimit.defaultValue,
  val reviewTime: Pair<Int, Int> = ReviewTime.defaultValue,
  val enableReviewHint: Boolean = AppSetting.ShowHintArrowPriority.defaultValue,
  val voiceInputLanguage: Language = Language.valueOf(AppSetting.VoiceInputLanguage.defaultValue)
)

fun AppState.reduceSettingState(action: Action): AppState {
  return when (action) {
    is SettingAction -> copy(settings = settings.reduce(action))
    else -> this
  }
}

fun AppSettingsState.reduce(action: SettingAction): AppSettingsState {
  return when (action) {
    is InitSettings -> initSettings(action)
    is SetSnoozeLimit -> copy(snoozeLimit = action.amount)
    is SetAppTheme -> copy(appTheme = action.theme)
    is OnBoardingComplete -> copy(isFirstTime = false)
    is EnableDevSettings -> copy(devSettings = action.enable)
    is SetReviewTimeAction -> copy(reviewTime = action.hour to action.minute)
    is EnableReviewHint -> copy(enableReviewHint = action.enable)
    is SetVoiceLanguage -> copy(voiceInputLanguage = action.language)
  }
}

private fun AppSettingsState.initSettings(action: InitSettings) =
  action.settings.let { cached ->
    copy(
      status = Status.Ready,
      devSettings = cached.devSettings,
      isFirstTime = cached.isFirstTime,
      appTheme = cached.appTheme,
      snoozeLimit = cached.snoozeLimit,
      reviewTime = cached.reviewTime,
      enableReviewHint = cached.enableReviewHint,
      voiceInputLanguage = cached.voiceInputLanguage
    )
  }