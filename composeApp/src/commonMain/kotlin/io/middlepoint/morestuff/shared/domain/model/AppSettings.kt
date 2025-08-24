package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.domain.enums.AppSetting
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language

data class AppSettings(
  val devSettings: Boolean = AppSetting.DevSettings.defaultValue,
  val isFirstTime: Boolean = AppSetting.FirstTime.defaultValue,
  val appTheme: AppTheme = AppTheme.valueOf(AppSetting.Theme.defaultValue),
  val snoozeLimit: Int = AppSetting.SnoozeLimit.defaultValue,
  val reviewTime: Pair<Int,Int> = AppSetting.ReviewTime.defaultValue,
  val enableReviewHint: Boolean = AppSetting.ShowHintArrowPriority.defaultValue,
  val voiceInputLanguage: Language = Language.valueOf(AppSetting.VoiceInputLanguage.defaultValue)
)