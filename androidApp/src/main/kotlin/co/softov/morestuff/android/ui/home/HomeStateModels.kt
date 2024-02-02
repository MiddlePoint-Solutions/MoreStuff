package co.softov.morestuff.android.ui.home

import co.softov.morestuff.android.domain.model.ScopeDomain
import kotlinx.serialization.Serializable

@Serializable
sealed class HomeScopeState {

    @Serializable
    data object Empty : HomeScopeState()

    @Serializable
    data class Data(val scopes: List<ScopeDomain>) : HomeScopeState()

}