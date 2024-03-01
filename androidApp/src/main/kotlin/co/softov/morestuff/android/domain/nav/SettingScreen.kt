package co.softov.morestuff.android.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
sealed class SettingScreen : Parcelable {
    @Parcelize
    data object Root : SettingScreen()
    @Parcelize
    data object Developer : SettingScreen()
    @Parcelize
    data object Scopes : SettingScreen()
    @Parcelize
    data object AboutLibraries : SettingScreen()
}
