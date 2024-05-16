package io.middlepoint.morestuff.android.domain.nav

import io.middlepoint.morestuff.android.domain.model.ScopeDomain
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ScopeScreen : Parcelable {

    @Parcelize
    data object Root : ScopeScreen()

    @Parcelize
    data object Create : ScopeScreen()

    @Parcelize
    data class Edit(val scope: ScopeDomain) : ScopeScreen()

}