package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.AppTheme
import io.middlepoint.morestuff.shared.domain.enums.Language

@Immutable
data class SettingsState(
  val appTheme: AppTheme = AppTheme.System,
  val snoozeLimit: Int = 0,
  val devSettings: Boolean = false,
  //val reviewTime: Pair<Int, Int> = Pair(9, 0),
  val inputVoiceLanguage: Language = Language.Device,
  val apiKey: String = ""
)

sealed class SettingsEvent {
  data class ChangeSnoozeLimit(val limit: Int) : SettingsEvent()
  data class SelectAppTheme(val index: Int) : SettingsEvent()
  data class EnableDevSettings(val enable: Boolean = true) : SettingsEvent()
  //data class SetReviewTime(val hour: Int, val minute: Int) : SettingsEvent()
  data class SelectLanguage(val index: Int) : SettingsEvent()
  data object OpenAppSettings : SettingsEvent()
  data class SetApiKey(val apiKey: String) : SettingsEvent()
  data object GetApiKey : SettingsEvent()
}