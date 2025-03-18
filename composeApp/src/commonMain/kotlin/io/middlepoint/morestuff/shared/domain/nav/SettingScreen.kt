package io.middlepoint.morestuff.shared.domain.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class SettingScreen {
  @Serializable
  data object Root : SettingScreen()

  @Serializable
  data object Developer : SettingScreen()

  @Serializable
  data object Scopes : SettingScreen()

  @Serializable
  data object AboutLibraries : SettingScreen()
}
