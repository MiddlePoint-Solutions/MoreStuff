package co.softov.morestuff.android.domain.nav

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ScopeScreen : Parcelable {

    @Parcelize
    data object Root : ScopeScreen()

    @Parcelize
    data object Create : ScopeScreen()

}