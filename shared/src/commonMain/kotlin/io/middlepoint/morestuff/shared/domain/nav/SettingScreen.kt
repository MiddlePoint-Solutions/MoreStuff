package io.middlepoint.morestuff.shared.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingScreen : Parcelable {
  data object Root : SettingScreen()

  data object Developer : SettingScreen()

  data object Scopes : SettingScreen()

  data object AboutLibraries : SettingScreen()
}
