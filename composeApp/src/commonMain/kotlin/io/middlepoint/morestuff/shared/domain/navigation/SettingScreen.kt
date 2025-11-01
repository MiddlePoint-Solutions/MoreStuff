package io.middlepoint.morestuff.shared.domain.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class SettingScreen: AppRoute {
  @Serializable
  data object Root : SettingScreen()

  @Serializable
  data object Developer : SettingScreen()

  @Serializable
  data object AboutLibraries : SettingScreen()
}
